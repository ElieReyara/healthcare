package com.healthcenter.repository;

import com.healthcenter.domain.entities.DisponibilitePersonnel;
import com.healthcenter.domain.enums.JourSemaine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * REPOSITORY - Disponibilité Personnel
 * 
 * Interface d'accès aux données pour l'entity DisponibilitePersonnel.
 * Gère les créneaux horaires du personnel par jour de la semaine.
 * 
 * @author Health Center Team
 */
@Repository
public interface DisponibilitePersonnelRepository extends JpaRepository<DisponibilitePersonnel, Long> {
    
    /**
     * Liste toutes les disponibilités d'un personnel.
     * 
     * @param personnelId ID du personnel
     * @return Liste disponibilités (tous jours confondus)
     */
    List<DisponibilitePersonnel> findByPersonnelId(Long personnelId);
    
    /**
     * Liste disponibilités d'un personnel pour un jour spécifique.
     * 
     * @param personnelId ID du personnel
     * @param jour Jour de la semaine
     * @return Liste créneaux ce jour (peut être multiple : matin + après-midi)
     */
    List<DisponibilitePersonnel> findByPersonnelIdAndJourSemaine(Long personnelId, JourSemaine jour);
    
    /**
     * Liste tous les personnels disponibles un jour donné (tous personnels confondus).
     * 
     * @param jour Jour de la semaine
     * @return Liste disponibilités ce jour
     */
    List<DisponibilitePersonnel> findByJourSemaine(JourSemaine jour);
    
    /**
     * Trouve disponibilités actives d'un personnel à une date précise.
     * 
     * Conditions :
     * - Jour correspond à la date (ex: date = Lundi 15/01 → cherche LUNDI)
     * - dateDebut <= date (ou null = depuis toujours)
     * - dateFin >= date (ou null = permanent)
     * 
     * @param personnelId ID du personnel
     * @param jour Jour de la semaine extrait de la date
     * @param date Date à vérifier
     * @return Liste disponibilités valides cette date
     */
    @Query("SELECT d FROM DisponibilitePersonnel d " +
           "WHERE d.personnel.id = :personnelId " +
           "AND d.jourSemaine = :jour " +
           "AND (d.dateDebut IS NULL OR d.dateDebut <= :date) " +
           "AND (d.dateFin IS NULL OR d.dateFin >= :date)")
    List<DisponibilitePersonnel> findDisponibilitesActives(@Param("personnelId") Long personnelId, 
                                                            @Param("jour") JourSemaine jour, 
                                                            @Param("date") LocalDate date);
}
