package com.matusalenalves.library.services;

import com.matusalenalves.library.dto.request.LoginRequest;
import com.matusalenalves.library.dto.request.RegisterRequest;
import com.matusalenalves.library.dto.response.TokenResponse;
import com.matusalenalves.library.entities.User;
import com.matusalenalves.library.entities.enums.Role;
import com.matusalenalves.library.repositories.UserRepository;
import com.matusalenalves.library.security.JwtService;
import com.matusalenalves.library.services.exceptions.EmailAlreadyExistsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Regras de negócio para cadastro (RF01, RF03) e autenticação (RF02) de usuários.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * Cadastra um novo usuário com perfil {@link Role#CLIENT} (RF01).
     *
     * @param request dados de cadastro.
     * @throws EmailAlreadyExistsException se já existir usuário com esse e-mail (RN07, RF03).
     */
    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        User user = new User(
                null,
                request.name(),
                request.email(),
                passwordEncoder.encode(request.password()),
                Role.CLIENT
        );

        userRepository.save(user);
    }

    /**
     * Autentica um usuário e emite um token JWT (RF02).
     *
     * @param request credenciais de login.
     * @return o token de acesso.
     */
    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        String token = jwtService.generateToken(request.email());
        return new TokenResponse(token);
    }
}