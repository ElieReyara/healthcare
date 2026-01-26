package com.healthcenter.repository;

import com.healthcenter.domain.entities.Vaccination;
import com.healthcenter.domain.enums.StatutVaccination;
import com.healthcenter.domain.enums.TypeVaccin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * REPOSITORY Vaccination = Accès données vaccinations.
 * 
 * Requêtes personnalisées pour filtrage et recherche de rappels.
 */
@Repository
public interface VaccinationRepository extends JpaRepository<Vaccination, Long> {
    
    /**
     * Trouve toutes les vaccinations d'un patient.
     * 
     * @param patientId ID du patient
     * @return Liste vaccinations
     */
    List<Vaccination> findByPatientId(Long patientId);
    
    /**
     * Trouve toutes les vaccinations d'un patient triées par date décroissante.
     * 
     * @param patientId ID du patient
     * @return Liste vaccinations (plus récentes en premier)
     */
    List<Vaccination> findByPatientIdOrderByDateAdministrationDesc(Long patientId);
    
    /**
     * Trouve toutes les vaccinations pour un type de vaccin donné.
     * 
     * @param vaccin Type de vaccin
     * @return Liste vaccinations
     */
    List<Vaccination> findByVaccin(TypeVaccin vaccin);
    
    /**
     * Trouve les vaccinations dans une période donnée.
     * 
     * @param debut Date début
     * @param fin Date fin
     * @return Liste vaccinations
     */
    List<Vaccination> findByDateAdministrationBetween(LocalDate debut, LocalDate fin);
    
    /**
     * Trouve les vaccinations par statut.
     * 
     * @param statut Statut recherché
     * @return Liste vaccinations
     */
    List<Vaccination> findByStatut(StatutVaccination statut);
    
    /**
     * Trouve les rappels en retard (date rappel passée, statut != ADMINISTRE).
     * 
     * @param date Date de référence (généralement aujourd'hui)
     * @return Liste vaccinations en retard
     */
    @Query("SELECT v FROM Vaccination v WHERE v.dateRappel IS NOT NULL " +
           "AND v.dateRappel < :date AND v.statut != 'ADMINISTRE'")
    List<Vaccination> findRappelsEnRetard(@Param("date") LocalDate date);
    
    /**
     * Trouve les rappels prochains dans une période donnée.
     * 
     * @param dateDebut Date début période
     * @param dateFin Date fin période
     * @return Liste vaccinations avec rappels prévus
     */
    @Query("SELECT v FROM Vaccination v WHERE v.dateRappel BETWEEN :dateDebut AND :dateFin " +
           "AND v.statut = 'RAPPEL_PREVU'")
    List<Vaccination> findRappelsProchains(@Param("dateDebut") LocalDate dateDebut, 
                                           @Param("dateFin") LocalDate dateFin);
    
    /**
     * Compte le nombre de vaccinations par type de vaccin.
     * Utilisé pour statistiques couverture vaccinale.
     * 
     * @return Liste [TypeVaccin, COUNT] pour chaque vaccin
     */
    @Query("SELECT v.vaccin, COUNT(v) FROM Vaccination v GROUP BY v.vaccin")
    List<Object[]> compterParTypeVaccin();
}
