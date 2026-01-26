package com.healthcenter.repository;

import com.healthcenter.domain.entities.Rapport;
import com.healthcenter.domain.enums.TypeRapport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository pour l'entité Rapport.
 * Gère la persistence de l'historique des rapports générés.
 */
@Repository
public interface RapportRepository extends JpaRepository<Rapport, Long> {
    
    /**
     * Trouve tous les rapports d'un type donné.
     *
     * @param type Le type de rapport
     * @return La liste des rapports
     */
    List<Rapport> findByTypeRapport(TypeRapport type);
    
    /**
     * Trouve les rapports générés dans une période donnée.
     *
     * @param debut La date de début
     * @param fin La date de fin
     * @return La liste des rapports
     */
    List<Rapport> findByDateGenerationBetween(LocalDateTime debut, LocalDateTime fin);
    
    /**
     * Trouve les 10 derniers rapports générés.
     *
     * @return La liste des 10 derniers rapports
     */
    List<Rapport> findTop10ByOrderByDateGenerationDesc();
}
