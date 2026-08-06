package com.matusalenalves.library.services;

import com.matusalenalves.library.dto.request.AuthorRequest;
import com.matusalenalves.library.dto.response.AuthorResponse;
import com.matusalenalves.library.entities.Author;
import com.matusalenalves.library.mapper.AuthorMapper;
import com.matusalenalves.library.repositories.AuthorRepository;
import com.matusalenalves.library.repositories.BookRepository;
import com.matusalenalves.library.services.exceptions.BusinessRuleException;
import com.matusalenalves.library.services.exceptions.DataBaseException;
import com.matusalenalves.library.services.exceptions.ResourceNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Regras de negócio para autores (RF10-RF13), incluindo a checagem da RN05
 * (autor não pode ser excluído enquanto possuir livro vinculado).
 */
@Service
public class AuthorService {
    private final AuthorRepository authorRepository;

    private final BookRepository bookRepository;

    public AuthorService(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    /**
     * Lista todos os autores cadastrados (RF13).
     *
     * @return todos os autores, convertidos para o DTO de resposta
     */
    @Transactional(readOnly = true)
    public List<AuthorResponse> findAll() {
        return authorRepository
                .findAll()
                .stream()
                .map(author -> AuthorMapper.toResponse(author))
                .toList();
    }

    /**
     * Busca um autor pelo id.
     *
     * @param id identificador do autor
     * @return o autor correspondente, convertido para o DTO de resposta
     * @throws ResourceNotFoundException se não existir autor com esse id
     */
    @Transactional(readOnly = true)
    public AuthorResponse findById(Long id) {
        Author author = authorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
        return AuthorMapper.toResponse(author);
    }

    /**
     * Cadastra um novo autor (RF10).
     *
     * @param request dados do autor a ser cadastrado
     * @return o autor criado, convertido para o DTO de resposta
     */
    @Transactional
    public AuthorResponse create(AuthorRequest request) {
        Author author = AuthorMapper.toEntity(request);
        return AuthorMapper.toResponse(authorRepository.save(author));
    }

    /**
     * Edita os dados de um autor existente (RF11).
     *
     * @param id      identificador do autor a ser editado
     * @param request novos dados do autor
     * @return o autor atualizado, convertido para o DTO de resposta
     * @throws ResourceNotFoundException se não existir autor com esse id
     */
    @Transactional
    public AuthorResponse update(Long id, AuthorRequest request) {
        Author author = authorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
        author.setName(request.name());
        return AuthorMapper.toResponse(authorRepository.save(author));
    }

    /**
     * Exclui um autor (RF12).
     * <p>
     * A RN05 é verificada de duas formas: primeiro checando explicitamente
     * se há livro vinculado; e, como rede de segurança contra condições de
     * corrida, capturando também a {@link DataIntegrityViolationException}
     * que o banco lançaria caso um vínculo tenha sido criado entre a
     * checagem e a exclusão.
     *
     * @param id identificador do autor a ser excluído
     * @throws ResourceNotFoundException se não existir autor com esse id
     * @throws BusinessRuleException     se houver livro vinculado ao autor
     * @throws DataBaseException         se a exclusão violar integridade referencial no banco
     */
    @Transactional
    public void delete(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }
        if (bookRepository.existsByAuthorId(id)) {
            throw new BusinessRuleException("Cannot delete author with id " + id + ": there are books linked to this author.");
        }
        try {
            authorRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DataBaseException(e.getMessage());
        }
    }
}