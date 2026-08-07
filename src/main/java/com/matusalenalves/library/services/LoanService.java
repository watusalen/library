package com.matusalenalves.library.services;

import com.matusalenalves.library.dto.request.LoanRequest;
import com.matusalenalves.library.dto.response.LoanResponse;
import com.matusalenalves.library.entities.Book;
import com.matusalenalves.library.entities.Loan;
import com.matusalenalves.library.entities.User;
import com.matusalenalves.library.entities.enums.LoanStatus;
import com.matusalenalves.library.mapper.LoanMapper;
import com.matusalenalves.library.repositories.BookRepository;
import com.matusalenalves.library.repositories.LoanRepository;
import com.matusalenalves.library.repositories.UserRepository;
import com.matusalenalves.library.services.exceptions.BusinessRuleException;
import com.matusalenalves.library.services.exceptions.LoanAccessDeniedException;
import com.matusalenalves.library.services.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Regras de negócio para empréstimos: registro (RF18), devolução (RF19) e
 * consulta de histórico, tanto do próprio cliente (RF20) quanto geral, para
 * administradores (RF21).
 */
@Service
public class LoanService {
    private final LoanRepository loanRepository;

    private final BookRepository bookRepository;

    private final UserRepository userRepository;

    public LoanService(LoanRepository loanRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    /**
     * Lista o histórico de empréstimos de um usuário (RF20).
     *
     * @param id identificador do usuário.
     * @return empréstimos do usuário, convertidos para o DTO de resposta.
     */
    @Transactional(readOnly = true)
    public List<LoanResponse> findMyLoans(Long id) {
        return toResponseList(loanRepository.findByUserId(id));
    }

    /**
     * Lista o histórico de empréstimos de todos os usuários (RF21).
     *
     * @return todos os empréstimos, convertidos para o DTO de resposta.
     */
    @Transactional(readOnly = true)
    public List<LoanResponse> findAll() {
        return toResponseList(loanRepository.findAll());
    }

    /**
     * Registra o empréstimo de um livro para um cliente (RF18).
     * <p>
     * A data prevista de devolução (RN02) é calculada automaticamente pelo
     * construtor de {@link Loan}, chamado por {@link LoanMapper#toEntity}.
     *
     * @param id      identificador do cliente que está pegando o livro emprestado.
     * @param request dados do empréstimo, contendo o livro desejado.
     * @return o empréstimo criado, convertido para o DTO de resposta.
     * @throws ResourceNotFoundException se o usuário ou o livro informado não existir.
     * @throws BusinessRuleException     se o livro não tiver exemplar disponível (RN01),
     *                                   ou se o cliente tiver algum empréstimo em atraso (RN04).
     */
    @Transactional
    public LoanResponse create(Long id, LoanRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
        Book book = bookRepository.findById(request.bookId()).orElseThrow(() -> new ResourceNotFoundException(request.bookId()));

        if (!book.isAvailable()) {
            throw new BusinessRuleException("Book with id " + book.getId() + " has no available copies.");
        }

        boolean hasOverdueLoan = loanRepository.findByUserIdAndStatus(id, LoanStatus.ACTIVE)
                .stream()
                .anyMatch(loan -> loan.isOverdue());

        if (hasOverdueLoan) {
            throw new BusinessRuleException("User with id " + id + " has overdue loans and cannot borrow another book.");
        }

        book.decreaseAvailableCopies();
        Loan loan = LoanMapper.toEntity(user, book);
        bookRepository.save(book);
        return LoanMapper.toResponse(loanRepository.save(loan));
    }

    /**
     * Registra a devolução de um empréstimo (RF19).
     * <p>
     * Apenas o cliente dono do empréstimo pode devolvê-lo — exceto o
     * administrador, que pode devolver qualquer empréstimo (RN09).
     *
     * @param loanId  identificador do empréstimo a ser devolvido.
     * @param userId  identificador do usuário autenticado que está solicitando a devolução.
     * @param isAdmin se o usuário autenticado tem perfil {@code ADMIN}.
     * @return o empréstimo devolvido, convertido para o DTO de resposta.
     * @throws ResourceNotFoundException  se não existir empréstimo com esse id.
     * @throws LoanAccessDeniedException  se o usuário não for o dono do empréstimo nem admin (RN09).
     * @throws IllegalStateException      se o empréstimo já tiver sido devolvido — ver
     *                                    {@link Loan#markAsReturned()}.
     */
    @Transactional
    public LoanResponse returnLoan(Long loanId, Long userId, boolean isAdmin) {
        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new ResourceNotFoundException(loanId));

        if (!loan.getUser().getId().equals(userId) && !isAdmin) {
            throw new LoanAccessDeniedException("User with id " + userId + " is not allowed to return loan with id " + loanId + ".");
        }

        loan.markAsReturned();
        loan.getBook().increaseAvailableCopies();
        bookRepository.save(loan.getBook());
        return LoanMapper.toResponse(loanRepository.save(loan));
    }

    /**
     * Converte uma lista de empréstimos para o DTO de resposta, evitando
     * repetir o mesmo {@code stream().map(...).toList()} em cada consulta.
     *
     * @param loans empréstimos a serem convertidos.
     * @return os empréstimos convertidos para o DTO de resposta.
     */
    private List<LoanResponse> toResponseList(List<Loan> loans) {
        return loans.stream().map(loan -> LoanMapper.toResponse(loan)).toList();
    }
}
