package com.matusalenalves.library.entities.enums;

/**
 * Situação de um {@link com.matusalenalves.library.entities.Loan} (RN03).
 * <p>
 * Não existe uma transição automática para {@link #OVERDUE}: o atraso é
 * calculado sob demanda por {@code Loan.isOverdue()}, comparando a data
 * prevista de devolução com a data atual enquanto o status ainda é
 * {@link #ACTIVE}.
 */
public enum LoanStatus {

    /**
     * Empréstimo em andamento, ainda não devolvido.
     */
    ACTIVE,

    /**
     * Empréstimo já devolvido pelo cliente.
     */
    RETURNED,

    /**
     * Empréstimo cuja data prevista de devolução já passou.
     */
    OVERDUE
}