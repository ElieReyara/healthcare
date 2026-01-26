package com.healthcenter;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuration Spring Boot principale.
 * 
 * @SpringBootApplication active :
 * - @ComponentScan (détecte @Service, @Repository, @Component)
 * - @EnableAutoConfiguration (JPA, Transactions, etc.)
 * - @Configuration (permet @Bean)
 * 
 * @EnableScheduling active les tâches planifiées (@Scheduled)
 * @EnableAsync active l'exécution asynchrone (@Async)
 */
@SpringBootApplication
@EnableScheduling
@EnableAsync
public class AppConfig {
    // Classe vide, les annotations font tout
}
