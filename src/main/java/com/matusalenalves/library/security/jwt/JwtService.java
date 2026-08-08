package com.matusalenalves.library.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

/**
 * Geração e validação de tokens JWT (RNF04): emite o token no login e o
 * valida a cada requisição autenticada, sem exigir estado de sessão no
 * servidor.
 */
@Service
public class JwtService {

    private final SecretKey secretKey;

    private final long expirationSeconds;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-seconds}") long expirationSeconds
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationSeconds = expirationSeconds;
    }

    /**
     * Gera um novo token para o e-mail informado, com tempo de expiração
     * configurável (RNF07) via {@code jwt.expiration-seconds}.
     *
     * @param email e-mail do usuário autenticado (usado como subject do token).
     * @return o token assinado.
     */
    public String generateToken(String email) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationSeconds * 1000);

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Extrai o e-mail (subject) contido no token.
     *
     * @param token token JWT.
     * @return o e-mail do usuário para quem o token foi emitido.
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Verifica se o token corresponde ao e-mail informado e ainda não
     * expirou.
     *
     * @param token token JWT.
     * @param email e-mail esperado.
     * @return {@code true} se o token for válido para esse e-mail.
     */
    public boolean isTokenValid(String token, String email) {
        String extractedEmail = extractEmail(token);
        return extractedEmail.equals(email) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    /**
     * Decodifica e valida a assinatura do token, extraindo o claim
     * desejado. Centraliza o parsing usado por {@link #extractEmail} e
     * {@link #isTokenExpired}.
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claimsResolver.apply(claims);
    }
}