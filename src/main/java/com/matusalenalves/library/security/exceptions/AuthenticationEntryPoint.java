package com.matusalenalves.library.security.exceptions;

import com.matusalenalves.library.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Responde toda requisição rejeitada por falta de autenticação (token
 * ausente, malformado ou expirado) com o mesmo formato padronizado de erro
 * (RF28/RNF17) usado pelo {@code GlobalExceptionHandler}.
 * <p>
 * Necessário porque essa rejeição acontece dentro da cadeia de filtros do
 * Spring Security — antes do {@code DispatcherServlet} — e por isso nunca
 * chega a um {@code @ExceptionHandler}: é registrado como
 * {@code authenticationEntryPoint} em {@code SecurityConfig}.
 */
@Component
public class AuthenticationEntryPoint implements org.springframework.security.web.AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public AuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                "Missing, invalid or expired authentication token.",
                request.getRequestURI()
        );

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), error);
    }
}
