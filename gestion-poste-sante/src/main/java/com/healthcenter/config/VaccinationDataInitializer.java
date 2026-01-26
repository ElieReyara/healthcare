package com.healthcenter.config;

import com.healthcenter.service.CalendrierVaccinalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * DATA INITIALIZER = Initialisation automatique du calendrier vaccinal Sénégal.
 * 
 * Exécuté au démarrage de l'application (@PostConstruct).
 * Peuple la base avec les 16 vaccins du calendrier national.
 */
@Component
public class VaccinationDataInitializer {
    
    private static final Logger logger = LoggerFactory.getLogger(VaccinationDataInitializer.class);
    
    @Autowired
    private CalendrierVaccinalService calendrierVaccinalService;
    
    
    /**
     * Initialisation du calendrier vaccinal au démarrage.
     */
    @PostConstruct
    public void initCalendrierVaccinal() {
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
}
