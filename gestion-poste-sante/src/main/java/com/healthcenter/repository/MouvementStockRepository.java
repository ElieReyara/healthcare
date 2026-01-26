package com.healthcenter.repository;

import com.healthcenter.domain.entities.MouvementStock;
import com.healthcenter.domain.enums.TypeMouvement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REPOSITORY PATTERN : Interface d'accès données pour MouvementStock.
 * 
 * JpaRepository<MouvementStock, Long> :
 * - MouvementStock = type d'entité
 * - Long = type de la clé primaire (id)
 * 
 * Méthodes héritées automatiquement :
 * - save(MouvementStock) : INSERT ou UPDATE
 * - findById(Long) : SELECT par ID
 * - findAll() : SELECT *
 * - deleteById(Long) : DELETE
 * - count() : COUNT(*)
 */
@Repository
public interface MouvementStockRepository extends JpaRepository<MouvementStock, Long> {
    
    /**
     * Recherche mouvements d'un médicament (triés par date décroissante).
     * Utile pour afficher historique (plus récents en premier).
     * 
     * findBy + MedicamentId + OrderBy + DateMouvement + Desc
     * → SQL : SELECT * FROM mouvements_stock 
     *         WHERE medicament_id = :medicamentId 
     *         ORDER BY date_mouvement DESC
     * 
     * @param medicamentId ID du médicament
     * @return Liste triée par date décroissante
     */
    List<MouvementStock> findByMedicamentIdOrderByDateMouvementDesc(Long medicamentId);
    
    /**
     * Recherche mouvements dans un intervalle de dates.
     * Utile pour rapports mensuels/annuels.
     * 
     * findBy + DateMouvement + Between
     * → SQL : SELECT * FROM mouvements_stock 
     *         WHERE date_mouvement BETWEEN :debut AND :fin
     * 
     * @param debut Date/heure début (inclusive)
     * @param fin Date/heure fin (inclusive)
     * @return Liste des mouvements dans l'intervalle
     */
    List<MouvementStock> findByDateMouvementBetween(LocalDateTime debut, LocalDateTime fin);
    
    /**
     * Recherche mouvements par type (ENTREE ou SORTIE).
     * 
     * findBy + Type
     * → SQL : SELECT * FROM mouvements_stock WHERE type = :type
     * 
     * @param type Type de mouvement (enum)
     * @return Liste des mouvements de ce type
     */
    List<MouvementStock> findByType(TypeMouvement type);
}
