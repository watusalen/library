package com.matusalenalves.library.entities.enums;

/**
 * Perfil de acesso de um {@link com.matusalenalves.library.entities.User}.
 * <p>
 * Determina quais operações o usuário pode realizar (RN08): apenas
 * {@link #ADMIN} pode criar, editar ou excluir livros, autores e categorias.
 */
public enum Role {

    /**
     * Responsável pela gestão do acervo (livros, autores e categorias) e
     * pela supervisão de todos os empréstimos.
     */
    ADMIN,

    /**
     * Consulta o acervo e gerencia apenas os próprios empréstimos.
     */
    CLIENT
}