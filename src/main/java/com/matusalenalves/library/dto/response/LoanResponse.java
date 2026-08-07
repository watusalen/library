package com.matusalenalves.library.dto.response;

import com.matusalenalves.library.entities.enums.LoanStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Representação de um empréstimo retornada pela API (RF20, RF21), isolando
 * a entidade JPA {@code Loan} do contrato público.
 */
public record LoanResponse(

        Long id,
        LocalDateTime loanDate,
        LocalDate dueDate,
        LocalDateTime returnDate,
        LoanStatus status,
        UserSummary user,
        BookSummary book

) {

    /**
     * Projeção resumida do usuário do empréstimo, com apenas id, nome e
     * e-mail — evita expor os demais dados do usuário dentro da resposta
     * do empréstimo.
     */
    public record UserSummary(Long id, String name, String email) {
    }

    /**
     * Projeção resumida do livro do empréstimo, com apenas os dados
     * suficientes para identificá-lo — evita repetir a resposta completa
     * de {@link BookResponse} dentro da resposta do empréstimo.
     */
    public record BookSummary(Long id, String title, String isbn, Integer publicationYear) {
    }
}
