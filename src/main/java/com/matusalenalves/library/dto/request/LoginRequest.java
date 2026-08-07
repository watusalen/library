package com.matusalenalves.library.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Dados de entrada para autenticação (RF02).
 */
public record LoginRequest(
        @NotBlank(message = "Email must not be blank")
        String email,

        @NotBlank(message = "Password must not be blank")
        String password

) {
}
