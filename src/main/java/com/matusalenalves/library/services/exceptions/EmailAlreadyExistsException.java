package com.matusalenalves.library.services.exceptions;

import java.io.Serial;

/**
 * Lançada ao cadastrar um usuário com um e-mail já registrado (RF03),
 * já que o e-mail é único no sistema e funciona como identificador de
 * login (RN07).
 * <p>
 * Deve ser traduzida pela camada {@code controller} em uma resposta
 * HTTP 409 Conflict.
 */
public class EmailAlreadyExistsException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public EmailAlreadyExistsException(String email) {
        super("Email already registered: " + email);
    }
}