package com.matusalenalves.library.dto.response;

import java.util.Set;

/**
 * Representação de um livro retornada pela API (RF07-RF09), isolando a
 * entidade JPA {@code Book} do contrato público.
 */
public record BookResponse(

        Long id,
        String title,
        String isbn,
        Integer publicationYear,
        Integer totalCopies,
        Integer availableCopies,
        AuthorSummary author,
        Set<CategorySummary> categories

) {

    /**
     * Projeção resumida do autor do livro, com apenas id e nome — evita
     * expor a lista completa de livros do autor dentro da resposta do livro.
     */
    public record AuthorSummary(Long id, String name) {
    }

    /**
     * Projeção resumida de uma categoria do livro, com apenas id e nome —
     * pelo mesmo motivo de {@link AuthorSummary}.
     */
    public record CategorySummary(Long id, String name) {
    }
}