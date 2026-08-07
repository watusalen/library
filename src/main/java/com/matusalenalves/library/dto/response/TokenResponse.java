package com.matusalenalves.library.dto.response;

/**
 * Token de acesso emitido após um login bem-sucedido (RF02).
 */
public record TokenResponse(String token) {
}