package com.matusalenalves.library.entities;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Categoria usada para classificar um ou mais {@link Book livros} do acervo.
 * <p>
 * Não pode ser excluída enquanto possuir ao menos um livro vinculado (RN06).
 */
@Entity
@Table(name = "category")
public class Category implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100, nullable = false)
    private String name;

    public Category() {
    }

    public Category(Long id, String name) {
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
     * Compara categorias pela identidade (id), como recomendado para
     * entidades JPA — duas categorias são iguais se representarem o mesmo
     * registro no banco, independentemente dos demais campos.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}