package com.matusalenalves.library.controller.exceptions;

import com.matusalenalves.library.dto.response.ErrorResponse;
import com.matusalenalves.library.services.exceptions.BusinessRuleException;
import com.matusalenalves.library.services.exceptions.DataBaseException;
import com.matusalenalves.library.services.exceptions.EmailAlreadyExistsException;
import com.matusalenalves.library.services.exceptions.LoanAccessDeniedException;
import com.matusalenalves.library.services.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Intercepta as exceções lançadas por qualquer camada e as traduz no
 * formato padronizado de erro (RF28, RNF11, RNF17), definido na seção 9 do
 * documento de requisitos.
 * <p>
 * Não cobre os 401/403 gerados pelo próprio filtro de segurança (token
 * ausente/inválido/expirado, ou perfil sem permissão para a rota, conforme
 * {@code SecurityConfig}): essa rejeição acontece dentro da cadeia de
 * filtros do Spring Security, antes do {@code DispatcherServlet}, e por isso
 * nunca chega a um {@code @ExceptionHandler}. Esses dois casos são cobertos
 * separadamente por {@code RestAuthenticationEntryPoint} (401) e
 * {@code AccessDeniedHandler} (403), no pacote {@code security}, ambos
 * escrevendo o mesmo formato de {@link ErrorResponse} diretamente na
 * resposta.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Recurso inexistente (busca, edição ou exclusão por id que não existe).
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, e.getMessage(), request);
    }

    /**
     * Violação de regra de negócio ou de integridade referencial que
     * impede a operação (RN05, RN06, RN10, RN07 e a rede de segurança de
     * {@link DataBaseException} contra condições de corrida).
     */
    @ExceptionHandler({BusinessRuleException.class, DataBaseException.class, EmailAlreadyExistsException.class})
    public ResponseEntity<ErrorResponse> conflict(RuntimeException e, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, e.getMessage(), request);
    }

    /**
     * Cliente tentando devolver um empréstimo que não é seu (RN09).
     */
    @ExceptionHandler(LoanAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> loanAccessDenied(LoanAccessDeniedException e, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, e.getMessage(), request);
    }

    /**
     * Credenciais inválidas no login (RF02), sem detalhar qual campo está
     * incorreto (critério de aceite da US02).
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> authenticationFailed(AuthenticationException e, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid email or password.", request);
    }

    /**
     * Falha de Bean Validation em um {@code @RequestBody} (RNF10), com o
     * detalhamento por campo exigido pela seção 9 do documento de
     * requisitos.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException e, HttpServletRequest request) {
        List<ErrorResponse.FieldErrorResponse> errors = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ErrorResponse.FieldErrorResponse(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();

        ErrorResponse error = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation failed for the given fields.",
                request.getRequestURI(),
                errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Rede de segurança final (RNF11): qualquer falha não tratada
     * explicitamente acima vira 500, sem expor detalhes internos da
     * aplicação ao cliente.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> internalServerError(Exception e, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.", request);
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.of(status.value(), status.getReasonPhrase(), message, request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }
}