package com.matusalenalves.library.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Dados de entrada para cadastro (RF10) e edição (RF11) de um autor.
 */
public record AuthorRequest(
        @NotBlank(message = "Author's name is required.")
        @Size(min = 2, max = 150, message = "Author's name must be between 2 and 150 characters long.")
        String name
) {
}