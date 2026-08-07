package com.matusalenalves.library.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * Dados de entrada para registrar um empréstimo (RF18).
 * <p>
 * Apenas o livro é informado pelo cliente — o usuário vem do contexto de
 * autenticação, e as demais informações (datas, status) são calculadas
 * automaticamente pelo sistema (RN02).
 */
public record LoanRequest(
        @NotNull(message = "Book id must not be null")
        Long bookId
) {
}
