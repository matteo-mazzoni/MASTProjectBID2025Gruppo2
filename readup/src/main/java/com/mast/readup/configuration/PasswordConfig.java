package com.mast.readup.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig {

    /**
     * Configures a PasswordEncoder bean using BCrypt hashing algorithm.
     * Provides password encoding functionality with a default strength.
     * This bean is used for encrypting and verifying passwords.
     */
  
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}