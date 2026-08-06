package com.matusalenalves.library.mapper;

import com.matusalenalves.library.dto.request.CategoryRequest;
import com.matusalenalves.library.dto.response.CategoryResponse;
import com.matusalenalves.library.entities.Category;

/**
 * Conversão entre {@link Category} e seus DTOs de request/response.
 */
public class CategoryMapper {

    /**
     * Converte os dados de entrada em uma nova entidade {@link Category},
     * ainda sem id (a ser gerado na persistência).
     *
     * @param request dados de cadastro/edição da categoria
     * @return entidade correspondente, pronta para ser salva
     */
    public static Category toEntity(CategoryRequest request) {
        return new Category(null, request.name());
    }

    /**
     * Converte a entidade em seu DTO de saída, para retorno pela API.
     *
     * @param category entidade a ser convertida
     * @return DTO com os dados públicos da categoria
     */
    public static CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName());
    }
}