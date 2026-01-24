package com.healthcenter.repository;

import com.healthcenter.domain.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * REPOSITORY PATTERN : Interface d'accès données.
 * 
 * JpaRepository<Patient, Long> :
 * - Patient = type d'entité
 * - Long = type de la clé primaire (id)
 * 
 * Méthodes héritées automatiquement :
 * - save(Patient) : INSERT ou UPDATE
 * - findById(Long) : SELECT par ID
 * - findAll() : SELECT *
 * - deleteById(Long) : DELETE
 * - count() : COUNT(*)
 */
@Repository  // ← Spring détecte et crée l'implémentation automatiquement
public interface PatientRepository extends JpaRepository<Patient, Long> {
    
    /**
     * Méthode de requête dérivée (Query Method).
     * JPA génère le SQL automatiquement via le nom de la méthode !
     * 
     * findBy + Nom + ContainingIgnoreCase
     * → SQL : SELECT * FROM patients WHERE LOWER(nom) LIKE LOWER('%:nom%')
     */
    List<Patient> findByNomContainingIgnoreCase(String nom);
    
    /**
     * Recherche exacte par numéro carnet (unique).
     * Optional = peut retourner null (Java 8+, évite NullPointerException)
     */
    Optional<Patient> findByNumeroCarnet(String numeroCarnet);
    
    /**
     * Requête custom avec JPQL (Java Persistence Query Language).
     * Syntaxe SQL-like mais orientée objets.
     * 
     * @Query : écris la requête manuellement
     * @Param : bind le paramètre
     */
    @Query("SELECT p FROM Patient p WHERE p.dateNaissance > :date ORDER BY p.dateNaissance")
    List<Patient> findPatientsNesApres(@Param("date") LocalDate date);
    
    /**
     * Compte patients par sexe.
     * JPQL fonctionne avec les attributs Java (pas colonnes SQL).
     */
    @Query("SELECT COUNT(p) FROM Patient p WHERE p.sexe = :sexe")
    long countBySexe(@Param("sexe") com.healthcenter.domain.enums.Sexe sexe);
}
