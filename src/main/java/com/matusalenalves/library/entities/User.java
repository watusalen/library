package com.matusalenalves.library.entities;

import com.matusalenalves.library.entities.enums.Role;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Usuário do sistema, autenticado por e-mail e senha (RF01, RF02).
 * <p>
 * Mapeada para a tabela {@code tb_user}, e não {@code user}, pois {@code user}
 * é palavra reservada no PostgreSQL.
 */
@Entity
@Table(name = "tb_user")
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100, nullable = false)
    private String name;
    @Column(unique = true, length = 150, nullable = false)
    private String email;
    @Column(length = 255, nullable = false)
    private String password;
    @Column(length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public User() {
    }

    public User(Long id, String name, String email, String password, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Indica se o usuário possui perfil {@link Role#ADMIN} (RN08).
     *
     * @return {@code true} se o perfil do usuário for {@code ADMIN}
     */
    public boolean isAdmin() {
        return role.equals(Role.ADMIN);
    }

    /**
     * Compara usuários pela identidade (id), como recomendado para
     * entidades JPA — dois usuários são iguais se representarem o mesmo
     * registro no banco, independentemente dos demais campos.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}