package com.matusalenalves.library.security.exceptions;

import com.matusalenalves.library.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Responde toda requisição rejeitada por falta de permissão (usuário
 * autenticado, mas sem o perfil exigido pela rota — RN08/RN09) com o mesmo
 * formato padronizado de erro (RF28/RNF17) usado pelo
 * {@code GlobalExceptionHandler}.
 * <p>
 * Necessário pelo mesmo motivo do {@link AuthenticationEntryPoint}: essa
 * rejeição acontece na cadeia de filtros do Spring Security, antes de
 * qualquer {@code Controller} — é registrado como {@code accessDeniedHandler}
 * em {@code SecurityConfig}.
 */
@Component
public class AccessDeniedHandler implements org.springframework.security.web.access.AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public AccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                "You do not have permission to access this resource.",
                request.getRequestURI()
        );

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), error);
    }
}
