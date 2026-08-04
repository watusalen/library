package com.matusalenalves.library.entities;

import com.matusalenalves.library.entities.enums.LoanStatus;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "loan")
public class Loan implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime loanDate;
    @Column(nullable = false)
    private LocalDate dueDate;
    @Column(nullable = true)
    private LocalDateTime returnDate;
    @Column(
            length = 20,
            nullable = false
    )
    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    @ManyToOne
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;
    @ManyToOne
    @JoinColumn(
            name = "book_id",
            nullable = false
    )
    private Book book;

    public Loan() {
    }

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

    public boolean isOverdue() {
        return status.equals(LoanStatus.ACTIVE) && dueDate.isBefore(LocalDate.now());
    }

    public void markAsReturned() {
        if(status.equals(LoanStatus.RETURNED)){
            throw new IllegalStateException("This book has already been returned.");
        }
        returnDate = LocalDateTime.now();
        status = LoanStatus.RETURNED;
    }

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