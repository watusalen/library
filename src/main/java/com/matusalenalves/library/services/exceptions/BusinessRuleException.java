package com.matusalenalves.library.services.exceptions;

import java.io.Serial;

/**
 * Lançada quando uma operação viola uma regra de negócio do domínio
 * (ex.: RN05, RN06, RN10 — exclusão bloqueada por vínculo existente).
 * <p>
 * Deve ser traduzida pela camada {@code controller} em uma resposta
 * HTTP 409 Conflict.
 */
public class BusinessRuleException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public BusinessRuleException(String message) {
        super(message);
    }
}