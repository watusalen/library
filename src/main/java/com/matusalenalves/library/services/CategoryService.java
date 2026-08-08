package com.matusalenalves.library.services;

import com.matusalenalves.library.dto.request.CategoryRequest;
import com.matusalenalves.library.dto.response.CategoryResponse;
import com.matusalenalves.library.dto.response.PageResponse;
import com.matusalenalves.library.entities.Category;
import com.matusalenalves.library.mapper.CategoryMapper;
import com.matusalenalves.library.repositories.CategoryRepository;
import com.matusalenalves.library.repositories.BookRepository;
import com.matusalenalves.library.services.exceptions.BusinessRuleException;
import com.matusalenalves.library.services.exceptions.DataBaseException;
import com.matusalenalves.library.services.exceptions.ResourceNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Regras de negócio para categorias (RF14-RF17), incluindo a checagem da
 * RN06 (categoria não pode ser excluída enquanto possuir livro vinculado).
 */
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    private final BookRepository bookRepository;

    public CategoryService(CategoryRepository categoryRepository, BookRepository bookRepository) {
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
    }

    /**
     * Lista as categorias cadastradas, paginadas (RF17, RNF12).
     *
     * @param pageable página, tamanho e ordenação solicitados
     * @return a página de categorias correspondente, convertida para o DTO de resposta
     */
    @Transactional(readOnly = true)
    public PageResponse<CategoryResponse> findAll(Pageable pageable) {
        return PageResponse.of(categoryRepository.findAll(pageable).map(CategoryMapper::toResponse));
    }

    /**
     * Busca uma categoria pelo id.
     *
     * @param id identificador da categoria
     * @return a categoria correspondente, convertida para o DTO de resposta
     * @throws ResourceNotFoundException se não existir categoria com esse id
     */
    @Transactional(readOnly = true)
    public CategoryResponse findById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
        return CategoryMapper.toResponse(category);
    }

    /**
     * Cadastra uma nova categoria (RF14).
     *
     * @param request dados da categoria a ser cadastrada
     * @return a categoria criada, convertida para o DTO de resposta
     */
    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        Category category = CategoryMapper.toEntity(request);
        return CategoryMapper.toResponse(categoryRepository.save(category));
    }

    /**
     * Edita os dados de uma categoria existente (RF15).
     *
     * @param id      identificador da categoria a ser editada
     * @param request novos dados da categoria
     * @return a categoria atualizada, convertida para o DTO de resposta
     * @throws ResourceNotFoundException se não existir categoria com esse id
     */
    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
        category.setName(request.name());
        return CategoryMapper.toResponse(categoryRepository.save(category));
    }

    /**
     * Exclui uma categoria (RF16).
     * <p>
     * A RN06 é verificada de duas formas: primeiro checando explicitamente
     * se há livro vinculado; e, como rede de segurança contra condições de
     * corrida, capturando também a {@link DataIntegrityViolationException}
     * que o banco lançaria caso um vínculo tenha sido criado entre a
     * checagem e a exclusão.
     *
     * @param id identificador da categoria a ser excluída
     * @throws ResourceNotFoundException se não existir categoria com esse id
     * @throws BusinessRuleException     se houver livro vinculado à categoria
     * @throws DataBaseException         se a exclusão violar integridade referencial no banco
     */
    @Transactional
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }
        if (bookRepository.existsByCategoriesId(id)) {
            throw new BusinessRuleException("Cannot delete category with id " + id + ": there are books linked to this category.");
        }
        try {
            categoryRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DataBaseException(e.getMessage());
        }
    }
}