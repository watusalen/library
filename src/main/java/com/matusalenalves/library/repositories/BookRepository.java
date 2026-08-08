package com.matusalenalves.library.repositories;

import com.matusalenalves.library.entities.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
     * Cada filtro é opcional e pode ser combinado livremente com os demais:
     * um parâmetro {@code null} simplesmente não é aplicado à busca. A
     * comparação do título é parcial e não diferencia maiúsculas de
     * minúsculas, conforme o fluxo alternativo 1a da UC04.
     *
     * @param title      trecho do título a ser buscado, ou {@code null} para não filtrar por título
     * @param authorId   identificador do autor, ou {@code null} para não filtrar por autor
     * @param categoryId identificador da categoria, ou {@code null} para não filtrar por categoria
     * @param pageable   página, tamanho e ordenação solicitados
     * @return a página de livros que atendem a todos os filtros informados
     */
    @Query("""
        SELECT DISTINCT b FROM Book b
        LEFT JOIN b.categories c
        WHERE (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')))
          AND (:authorId IS NULL OR b.author.id = :authorId)
          AND (:categoryId IS NULL OR c.id = :categoryId)
        """)
    Page<Book> search(@Param("title") String title, @Param("authorId") Long authorId, @Param("categoryId") Long categoryId, Pageable pageable);

    /**
     * Verifica se existe algum livro vinculado ao autor informado.
     * <p>
     * Usado antes de excluir um autor, para impedir a exclusão enquanto
     * houver livro vinculado (RN05).
     *
     * @param authorId identificador do autor.
     * @return {@code true} se houver ao menos um livro vinculado ao autor.
     */
    boolean existsByAuthorId(Long authorId);

    /**
     * Verifica se existe algum livro vinculado à categoria informada.
     * <p>
     * Usado antes de excluir uma categoria, para impedir a exclusão enquanto
     * houver livro vinculado (RN06).
     *
     * @param categoriesId identificador da categoria.
     * @return {@code true} se houver ao menos um livro vinculado à categoria.
     */
    boolean existsByCategoriesId(Long categoriesId);
}