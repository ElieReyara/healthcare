package com.healthcenter.config;

import com.healthcenter.domain.entities.Utilisateur;
import com.healthcenter.domain.enums.RoleUtilisateur;
import com.healthcenter.repository.UtilisateurRepository;
import com.healthcenter.service.CalendrierVaccinalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;

/**
 * DATA INITIALIZER = Initialisation automatique du calendrier vaccinal Sénégal + utilisateur admin.
 * 
 * Exécuté au démarrage de l'application (@PostConstruct).
 * Peuple la base avec les 16 vaccins du calendrier national + créé admin par défaut.
 */
@Component
public class VaccinationDataInitializer {
    
    private static final Logger logger = LoggerFactory.getLogger(VaccinationDataInitializer.class);
    
    @Autowired
    private CalendrierVaccinalService calendrierVaccinalService;
    
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    
    /**
     * Initialisation du calendrier vaccinal au démarrage.
     */
    @PostConstruct
    public void init() {
        initCalendrierVaccinal();
        initUtilisateurAdmin();
    }
    
    /**
     * Initialise le calendrier vaccinal
     */
    private void initCalendrierVaccinal() {
        try {
            logger.info("🔄 Vérification du calendrier vaccinal...");
            
            // Vérifier si déjà initialisé
            long count = calendrierVaccinalService.obtenirTousCalendriers().size();
            
            if (count == 0) {
                logger.info("📋 Calendrier vide, initialisation avec données Sénégal...");
                calendrierVaccinalService.initialiserCalendrierDefaut();
                logger.info("✅ Calendrier vaccinal initialisé avec succès (16 vaccins)");
            } else {
                logger.info("✅ Calendrier vaccinal déjà présent (" + count + " vaccins)");
            }
            
        } catch (Exception e) {
            logger.error("❌ Erreur lors de l'initialisation du calendrier vaccinal", e);
        }
    }
    
    /**
     * Crée l'utilisateur admin par défaut si n'existe pas
     */
    private void initUtilisateurAdmin() {
        try {
            logger.info("🔄 Vérification utilisateur admin...");
            
            if (utilisateurRepository.findByUsername("admin").isEmpty()) {
                Utilisateur admin = new Utilisateur();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123")); // CHANGER EN PROD
                admin.setNom("Administrateur");
                admin.setPrenom("Système");
                admin.setRole(RoleUtilisateur.ADMIN);
                admin.setActif(true);
                admin.setDateCreation(LocalDateTime.now());
                utilisateurRepository.save(admin);
                
                logger.info("✅ Utilisateur admin créé : username=admin, password=admin123");
                logger.warn("⚠️  IMPORTANT: Changez le mot de passe admin après première connexion!");
            } else {
                logger.info("✅ Utilisateur admin déjà présent");
            }
            
        } catch (Exception e) {
            logger.error("❌ Erreur lors de l'initialisation utilisateur admin", e);
        }
    }
}
