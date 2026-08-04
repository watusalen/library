package com.matusalenalves.library.repositories;

import com.matusalenalves.library.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Acesso a dados de {@link User}.
 * <p>
 * Além das operações de CRUD herdadas de {@link JpaRepository}, concentra as
 * consultas por e-mail usadas no login (RF02) e no cadastro (RF03), já que o
 * e-mail é o identificador único de login do usuário (RN07).
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca um usuário pelo e-mail, que funciona como identificador de login (RN07).
     * <p>
     * Usado no processo de autenticação (RF02) para localizar o usuário
     * antes de validar a senha.
     *
     * @param email e-mail do usuário.
     * @return o usuário correspondente, ou {@link Optional#empty()} se não existir.
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica se já existe um usuário cadastrado com o e-mail informado.
     * <p>
     * Usado no cadastro de novos usuários (RF03) para impedir e-mails
     * duplicados, já que o e-mail é único no sistema (RN07).
     *
     * @param email e-mail a ser verificado.
     * @return {@code true} se já existir um usuário com esse e-mail.
     */
    boolean existsByEmail(String email);
}