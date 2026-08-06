package com.matusalenalves.library.dto.request;

import jakarta.validation.constraints.*;

import java.util.Set;

/**
 * Dados de entrada para cadastro (RF04) e edição (RF05) de um livro.
 * <p>
 * {@code authorId} e {@code categoryIds} referenciam registros que devem
 * existir previamente — a resolução para as entidades reais acontece no
 * {@code BookService}, não neste DTO.
 */
public record BookRequest(
        @NotBlank(message = "Title must not be blank")
        @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
        String title,

        @NotBlank(message = "ISBN must not be blank")
        String isbn,

        @NotNull(message = "Publication year must not be null")
        Integer publicationYear,

        @NotNull(message = "Total copies must not be null")
        @Min(value = 1, message = "Total copies must be at least 1")
        Integer totalCopies,

        @NotNull(message = "Author id must not be null")
        Long authorId,

        @NotEmpty(message = "At least one category is required")
        Set<Long> categoryIds

) {
}