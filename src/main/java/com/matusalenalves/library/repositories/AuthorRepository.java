package com.matusalenalves.library.repositories;

import com.matusalenalves.library.entities.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Acesso a dados de {@link Author}.
 * <p>
 * Ainda não possui consultas próprias: por enquanto usa apenas as operações
 * de CRUD herdadas de {@link JpaRepository} (cadastro, edição, listagem e
 * exclusão de autores — RF10-RF13).
 */
@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
}