package com.matusalenalves.library.mapper;

import com.matusalenalves.library.dto.request.BookRequest;
import com.matusalenalves.library.dto.response.BookResponse;
import com.matusalenalves.library.entities.Author;
import com.matusalenalves.library.entities.Book;
import com.matusalenalves.library.entities.Category;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Conversão entre {@link Book} e seus DTOs de request/response.
 */
public class BookMapper {

    /**
     * Converte a entidade em seu DTO de saída, projetando autor e
     * categorias apenas com id e nome (ver {@link BookResponse.AuthorSummary}
     * e {@link BookResponse.CategorySummary}).
     *
     * @param book entidade a ser convertida
     * @return DTO com os dados públicos do livro
     */
    public static BookResponse toResponse(Book book) {
        BookResponse.AuthorSummary authorSummary =
                new BookResponse.AuthorSummary(book.getAuthor().getId(), book.getAuthor().getName());

        Set<BookResponse.CategorySummary> categorySummaries = book.getCategories().stream()
                .map(category -> new BookResponse.CategorySummary(category.getId(), category.getName()))
                .collect(Collectors.toSet());

        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getPublicationYear(),
                book.getTotalCopies(),
                book.getAvailableCopies(),
                authorSummary,
                categorySummaries
        );
    }

    /**
     * Converte os dados de entrada em uma nova entidade {@link Book}, ainda
     * sem id (a ser gerado na persistência).
     * <p>
     * Recebe {@code author} e {@code categories} já resolvidos, em vez dos
     * ids brutos de {@code request} — a busca desses registros no banco (e
     * o erro 404 caso não existam) é responsabilidade do {@code BookService},
     * não deste mapper.
     *
     * @param request    dados de cadastro/edição do livro
     * @param author     autor já carregado, correspondente a {@code request.authorId()}
     * @param categories categorias já carregadas, correspondentes a {@code request.categoryIds()}
     * @return entidade correspondente, pronta para ser salva
     */
    public static Book toEntity(BookRequest request, Author author, Set<Category> categories) {
        Book book = new Book(
                null,
                request.title(),
                request.isbn(),
                request.publicationYear(),
                request.totalCopies(),
                author
        );
        book.setCategories(categories);
        return book;
    }
}