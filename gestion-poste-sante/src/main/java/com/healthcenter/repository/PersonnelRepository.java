package com.healthcenter.repository;

import com.healthcenter.domain.entities.Personnel;
import com.healthcenter.domain.enums.FonctionPersonnel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * REPOSITORY - Personnel
 * 
 * Interface d'accès aux données pour l'entity Personnel.
 * Hérite de JpaRepository pour CRUD auto-généré + méthodes custom.
 * 
 * Méthodes nommées auto-générées par Spring Data JPA :
 * - findByXxx() → SELECT WHERE
 * - countByXxx() → SELECT COUNT WHERE
 * 
 * Méthodes @Query pour requêtes complexes JPQL.
 * 
 * @author Health Center Team
 */
@Repository
public interface PersonnelRepository extends JpaRepository<Personnel, Long> {
    
    /**
     * Recherche personnel par nom OU prénom (insensible casse).
     * 
     * @param nom Terme recherché dans nom
     * @param prenom Terme recherché dans prénom
     * @return Liste personnel dont nom ou prénom contient terme
     */
    List<Personnel> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom);
    
    /**
     * Filtre personnel par fonction.
     * 
     * @param fonction Fonction médicale
     * @return Liste personnel de cette fonction
     */
    List<Personnel> findByFonction(FonctionPersonnel fonction);
    
    /**
     * Recherche personnel par numéro matricule unique.
     * 
     * @param matricule Numéro matricule
     * @return Optional personnel (vide si inexistant)
     */
    Optional<Personnel> findByNumeroMatricule(String matricule);
    
    /**
     * Liste SEULEMENT le personnel actif (actif = true).
     * 
     * @return Liste personnel actif
     */
    List<Personnel> findByActifTrue();
    
    /**
     * Liste SEULEMENT le personnel inactif/parti (actif = false).
     * 
     * @return Liste personnel inactif
     */
    List<Personnel> findByActifFalse();
    
    /**
     * Filtre personnel par période d'embauche.
     * 
     * @param debut Date début période
     * @param fin Date fin période
     * @return Liste personnel embauché entre ces dates
     */
    List<Personnel> findByDateEmbaucheBetween(LocalDate debut, LocalDate fin);
    
    /**
     * Filtre personnel actif par fonctions multiples.
     * 
     * @param fonctions Liste fonctions recherchées
     * @return Liste personnel actif de ces fonctions
     */
    @Query("SELECT p FROM Personnel p WHERE p.fonction IN :fonctions AND p.actif = true")
    List<Personnel> findByFonctionsActifs(@Param("fonctions") List<FonctionPersonnel> fonctions);
    
    /**
     * Compte consultations par personnel sur une période.
     * 
     * @param debut Date/heure début période
     * @param fin Date/heure fin période
     * @return Liste [Personnel, Long nbConsultations] triée par nb décroissant
     */
    @Query("SELECT p, COUNT(c) FROM Personnel p LEFT JOIN p.consultations c " +
           "WHERE c.dateConsultation BETWEEN :debut AND :fin " +
           "GROUP BY p ORDER BY COUNT(c) DESC")
    List<Object[]> compterConsultationsParPersonnel(@Param("debut") LocalDateTime debut, 
                                                     @Param("fin") LocalDateTime fin);
}
