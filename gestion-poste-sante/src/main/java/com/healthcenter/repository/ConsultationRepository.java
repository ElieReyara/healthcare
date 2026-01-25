package com.healthcenter.repository;

import com.healthcenter.domain.entities.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REPOSITORY PATTERN : Interface d'accès données pour Consultation.
 * 
 * JpaRepository<Consultation, Long> :
 * - Consultation = type d'entité
 * - Long = type de la clé primaire (id)
 * 
 * Méthodes héritées automatiquement :
 * - save(Consultation) : INSERT ou UPDATE
 * - findById(Long) : SELECT par ID
 * - findAll() : SELECT *
 * - deleteById(Long) : DELETE
 * - count() : COUNT(*)
 */
@Repository  // ← Spring détecte et crée l'implémentation automatiquement
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    
    /**
     * Méthode de requête dérivée (Query Method).
     * JPA génère le SQL automatiquement via le nom de la méthode.
     * 
     * findBy + Patient + Id
     * → SQL : SELECT * FROM consultations WHERE patient_id = :patientId
     * 
     * @param patientId ID du patient
     * @return Liste des consultations du patient (peut être vide)
     */
    List<Consultation> findByPatientId(Long patientId);
    
    /**
     * Recherche consultations dans un intervalle de dates.
     * 
     * findBy + DateConsultation + Between
     * → SQL : SELECT * FROM consultations 
     *         WHERE date_consultation BETWEEN :debut AND :fin
     * 
     * @param debut Date/heure début (inclusive)
     * @param fin Date/heure fin (inclusive)
     * @return Liste des consultations dans l'intervalle
     */
    List<Consultation> findByDateConsultationBetween(LocalDateTime debut, LocalDateTime fin);
    
    /**
     * Recherche consultations d'un patient triées par date décroissante.
     * Utile pour afficher l'historique (plus récentes en premier).
     * 
     * findBy + PatientId + OrderBy + DateConsultation + Desc
     * → SQL : SELECT * FROM consultations 
     *         WHERE patient_id = :patientId 
     *         ORDER BY date_consultation DESC
     * 
     * @param patientId ID du patient
     * @return Liste triée (plus récentes en premier)
     */
    List<Consultation> findByPatientIdOrderByDateConsultationDesc(Long patientId);
    
    /**
     * Requête JPQL custom : Compte consultations d'un patient.
     * Alternative à findByPatientId().size() (plus performant).
     * 
     * @param patientId ID du patient
     * @return Nombre de consultations
     */
    @Query("SELECT COUNT(c) FROM Consultation c WHERE c.patient.id = :patientId")
    long countByPatientId(@Param("patientId") Long patientId);
    
    /**
     * Requête JPQL : Recherche consultations avec diagnostic non vide.
     * Utile pour statistiques (consultations avec diagnostic posé).
     * 
     * @return Liste des consultations avec diagnostic
     */
    @Query("SELECT c FROM Consultation c WHERE c.diagnostic IS NOT NULL AND c.diagnostic != ''")
    List<Consultation> findConsultationsAvecDiagnostic();
    
    /**
     * Requête JPQL : Consultations récentes (dernières 30 jours).
     * 
     * @param dateDebut Date limite (ex: LocalDateTime.now().minusDays(30))
     * @return Liste consultations récentes
     */
    @Query("SELECT c FROM Consultation c WHERE c.dateConsultation >= :dateDebut ORDER BY c.dateConsultation DESC")
    List<Consultation> findConsultationsRecentes(@Param("dateDebut") LocalDateTime dateDebut);
}
