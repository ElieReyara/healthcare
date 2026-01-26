package com.healthcenter.service;

import com.healthcenter.domain.entities.CalendrierVaccinal;
import com.healthcenter.domain.enums.TypeVaccin;
import com.healthcenter.dto.CalendrierVaccinalDTO;
import com.healthcenter.repository.CalendrierVaccinalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * SERVICE CalendrierVaccinal = Gestion du calendrier vaccinal de référence.
 * 
 * Initialise et maintient le schéma vaccinal officiel du Sénégal.
 */
@Service
public class CalendrierVaccinalService {
    
    @Autowired
    private CalendrierVaccinalRepository calendrierRepository;
    
    
    // ========== LECTURE ==========
    
    /**
     * Obtient tous les calendriers vaccinaux.
     * Triés par âge recommandé croissant.
     * 
     * @return Liste calendriers
     */
    @Transactional(readOnly = true)
    public List<CalendrierVaccinal> obtenirTousCalendriers() {
        return calendrierRepository.findAllByOrderByAgeRecommandeAsc();
    }
    
    /**
     * Obtient le calendrier pour un vaccin spécifique.
     * 
     * @param vaccin Type vaccin
     * @return Optional CalendrierVaccinal
     */
    @Transactional(readOnly = true)
    public Optional<CalendrierVaccinal> obtenirCalendrierParVaccin(TypeVaccin vaccin) {
        return calendrierRepository.findByVaccin(vaccin);
    }
    
    /**
     * Obtient tous les vaccins obligatoires.
     * 
     * @return Liste calendriers vaccins obligatoires
     */
    @Transactional(readOnly = true)
    public List<CalendrierVaccinal> obtenirVaccinsObligatoires() {
        return calendrierRepository.findByObligatoireTrue();
    }
    
    
    // ========== CRÉATION/MODIFICATION ==========
    
    /**
     * Crée un nouveau calendrier vaccinal.
     * 
     * @param dto Données calendrier
     * @return CalendrierVaccinal créé
     */
    @Transactional
    public CalendrierVaccinal creerCalendrier(CalendrierVaccinalDTO dto) {
        CalendrierVaccinal calendrier = new CalendrierVaccinal();
        calendrier.setVaccin(TypeVaccin.valueOf(dto.getVaccin()));
        calendrier.setAgeRecommande(dto.getAgeRecommande());
        calendrier.setNombreRappels(dto.getNombreRappels());
        calendrier.setDelaiRappel(dto.getDelaiRappel());
        calendrier.setObligatoire(dto.getObligatoire());
        calendrier.setDescription(dto.getDescription());
        
        return calendrierRepository.save(calendrier);
    }
    
    /**
     * Met à jour un calendrier vaccinal.
     * 
     * @param id ID calendrier
     * @param dto Nouvelles données
     * @return CalendrierVaccinal mis à jour
     */
    @Transactional
    public CalendrierVaccinal mettreAJourCalendrier(Long id, CalendrierVaccinalDTO dto) {
        CalendrierVaccinal calendrier = calendrierRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "Calendrier avec ID " + id + " introuvable"
            ));
        
        calendrier.setAgeRecommande(dto.getAgeRecommande());
        calendrier.setNombreRappels(dto.getNombreRappels());
        calendrier.setDelaiRappel(dto.getDelaiRappel());
        calendrier.setObligatoire(dto.getObligatoire());
        calendrier.setDescription(dto.getDescription());
        
        return calendrierRepository.save(calendrier);
    }
    
    
    // ========== INITIALISATION DONNÉES SÉNÉGAL ==========
    
    /**
     * Initialise le calendrier vaccinal avec les données standard du Sénégal.
     * N'exécute que si la table est vide.
     * 
     * Source : Programme Élargi de Vaccination (PEV) Sénégal
     */
    @Transactional
    public void initialiserCalendrierDefaut() {
        // Vérifier si déjà initialisé
        if (calendrierRepository.count() > 0) {
            return;
        }
        
        // BCG - À la naissance
        calendrierRepository.save(creerCalendrierSenegal(
            TypeVaccin.BCG, 0, 0, null, 
            "Vaccination contre la tuberculose. À administrer dès la naissance."
        ));
        
        // Polio 0 - À la naissance
        calendrierRepository.save(creerCalendrierSenegal(
            TypeVaccin.POLIO_0, 0, 0, null,
            "Première dose de polio orale (VPO). À administrer dès la naissance."
        ));
        
        // Polio 1 - 6 semaines (42 jours)
        calendrierRepository.save(creerCalendrierSenegal(
            TypeVaccin.POLIO_1, 42, 0, null,
            "Deuxième dose de polio. À 6 semaines."
        ));
        
        // Polio 2 - 10 semaines (70 jours)
        calendrierRepository.save(creerCalendrierSenegal(
            TypeVaccin.POLIO_2, 70, 0, null,
            "Troisième dose de polio. À 10 semaines."
        ));
        
        // Polio 3 - 14 semaines (98 jours)
        calendrierRepository.save(creerCalendrierSenegal(
            TypeVaccin.POLIO_3, 98, 0, null,
            "Quatrième dose de polio. À 14 semaines."
        ));
        
        // Penta 1 - 6 semaines (42 jours) - 2 rappels à 28 jours d'intervalle
        calendrierRepository.save(creerCalendrierSenegal(
            TypeVaccin.PENTA_1, 42, 2, 28,
            "Vaccin pentavalent (DTCoq-HepB-Hib). Première dose à 6 semaines."
        ));
        
        // Penta 2 - 10 semaines (70 jours) - 1 rappel à 28 jours
        calendrierRepository.save(creerCalendrierSenegal(
            TypeVaccin.PENTA_2, 70, 1, 28,
            "Vaccin pentavalent. Deuxième dose à 10 semaines."
        ));
        
        // Penta 3 - 14 semaines (98 jours) - Pas de rappels
        calendrierRepository.save(creerCalendrierSenegal(
            TypeVaccin.PENTA_3, 98, 0, null,
            "Vaccin pentavalent. Troisième et dernière dose à 14 semaines."
        ));
        
        // Pneumocoque 1 - 6 semaines (42 jours) - 2 rappels à 28 jours
        calendrierRepository.save(creerCalendrierSenegal(
            TypeVaccin.PNEUMO_1, 42, 2, 28,
            "Vaccin contre le pneumocoque (PCV13). Première dose à 6 semaines."
        ));
        
        // Pneumocoque 2 - 10 semaines (70 jours) - 1 rappel à 28 jours
        calendrierRepository.save(creerCalendrierSenegal(
            TypeVaccin.PNEUMO_2, 70, 1, 28,
            "Vaccin pneumocoque. Deuxième dose à 10 semaines."
        ));
        
        // Pneumocoque 3 - 14 semaines (98 jours) - Pas de rappels
        calendrierRepository.save(creerCalendrierSenegal(
            TypeVaccin.PNEUMO_3, 98, 0, null,
            "Vaccin pneumocoque. Troisième et dernière dose à 14 semaines."
        ));
        
        // Rotavirus 1 - 6 semaines (42 jours) - 1 rappel à 28 jours
        calendrierRepository.save(creerCalendrierSenegal(
            TypeVaccin.ROTA_1, 42, 1, 28,
            "Vaccin contre le rotavirus. Première dose à 6 semaines."
        ));
        
        // Rotavirus 2 - 10 semaines (70 jours) - Pas de rappels
        calendrierRepository.save(creerCalendrierSenegal(
            TypeVaccin.ROTA_2, 70, 0, null,
            "Vaccin rotavirus. Deuxième et dernière dose à 10 semaines."
        ));
        
        // VAR - 9 mois (270 jours)
        calendrierRepository.save(creerCalendrierSenegal(
            TypeVaccin.VAR, 270, 0, null,
            "Vaccin Rougeole-Rubéole (VAR). À 9 mois."
        ));
        
        // Fièvre Jaune - 9 mois (270 jours)
        calendrierRepository.save(creerCalendrierSenegal(
            TypeVaccin.FIEVRE_JAUNE, 270, 0, null,
            "Vaccin contre la fièvre jaune. À 9 mois."
        ));
        
        // Méningite A - 9 mois (270 jours)
        calendrierRepository.save(creerCalendrierSenegal(
            TypeVaccin.MENINGITE, 270, 0, null,
            "Vaccin contre la méningite A. À 9 mois."
        ));
    }
    
    /**
     * Helper pour créer un calendrier avec données Sénégal.
     * 
     * @param vaccin Type vaccin
     * @param ageJours Âge recommandé en jours
     * @param nbRappels Nombre de rappels
     * @param delaiRappel Délai entre rappels (jours)
     * @param description Description
     * @return CalendrierVaccinal
     */
    private CalendrierVaccinal creerCalendrierSenegal(TypeVaccin vaccin, int ageJours, 
                                                      int nbRappels, Integer delaiRappel, 
                                                      String description) {
        CalendrierVaccinal calendrier = new CalendrierVaccinal();
        calendrier.setVaccin(vaccin);
        calendrier.setAgeRecommande(ageJours);
        calendrier.setNombreRappels(nbRappels);
        calendrier.setDelaiRappel(delaiRappel);
        calendrier.setObligatoire(true);
        calendrier.setDescription(description);
        return calendrier;
    }
}
