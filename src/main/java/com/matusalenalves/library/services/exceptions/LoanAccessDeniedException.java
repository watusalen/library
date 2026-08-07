package com.matusalenalves.library.services.exceptions;

import java.io.Serial;

/**
 * Lançada quando um cliente tenta devolver um empréstimo que não é seu
 * (RF19), já que apenas o dono do empréstimo ou um administrador podem
 * fazer isso (RN09).
 * <p>
 * Deve ser traduzida pela camada {@code controller} em uma resposta
 * HTTP 403 Forbidden.
 */
public class LoanAccessDeniedException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public LoanAccessDeniedException(String message) {
        super(message);
    }
}