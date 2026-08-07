package com.matusalenalves.library.mapper;

import com.matusalenalves.library.dto.response.LoanResponse;
import com.matusalenalves.library.entities.Book;
import com.matusalenalves.library.entities.Loan;
import com.matusalenalves.library.entities.User;

/**
 * Conversão entre {@link Loan} e seus DTOs de request/response.
 */
public class LoanMapper {

    /**
     * Converte a entidade em seu DTO de saída, projetando usuário e livro
     * apenas com os dados suficientes para identificá-los (ver
     * {@link LoanResponse.UserSummary} e {@link LoanResponse.BookSummary}).
     *
     * @param loan entidade a ser convertida.
     * @return DTO com os dados públicos do empréstimo.
     */
    public static LoanResponse toResponse(Loan loan) {
        LoanResponse.UserSummary userSummary = new LoanResponse.UserSummary(
                loan.getUser().getId(),
                loan.getUser().getName(),
                loan.getUser().getEmail()
        );

        LoanResponse.BookSummary bookSummary = new LoanResponse.BookSummary(
                loan.getBook().getId(),
                loan.getBook().getTitle(),
                loan.getBook().getIsbn(),
                loan.getBook().getPublicationYear()
        );

        return new LoanResponse(
                loan.getId(),
                loan.getLoanDate(),
                loan.getDueDate(),
                loan.getReturnDate(),
                loan.getStatus(),
                userSummary,
                bookSummary
        );
    }

    /**
     * Cria uma nova entidade {@link Loan} para o usuário e livro informados,
     * ainda sem id (a ser gerado na persistência). Delegado ao construtor de
     * {@link Loan}, que já inicializa data do empréstimo, prazo de devolução
     * (RN02) e situação inicial ({@code ACTIVE}).
     *
     * @param user usuário que está realizando o empréstimo.
     * @param book livro a ser emprestado.
     * @return entidade correspondente, pronta para ser salva.
     */
    public static Loan toEntity(User user, Book book) {
        return new Loan(null, book, user);
    }
}
