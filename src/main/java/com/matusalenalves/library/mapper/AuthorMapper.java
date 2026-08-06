package com.matusalenalves.library.mapper;

import com.matusalenalves.library.dto.request.AuthorRequest;
import com.matusalenalves.library.dto.response.AuthorResponse;
import com.matusalenalves.library.entities.Author;

/**
 * Conversão entre {@link Author} e seus DTOs de request/response.
 */
public class AuthorMapper {

    /**
     * Converte os dados de entrada em uma nova entidade {@link Author},
     * ainda sem id (a ser gerado na persistência).
     *
     * @param request dados de cadastro/edição do autor
     * @return entidade correspondente, pronta para ser salva
     */
    public static Author toEntity(AuthorRequest request) {
        return new Author(null, request.name());
    }

    /**
     * Converte a entidade em seu DTO de saída, para retorno pela API.
     *
     * @param author entidade a ser convertida
     * @return DTO com os dados públicos do autor
     */
    public static AuthorResponse toResponse(Author author) {
        return new AuthorResponse(author.getId(), author.getName());
    }
}