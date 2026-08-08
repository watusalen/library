package com.matusalenalves.library.security;

import com.matusalenalves.library.security.exceptions.AccessDeniedHandler;
import com.matusalenalves.library.security.jwt.JwtAuthenticationFilter;
import com.matusalenalves.library.security.userdetails.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuração central do Spring Security (RNF04, RNF06): habilita
 * autenticação stateless via JWT e define, endpoint a endpoint, se a
 * requisição exige apenas autenticação ou um perfil específico (RN08, RN09),
 * conforme o contrato de API da seção 9 do documento de requisitos.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final CustomUserDetailsService userDetailsService;

    private final AuthenticationEntryPoint authenticationEntryPoint;

    private final AccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CustomUserDetailsService userDetailsService,
            AuthenticationEntryPoint authenticationEntryPoint,
            AccessDeniedHandler accessDeniedHandler
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    /**
     * Algoritmo de hash usado para armazenar e verificar senhas (RNF05).
     *
     * @return um {@link BCryptPasswordEncoder} com o fator de custo padrão.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Liga o carregamento do usuário ({@link CustomUserDetailsService}) à
     * verificação de senha ({@link #passwordEncoder()}), formando o
     * provedor usado pelo {@link AuthenticationManager} para validar
     * credenciais no login (RF02).
     *
     * @return o provedor de autenticação configurado.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Expõe o {@link AuthenticationManager} padrão do Spring Security como
     * bean, para ser injetado em {@code AuthService} e autenticar as
     * credenciais de login.
     *
     * @param config configuração de autenticação gerenciada pelo Spring.
     * @return o gerenciador de autenticação.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Define a cadeia de filtros de segurança: desativa CSRF (irrelevante
     * para uma API stateless autenticada por token), remove sessão HTTP
     * (RNF04), autoriza cada grupo de endpoints conforme o perfil exigido
     * (RN08, RN09) e insere {@link #jwtAuthenticationFilter} antes do
     * filtro padrão de usuário/senha, para popular o {@code SecurityContext}
     * a partir do token antes de qualquer verificação de autorização.
     *
     * @param http builder de configuração HTTP do Spring Security.
     * @return a cadeia de filtros configurada.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/books/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/books/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/books/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/books/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/authors/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/authors/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/authors/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/authors/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/categories/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/categories/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/loans/me").hasRole("CLIENT")
                        .requestMatchers(HttpMethod.GET, "/loans").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/loans").hasRole("CLIENT")
                        .requestMatchers(HttpMethod.PUT, "/loans/*/return").authenticated()

                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}