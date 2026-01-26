package com.healthcenter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration de la sécurité
 * Fournit le PasswordEncoder pour le hashage des mots de passe
 */
@Configuration
public class SecurityConfig {
    
    /**
     * Bean PasswordEncoder utilisant BCrypt
     * BCrypt est un algorithme de hashage sécurisé recommandé pour les mots de passe
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
