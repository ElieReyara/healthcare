package com.healthcenter.config;

import com.healthcenter.domain.entities.Utilisateur;
import com.healthcenter.domain.enums.RoleUtilisateur;
import com.healthcenter.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class InitialUserConfig {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Bean
    public CommandLineRunner createDefaultAdmin() {
        return args -> {
            // Vérifier si l'utilisateur admin existe déjà
            if (utilisateurRepository.findByUsername("admin").isEmpty()) {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                
                Utilisateur admin = new Utilisateur();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRole(RoleUtilisateur.ADMIN);
                admin.setNom("Administrateur");
                admin.setPrenom("Système");
                admin.setActif(true);
                
                utilisateurRepository.save(admin);
                
                System.out.println("✅ Utilisateur admin créé avec succès !");
                System.out.println("   Login: admin");
                System.out.println("   Mot de passe: admin123");
            }
        };
    }
}