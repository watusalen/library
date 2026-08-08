package com.matusalenalves.library.services;

import com.matusalenalves.library.dto.request.BookRequest;
import com.matusalenalves.library.dto.response.BookResponse;
import com.matusalenalves.library.dto.response.PageResponse;
import com.matusalenalves.library.entities.Author;
import com.matusalenalves.library.entities.Book;
import com.matusalenalves.library.entities.Category;
import com.matusalenalves.library.entities.enums.LoanStatus;
import com.matusalenalves.library.mapper.BookMapper;
import com.matusalenalves.library.repositories.AuthorRepository;
import com.matusalenalves.library.repositories.BookRepository;
import com.matusalenalves.library.repositories.CategoryRepository;
import com.matusalenalves.library.repositories.LoanRepository;
import com.matusalenalves.library.services.exceptions.BusinessRuleException;
import com.matusalenalves.library.services.exceptions.DataBaseException;
import com.matusalenalves.library.services.exceptions.ResourceNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Regras de negócio para o acervo de livros: cadastro (RF04), edição (RF05),
 * exclusão (RF06) e consulta (RF07-RF09).
 */
@Service
public class BookService {
    private final BookRepository bookRepository;

    private final AuthorRepository authorRepository;

    private final CategoryRepository categoryRepository;

    private final LoanRepository loanRepository;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository, CategoryRepository categoryRepository, LoanRepository loanRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
        this.loanRepository = loanRepository;
    }

    /**
     * Consulta o acervo combinando filtros de título, autor e categoria,
     * paginado (RF09, RNF12).
     *
     * @param title      trecho do título a ser buscado, ou {@code null} para não filtrar por título
     * @param authorId   identificador do autor, ou {@code null} para não filtrar por autor
     * @param categoryId identificador da categoria, ou {@code null} para não filtrar por categoria
     * @param pageable   página, tamanho e ordenação solicitados
     * @return a página de livros que atendem aos filtros informados, convertida para o DTO de resposta
     */
    @Transactional(readOnly = true)
    public PageResponse<BookResponse> search(String title, Long authorId, Long categoryId, Pageable pageable) {
        return PageResponse.of(bookRepository.search(title, authorId, categoryId, pageable).map(BookMapper::toResponse));
    }

    /**
     * Busca um livro pelo id (RF08).
     *
     * @param id identificador do livro
     * @return o livro correspondente, convertido para o DTO de resposta
     * @throws ResourceNotFoundException se não existir livro com esse id
     */
    @Transactional(readOnly = true)
    public BookResponse findById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
        return BookMapper.toResponse(book);
    }

    /**
     * Cadastra um novo livro (RF04).
     * <p>
     * A quantidade disponível é inicializada igual à quantidade total de
     * exemplares, conforme feito pelo construtor de {@link Book}.
     *
     * @param request dados do livro a ser cadastrado
     * @return o livro criado, convertido para o DTO de resposta
     * @throws ResourceNotFoundException se o autor informado não existir
     * @throws BusinessRuleException     se alguma categoria informada não existir
     */
    @Transactional
    public BookResponse create(BookRequest request) {
        Author author = resolveAuthor(request.authorId());
        Set<Category> categories = resolveCategories(request);
        Book book = BookMapper.toEntity(request, author, categories);
        return BookMapper.toResponse(bookRepository.save(book));
    }

    /**
     * Edita os dados de um livro existente (RF05).
     *
     * @param id      identificador do livro a ser editado
     * @param request novos dados do livro
     * @return o livro atualizado, convertido para o DTO de resposta
     * @throws ResourceNotFoundException se não existir livro com esse id, ou se o autor informado não existir
     * @throws BusinessRuleException     se alguma categoria informada não existir
     */
    @Transactional
    public BookResponse update(Long id, BookRequest request) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
        book.setTitle(request.title());
        book.setIsbn(request.isbn());
        book.setPublicationYear(request.publicationYear());
        book.setTotalCopies(request.totalCopies());
        book.setAuthor(resolveAuthor(request.authorId()));
        book.setCategories(resolveCategories(request));
        return BookMapper.toResponse(bookRepository.save(book));
    }

    /**
     * Exclui um livro (RF06).
     * <p>
     * A RN10 é verificada de duas formas: primeiro checando explicitamente
     * se há empréstimo ativo vinculado; e, como rede de segurança contra
     * condições de corrida, capturando também a
     * {@link DataIntegrityViolationException} que o banco lançaria caso um
     * vínculo tenha sido criado entre a checagem e a exclusão.
     *
     * @param id identificador do livro a ser excluído
     * @throws ResourceNotFoundException se não existir livro com esse id
     * @throws BusinessRuleException     se houver empréstimo ativo vinculado ao livro
     * @throws DataBaseException         se a exclusão violar integridade referencial no banco
     */
    @Transactional
    public void delete(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }
        if (loanRepository.existsByBookIdAndStatus(id, LoanStatus.ACTIVE)) {
            throw new BusinessRuleException("Cannot delete book with id " + id + ": there are loans linked to this book.");
        }
        try {
            bookRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    /**
     * Busca o autor referenciado pelo request, garantindo que ele exista
     * antes de vincular o livro a ele.
     *
     * @param authorId identificador do autor
     * @return o autor correspondente
     * @throws ResourceNotFoundException se não existir autor com esse id
     */
    private Author resolveAuthor(Long authorId) {
        return authorRepository.findById(authorId).orElseThrow(() -> new ResourceNotFoundException(authorId));
    }

    /**
     * Busca as categorias referenciadas pelo request, garantindo que todas
     * existam antes de vincular o livro a elas.
     *
     * @param request dados do livro, contendo os ids das categorias
     * @return as categorias correspondentes
     * @throws BusinessRuleException se algum id de categoria não corresponder a uma categoria existente
     */
    private Set<Category> resolveCategories(BookRequest request) {
        Set<Category> categories = categoryRepository.findAllById(request.categoryIds())
                .stream()
                .collect(Collectors.toSet());

        if (request.categoryIds().size() > categories.size()) {
            Set<Long> foundIds = categories.stream()
                    .map(category -> category.getId())
                    .collect(Collectors.toSet());

            Set<Long> missingIds = request.categoryIds().stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toSet());

            throw new BusinessRuleException("Categories not found: " + missingIds);
        }

        return categories;
    }
}