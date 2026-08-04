package com.matusalenalves.library.repositories;

import com.matusalenalves.library.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Acesso a dados de {@link Book}.
 * <p>
 * Além das operações de CRUD herdadas de {@link JpaRepository}, concentra a
 * consulta usada na busca combinada do acervo por título, autor e categoria
 * (RF09).
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * Busca livros combinando filtros de título, autor e categoria (RF09).
     * <p>
     * A comparação do título é parcial e não diferencia maiúsculas de
     * minúsculas, conforme o fluxo alternativo 1a da UC04.
     *
     * @param title      trecho do título a ser buscado.
     * @param authorId   identificador do autor.
     * @param categoryId identificador da categoria.
     * @return livros que atendem simultaneamente aos três filtros.
     */
    List<Book> findByTitleContainingIgnoreCaseAndAuthorIdAndCategoriesId(String title, Long authorId, Long categoryId);
}