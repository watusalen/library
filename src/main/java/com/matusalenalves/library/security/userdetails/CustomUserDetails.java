package com.matusalenalves.library.security.userdetails;

import com.matusalenalves.library.entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Adapta {@link User} ao contrato {@link UserDetails} exigido pelo Spring
 * Security, usando o e-mail como username (RN07) e o perfil ({@link
 * com.matusalenalves.library.entities.enums.Role}) como authority.
 */
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    /**
     * Expõe a entidade original, para uso em outras camadas que precisem
     * do usuário autenticado além do contrato {@link UserDetails}.
     *
     * @return o usuário autenticado.
     */
    public User getUser() {
        return user;
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * O prefixo {@code "ROLE_"} é exigido pela convenção do Spring Security
     * para que {@code hasRole("ADMIN")} (usado em {@code SecurityConfig})
     * funcione — sem ele, a autorização por perfil falha silenciosamente.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}