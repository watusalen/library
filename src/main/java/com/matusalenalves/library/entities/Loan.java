package com.matusalenalves.library.entities;

import com.matusalenalves.library.entities.enums.LoanStatus;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Empréstimo de um {@link Book} para um {@link User}.
 * <p>
 * O prazo de devolução ({@code dueDate}) é calculado automaticamente no
 * momento do empréstimo, 14 dias corridos após {@code loanDate} (RN02).
 */
@Entity
@Table(name = "loan")
public class Loan implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, updatable = false)
    private LocalDateTime loanDate;
    @Column(nullable = false)
    private LocalDate dueDate;
    @Column(nullable = true)
    private LocalDateTime returnDate;
    @Column(length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    public Loan() {
    }

    /**
     * Cria um novo empréstimo já no estado inicial esperado pela regra de
     * negócio: situação {@link LoanStatus#ACTIVE}, data do empréstimo igual
     * ao momento atual e prazo de devolução calculado automaticamente para
     * 14 dias corridos depois (RN02).
     *
     * @param id   identificador do empréstimo
     * @param book livro emprestado
     * @param user cliente que está realizando o empréstimo
     */
    public Loan(Long id, Book book, User user) {
        this.id = id;
        this.book = book;
        this.status = LoanStatus.ACTIVE;
        this.user = user;
        loanDate = LocalDateTime.now();
        dueDate = loanDate.plusDays(14).toLocalDate();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(LocalDateTime loanDate) {
        this.loanDate = loanDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    /**
     * Indica se o empréstimo está em atraso (RN03).
     * <p>
     * O status {@link LoanStatus#OVERDUE} não é persistido automaticamente;
     * este método apenas calcula o atraso sob demanda, comparando a data
     * prevista de devolução com a data atual enquanto o empréstimo ainda
     * está {@link LoanStatus#ACTIVE}.
     *
     * @return {@code true} se o empréstimo estiver ativo e a data prevista
     *         de devolução já tiver passado
     */
    public boolean isOverdue() {
        return status.equals(LoanStatus.ACTIVE) && dueDate.isBefore(LocalDate.now());
    }

    /**
     * Registra a devolução do empréstimo (RF19), marcando a data efetiva
     * de devolução e atualizando a situação para {@link LoanStatus#RETURNED}.
     *
     * @throws IllegalStateException se o empréstimo já tiver sido devolvido
     */
    public void markAsReturned() {
        if (status.equals(LoanStatus.RETURNED)) {
            throw new IllegalStateException("This book has already been returned.");
        }
        returnDate = LocalDateTime.now();
        status = LoanStatus.RETURNED;
    }

    /**
     * Compara empréstimos pela identidade (id), como recomendado para
     * entidades JPA — dois empréstimos são iguais se representarem o mesmo
     * registro no banco, independentemente dos demais campos.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Loan loan = (Loan) o;
        return Objects.equals(id, loan.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}