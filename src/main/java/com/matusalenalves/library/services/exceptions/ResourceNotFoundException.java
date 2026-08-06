package com.matusalenalves.library.services.exceptions;

import java.io.Serial;

/**
 * Lançada quando um recurso não é encontrado pelo id informado (ex.: buscar,
 * editar ou excluir um autor/categoria/livro/empréstimo inexistente).
 * <p>
 * Deve ser traduzida pela camada {@code controller} em uma resposta
 * HTTP 404 Not Found.
 */
public class ResourceNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(Object id) {
        super("Resource not found. Id " + id);
    }
}