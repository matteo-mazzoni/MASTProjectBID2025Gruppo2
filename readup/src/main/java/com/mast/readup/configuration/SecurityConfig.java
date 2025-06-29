package com.mast.readup.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.mast.readup.entities.Utente;
import com.mast.readup.services.UtenteService;


@Configuration
public class SecurityConfig {

    /**
     * BCryptPasswordEncoder for password encoding
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return (request, response, authentication) -> {
            if (authentication != null) {
                String nick = authentication.getName();
                System.out.println("Utente aggiornato: " + nick);
                utenteService.findByNickname(nick)
                    .ifPresent(loggedInUser -> {
                        loggedInUser.setLoggedIn(false);
                        utenteService.aggiornaUtente(loggedInUser);
                    }
                );

            }

            // Redirect to the home page
            response.sendRedirect(request.getContextPath() + "/?logout=true");
        };
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
        // Disabled CSRF (SpringBoot 6.1+)  
        .csrf(csrf -> csrf.disable())
    
        .authorizeHttpRequests(auth -> auth
        .anyRequest().permitAll()
        )

        // Rules for login
        .formLogin(form -> form
            .loginPage("/")
            .loginProcessingUrl("/login")
            .defaultSuccessUrl("/", true)
            .successHandler(authenticationSuccessHandler())
            .failureUrl("/?loginError=true")
            .permitAll()
        )

        // Rules for logout
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessHandler(logoutSuccessHandler())
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID")
            .logoutSuccessUrl("/?logout=true")
            .permitAll()
        );

        return http.build();
    
    }

    /**
     * After a successful login, this handler puts the logged user into session
     * @return the AuthenticationSuccessHandler
     */
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            String currentUserNickName = authentication.getName();
            Utente user = utenteService.findByNickname(currentUserNickName).orElseThrow(() -> new IllegalStateException("Utente non trovato"));
                                
            request.getSession().setAttribute("currentUser", user);
          
            // Redirect to the home page
            response.sendRedirect(request.getContextPath() + "/");  
        };
    
    }


    @Autowired
    private UtenteService utenteService;

    /**
     * Handler for successful logout events. 
     * This handler updates the user's logged-in status to false in the database 
     * if the user is authenticated, ensuring that the user's session is properly terminated.
     * 
     * @return the LogoutSuccessHandler
     */
  
}




    









    

  