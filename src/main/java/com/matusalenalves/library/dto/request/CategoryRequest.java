package com.matusalenalves.library.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Dados de entrada para cadastro (RF14) e edição (RF15) de uma categoria.
 */
public record CategoryRequest(
        @NotBlank(message = "Category's name is required")
        @Size(min = 2, max = 100, message = "Category's name must be between 2 and 100 characters")
        String name
) {
}