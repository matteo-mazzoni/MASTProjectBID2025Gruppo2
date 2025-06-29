package com.mast.readup.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

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

    /**
     * Configure the security filter chain. This method is annotated with
     * @Bean so that Spring Security can find it and use it to configure the
     * SecurityFilterChain. 
     * It configures what URL are considered public and what are protected
     * (requires authentication).
     * It also configures the logout process and the login page.
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

        // Protected URLs
        .requestMatchers("/profilo.html", "/libri.html", "/booklist.html").authenticated()
        
        // Public URLs
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
            .logoutSuccessUrl("/?logout=true")
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID")
            .permitAll()
        );

        return http.build();
    
    }


    @Autowired 
    private UtenteService utenteService;
     

    /**
     * After a successful login, this handler puts the logged user into session
     *
     * @return the AuthenticationSuccessHandler
     */
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            String currentUserNickName = authentication.getName();
            Utente user = utenteService.findByNickname(currentUserNickName).orElseThrow(() -> new IllegalStateException("Utente non trovato"));
                                
            request.getSession().setAttribute("currentUser", user);
            System.out.println("🔥 LOGIN OK: " + currentUserNickName);

            // Redirect to the home page
            response.sendRedirect(request.getContextPath() + "/");  
        };
    
    }

}
    









    

  