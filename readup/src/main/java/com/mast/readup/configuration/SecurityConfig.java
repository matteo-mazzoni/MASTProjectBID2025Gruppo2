package com.mast.readup.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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
     * It retrieves the built-in l'AuthenticationManager configured by Spring Security,
     * It utilise automatically all the bean UserDetailsService and PasswordEncoder. 
    //  */
    // @Bean
    // public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    //     return authConfig.getAuthenticationManager();
    // }


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
        .anyRequest().permitAll()
        )
        .formLogin(form -> form.disable())
        .httpBasic(basic -> basic.disable())
        .logout(logout -> logout.disable());

        return http.build();
    
    }
}
    

    //       .requestMatchers(
    //           "/", "/login", "/register", "/css/**", "/js/**", "/img/**", "/favicon.ico"
    //       ).permitAll()
          
            
    //     .formLogin(form -> form
    //         .loginPage("/")
    //         .loginProcessingUrl("/login")
    //         .defaultSuccessUrl("/home", true)
    //         .failureUrl("/login?error=true")
    //         .permitAll()
    //     )
    //     .logout(logout -> logout
    //         .logoutUrl("/logout")
    //         .logoutSuccessUrl("/login?logout=true")
    //         .invalidateHttpSession(true)
    //         .deleteCookies("JSESSIONID")
    //         .permitAll()
    //     );

    //     return http.build();
    // }
// }