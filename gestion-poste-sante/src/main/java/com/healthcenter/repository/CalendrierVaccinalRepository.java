package com.healthcenter.repository;

import com.healthcenter.domain.entities.CalendrierVaccinal;
import com.healthcenter.domain.enums.TypeVaccin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * REPOSITORY CalendrierVaccinal = Accès données calendrier vaccinal.
 * 
 * Gère le schéma vaccinal de référence (Sénégal).
 */
@Repository
public interface CalendrierVaccinalRepository extends JpaRepository<CalendrierVaccinal, Long> {
    
    /**
     * Trouve le calendrier pour un vaccin spécifique.
     * 
     * @param vaccin Type de vaccin
     * @return Optional CalendrierVaccinal
     */
    Optional<CalendrierVaccinal> findByVaccin(TypeVaccin vaccin);
    
    /**
     * Trouve tous les vaccins obligatoires.
     * 
     * @return Liste calendriers vaccins obligatoires
     */
    List<CalendrierVaccinal> findByObligatoireTrue();
    
    /**
     * Trouve tous les calendriers triés par âge recommandé croissant.
     * Utile pour afficher le calendrier vaccinal dans l'ordre chronologique.
     * 
     * @return Liste calendriers triée par âge
     */
    List<CalendrierVaccinal> findAllByOrderByAgeRecommandeAsc();
}
