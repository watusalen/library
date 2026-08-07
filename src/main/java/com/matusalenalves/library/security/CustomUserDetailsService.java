package com.matusalenalves.library.security;

import com.matusalenalves.library.entities.User;
import com.matusalenalves.library.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementação de {@link UserDetailsService} usada pelo Spring Security
 * para carregar o usuário autenticado a partir do e-mail (RF02), tanto no
 * login quanto na validação do token a cada requisição.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Busca o usuário pelo e-mail e o adapta para {@link UserDetails}.
     *
     * @param email e-mail do usuário (usado como username).
     * @return o usuário autenticável correspondente.
     * @throws UsernameNotFoundException se não existir usuário com esse e-mail.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return new CustomUserDetails(user);
    }
}