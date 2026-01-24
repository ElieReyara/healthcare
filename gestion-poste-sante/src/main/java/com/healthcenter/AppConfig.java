package com.healthcenter;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Configuration Spring Boot principale.
 * 
 * @SpringBootApplication active :
 * - @ComponentScan (détecte @Service, @Repository, @Component)
 * - @EnableAutoConfiguration (JPA, Transactions, etc.)
 * - @Configuration (permet @Bean)
 */
@SpringBootApplication
public class AppConfig {
    // Classe vide, les annotations font tout
}
