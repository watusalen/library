package com.matusalenalves.library.entities;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Autor de um ou mais {@link Book livros} do acervo.
 * <p>
 * Não pode ser excluído enquanto possuir ao menos um livro vinculado (RN05).
 */
@Entity
@Table(name = "author")
public class Author implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 150, nullable = false)
    private String name;

    public Author() {
    }

    public Author(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Compara autores pela identidade (id), como recomendado para
     * entidades JPA — dois autores são iguais se representarem o mesmo
     * registro no banco, independentemente dos demais campos.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Author author = (Author) o;
        return Objects.equals(id, author.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}