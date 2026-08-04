package com.matusalenalves.library.entities;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "book")
public class Book implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(
            length = 200,
            nullable = false
    )
    private String title;
    @Column(
            unique = true,
            length = 20,
            nullable = false
    )
    private String isbn;
    @Column(nullable = false)
    private Integer publicationYear;
    @Column(nullable = false)
    private Integer totalCopies;
    @Column(nullable = false)
    private Integer availableCopies;

    @ManyToOne
    @JoinColumn(
            name = "author_id",
            nullable = false
    )
    private Author author;
    @ManyToMany
    @JoinTable(
            name = "book_category",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    public Book() {
    }

    public Book(Long id, String title, String isbn, Integer publicationYear, Integer totalCopies, Author author) {
        this.id = id;
        this.title = title;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public Integer getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(Integer totalCopies) {
        this.totalCopies = totalCopies;
    }

    public Integer getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(Integer availableCopies) {
        this.availableCopies = availableCopies;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public boolean isAvailable() {
        return getAvailableCopies() > 0;
    }

    public void decreaseAvailableCopies() {
        if (!isAvailable()) {
            throw new IllegalStateException("It is not possible to decrement a book because there are no available copies.");
        }
        availableCopies--;
    }

    public void increaseAvailableCopies() {
        if (!(totalCopies > availableCopies)) {
            throw new IllegalStateException("It is not possible to add a new copy of this book because it would exceed the actual number of copies.");
        }
        availableCopies++;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(id, book.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}