package com.matusalenalves.library.dto.response;

/**
 * Representação de uma categoria retornada pela API (RF14-RF17), isolando a
 * entidade JPA {@code Category} do contrato público.
 */
public record CategoryResponse(
        Long id,
        String name
) {
}
