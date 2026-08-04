package com.matusalenalves.library.repositories;

import com.matusalenalves.library.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Acesso a dados de {@link Category}.
 * <p>
 * Ainda não possui consultas próprias: por enquanto usa apenas as operações
 * de CRUD herdadas de {@link JpaRepository} (cadastro, edição, listagem e
 * exclusão de categorias — RF14-RF17).
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}