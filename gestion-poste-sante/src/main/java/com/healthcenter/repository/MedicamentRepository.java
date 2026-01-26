package com.healthcenter.repository;

import com.healthcenter.domain.entities.Medicament;
import com.healthcenter.domain.enums.FormeMedicament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * REPOSITORY PATTERN : Interface d'accès données pour Medicament.
 * 
 * JpaRepository<Medicament, Long> :
 * - Medicament = type d'entité
 * - Long = type de la clé primaire (id)
 * 
 * Méthodes héritées automatiquement :
 * - save(Medicament) : INSERT ou UPDATE
 * - findById(Long) : SELECT par ID
 * - findAll() : SELECT *
 * - deleteById(Long) : DELETE
 * - count() : COUNT(*)
 */
@Repository
public interface MedicamentRepository extends JpaRepository<Medicament, Long> {
    
    /**
     * Recherche médicaments par nom (insensible à la casse, partielle).
     * 
     * findBy + Nom + Containing + IgnoreCase
     * → SQL : SELECT * FROM medicaments WHERE LOWER(nom) LIKE LOWER(:nom)
     * 
     * @param nom Nom ou partie du nom à rechercher
     * @return Liste des médicaments correspondants
     */
    List<Medicament> findByNomContainingIgnoreCase(String nom);
    
    /**
     * Recherche médicaments par forme pharmaceutique.
     * 
     * findBy + Forme
     * → SQL : SELECT * FROM medicaments WHERE forme = :forme
     * 
     * @param forme Forme pharmaceutique (enum)
     * @return Liste des médicaments de cette forme
     */
    List<Medicament> findByForme(FormeMedicament forme);
    
    /**
     * Recherche médicaments dont le stock actuel est inférieur à un seuil.
     * Utile pour alertes générales stock faible.
     * 
     * findBy + StockActuel + LessThan
     * → SQL : SELECT * FROM medicaments WHERE stock_actuel < :seuil
     * 
     * @param seuil Seuil de stock
     * @return Liste des médicaments avec stock < seuil
     */
    List<Medicament> findByStockActuelLessThan(Integer seuil);
    
    /**
     * Requête JPQL : Médicaments en rupture de stock.
     * Stock actuel < seuil d'alerte configuré.
     * 
     * Retourne uniquement les médicaments ayant un seuil défini (NOT NULL)
     * et dont le stock actuel est inférieur à ce seuil.
     * 
     * @return Liste des médicaments en alerte stock
     */
    @Query("SELECT m FROM Medicament m WHERE m.stockActuel < m.seuilAlerte AND m.seuilAlerte IS NOT NULL")
    List<Medicament> findMedicamentsEnRuptureStock();
}
