package com.matusalenalves.library.dto.response;

/**
 * Representação de um autor retornada pela API (RF10-RF13), isolando a
 * entidade JPA {@code Author} do contrato público.
 */
public record AuthorResponse(
        Long id,
        String name
) {
}