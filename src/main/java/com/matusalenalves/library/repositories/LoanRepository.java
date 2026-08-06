package com.matusalenalves.library.repositories;

import com.matusalenalves.library.entities.Loan;
import com.matusalenalves.library.entities.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Acesso a dados de {@link Loan}.
 * <p>
 * Além das operações de CRUD herdadas de {@link JpaRepository}, concentra as
 * consultas usadas para montar histórico de empréstimos (RF20, RF21), para
 * verificar pendências em atraso antes de um novo empréstimo (RN04) e para
 * checar empréstimo ativo antes de excluir um livro (RN10).
 */
@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    /**
     * Busca todos os empréstimos de um usuário, independentemente da situação.
     * <p>
     * Usado pelo histórico de empréstimos do próprio cliente (RF20).
     *
     * @param id identificador do usuário
     * @return empréstimos associados ao usuário informado
     */
    List<Loan> findByUserId(Long id);

    /**
     * Busca os empréstimos de um usuário filtrando por situação.
     * <p>
     * Utilizado, em conjunto com {@link com.matusalenalves.library.entities.Loan#isOverdue()},
     * para verificar se um cliente possui empréstimos em atraso antes de
     * autorizar um novo empréstimo (RN04). Como {@link LoanStatus#OVERDUE} nunca
     * é persistido — o atraso é sempre calculado em tempo real, nunca gravado no
     * banco —, este método deve ser chamado com {@link LoanStatus#ACTIVE}, e o
     * resultado percorrido para verificar se algum item está atrasado.
     *
     * @param id     identificador do usuário.
     * @param status situação do empréstimo a ser filtrada.
     * @return empréstimos do usuário que estejam na situação informada.
     */
    List<Loan> findByUserIdAndStatus(Long id, LoanStatus status);

    /**
     * Verifica se existe algum empréstimo de um livro na situação informada.
     * <p>
     * Usado antes de excluir um livro, para impedir a exclusão enquanto
     * houver empréstimo ativo (RN10) — deve ser chamado com
     * {@link LoanStatus#ACTIVE}.
     *
     * @param bookId identificador do livro
     * @param status situação do empréstimo a ser filtrada
     * @return {@code true} se houver ao menos um empréstimo do livro nessa situação
     */
    boolean existsByBookIdAndStatus(Long bookId, LoanStatus status);
}