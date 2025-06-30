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
        // @Lazy qui evita comunque eventuali cicli residui
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
     * Qui definiamo le regole di sicurezza:
     * - CSRF off (solo per sviluppo)
     * - /profile, /myBooks e /booklists richiedono autenticazione
     * - formLogin personalizzato (/ con nickname e password)
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
     * Dopo login ok: settiamo loggedIn=true, salviamo e mettiamo in sessione
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
            utenteService.aggiornaUtente(u);

            request.getSession().setAttribute("currentUser", u);
            response.sendRedirect(request.getContextPath() + "/");
        };
    }
}




























// package com.mast.readup.configuration;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

// import com.mast.readup.entities.Utente;
// import com.mast.readup.services.UtenteService;


// @Configuration
// public class SecurityConfig {
//     @Autowired
//     private UtenteService utenteService;
    
//     /**
//      * BCryptPasswordEncoder for password encoding
//      */
//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }

//      /**
//      * It configures what URL are considered public and what are protected
//      * (requires authentication).
//      * It also configures the login and logout process.
//      *
//      *
//      * @param http the HttpSecurity object that contains the configuration for
//      *             the SecurityFilterChain
//      * @return the SecurityFilterChain object configured by this method
//      * @throws Exception if an error occurs while building the
//      *                   SecurityFilterChain
//      */

//     @Bean
//     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
       

//         // Ruler for requesting pages
//         http
//         // Disabled CSRF (SpringBoot 6.1+)
//         .csrf(csrf -> csrf.disable())
//             .authorizeHttpRequests(auth -> auth
//             .requestMatchers("/profile", "/myBooks", "/booklists").authenticated()
//             .anyRequest().permitAll()
//         )

//         // Rules for login
//         .formLogin(form -> form
//             .loginPage("/")
//             .loginProcessingUrl("/login")
//             .usernameParameter("nickname")   // default is: username for SpringBoot Security
//             .passwordParameter("password")
//             .defaultSuccessUrl("/", true)
//             .successHandler(authenticationSuccessHandler())
//             .failureUrl("/?loginError=true")
//             .permitAll()
//         )

//         // Rules for logout (logout disabled, it will be managed by controller)
//         .logout(logout -> logout.disable());
     
//         return http.build();

//     }


//     /**
//      * After a successful login, this handler puts the logged user into session
//      * @return the AuthenticationSuccessHandler
//      */
//     @Bean
//     public AuthenticationSuccessHandler authenticationSuccessHandler() {
//         return (request, response, authentication) -> {
//             String currentUserNickName = authentication.getName();
//             Utente user = utenteService.findByNickname(currentUserNickName).orElseThrow(() -> new IllegalStateException("Utente non trovato"));

//             user.setLoggedIn(true);
//             utenteService.aggiornaUtente(user);

//             request.getSession().setAttribute("currentUser", user);

//             // Redirect to the home page
//             response.sendRedirect(request.getContextPath() + "/");
//         };

//     }





// }







