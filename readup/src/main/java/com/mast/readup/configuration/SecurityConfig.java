package com.mast.readup.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.mast.readup.entities.Utente;
import com.mast.readup.services.UtenteService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {

    private final UtenteService utenteService;

    public SecurityConfig(@Lazy UtenteService utenteService) {
        this.utenteService = utenteService;
    }

    /**
     * Recupera l'AuthenticationManager da Spring Boot (non crea nuovi bean,
     * evita errori di "authenticationManager cannot be null").
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }


    /**
     * It configures what URL are considered public and what are protected
     * (requires authentication).
     * It also configures the login and logout process.
     *
     *
     * @param http the HttpSecurity object that contains the configuration for
     *             the SecurityFilterChain
     * @return the SecurityFilterChain object configured by this method
     * @throws Exception if an error occurs while building the
     *                   SecurityFilterChain
    */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/profile", "/myBooks", "/booklists")
                    .authenticated()
                .anyRequest()
                    .permitAll()
            )
            .formLogin(form -> form
                .loginPage("/")
                .loginProcessingUrl("/login")
                .usernameParameter("nickname")
                .passwordParameter("password")
                .defaultSuccessUrl("/", true)
                .successHandler(authenticationSuccessHandler())
                .failureUrl("/?loginError=true")
                .permitAll()
            )
            .logout(logout -> logout.disable());

        return http.build();
    }

   /**
    * After a successful login, this handler puts the logged user into session
    * @return the AuthenticationSuccessHandler
    */
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (HttpServletRequest request,
                HttpServletResponse response,
                org.springframework.security.core.Authentication auth) -> {

            String nickname = auth.getName();
            Utente u = utenteService.findByNickname(nickname)
                .orElseThrow(() -> new IllegalStateException("Utente non trovato"));

            u.setLoggedIn(true);
            utenteService.cambiaStatusLogin(u.getIdUtente(), true);

            request.getSession().setAttribute("currentUser", u);
            response.sendRedirect(request.getContextPath() + "/");
        };
    }
}
