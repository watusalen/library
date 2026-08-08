package com.matusalenalves.library.dto.response;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Envelope padronizado de listagem paginada (RNF12), devolvido por qualquer
 * endpoint de listagem que suporte paginação (ver seção 9 do documento de
 * requisitos).
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {

    /**
     * Constrói um {@link PageResponse} a partir de uma {@link Page} do
     * Spring Data já convertida para o DTO de resposta desejado.
     *
     * @param page página, já convertida para o tipo de resposta {@code T}
     * @param <T>  tipo do DTO de resposta contido na página
     * @return o envelope padronizado correspondente à página informada
     */
    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}