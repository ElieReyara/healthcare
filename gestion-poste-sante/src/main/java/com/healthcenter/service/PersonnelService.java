package com.healthcenter.service;

import com.healthcenter.domain.entities.DisponibilitePersonnel;
import com.healthcenter.domain.entities.Personnel;
import com.healthcenter.domain.enums.FonctionPersonnel;
import com.healthcenter.domain.enums.JourSemaine;
import com.healthcenter.dto.PersonnelDTO;
import com.healthcenter.repository.DisponibilitePersonnelRepository;
import com.healthcenter.repository.PersonnelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * SERVICE - Personnel
 * 
 * Couche métier pour gestion du personnel médical.
 * 
 * Responsabilités :
 * - CRUD personnel avec validation métier
 * - Statistiques activité (nb consultations)
 * - Gestion disponibilités/planning
 * - Activation/désactivation personnel
 * 
 * @author Health Center Team
 */
@Service
public class PersonnelService {
    
    @Autowired
    private PersonnelRepository personnelRepository;
    
    @Autowired
    private DisponibilitePersonnelRepository disponibiliteRepository;
    
    
    // ========== MÉTHODES CRUD ==========
    
    /**
     * Crée un nouveau personnel.
     * 
     * @param dto Données personnel
     * @return Personnel créé avec ID
     * @throws IllegalArgumentException si validation échoue
     */
    @Transactional
    public Personnel creerPersonnel(PersonnelDTO dto) {
        valider(dto);
        
        // Vérifier unicité matricule si fourni
        if (dto.getNumeroMatricule() != null && !dto.getNumeroMatricule().trim().isEmpty()) {
            Optional<Personnel> existant = personnelRepository.findByNumeroMatricule(dto.getNumeroMatricule());
            if (existant.isPresent()) {
                throw new IllegalArgumentException("Un personnel avec ce matricule existe déjà : " + dto.getNumeroMatricule());
            }
        }
        
        Personnel personnel = mapToEntity(dto);
        personnel.setActif(dto.getActif() != null ? dto.getActif() : true);
        
        return personnelRepository.save(personnel);
    }
    
    /**
     * Liste tout le personnel (actif + inactif).
     * 
     * @return Liste complète personnel
     */
    @Transactional(readOnly = true)
    public List<Personnel> obtenirToutPersonnel() {
        return personnelRepository.findAll();
    }
    
    /**
     * Liste SEULEMENT le personnel actif.
     * 
     * @return Liste personnel actif
     */
    @Transactional(readOnly = true)
    public List<Personnel> obtenirPersonnelActif() {
        return personnelRepository.findByActifTrue();
    }
    
    /**
     * Recherche personnel par ID.
     * 
     * @param id ID personnel
     * @return Optional personnel
     */
    @Transactional(readOnly = true)
    public Optional<Personnel> obtenirPersonnelParId(Long id) {
        return personnelRepository.findById(id);
    }
    
    /**
     * Recherche personnel par nom OU prénom.
     * 
     * @param recherche Terme recherché
     * @return Liste personnel correspondant
     */
    @Transactional(readOnly = true)
    public List<Personnel> rechercherPersonnelParNom(String recherche) {
        if (recherche == null || recherche.trim().isEmpty()) {
            return obtenirToutPersonnel();
        }
        return personnelRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(recherche, recherche);
    }
    
    /**
     * Filtre personnel par fonction.
     * 
     * @param fonction Fonction médicale
     * @return Liste personnel de cette fonction
     */
    @Transactional(readOnly = true)
    public List<Personnel> obtenirPersonnelParFonction(FonctionPersonnel fonction) {
        return personnelRepository.findByFonction(fonction);
    }
    
    /**
     * Recherche personnel par numéro matricule.
     * 
     * @param matricule Numéro matricule
     * @return Optional personnel
     */
    @Transactional(readOnly = true)
    public Optional<Personnel> obtenirPersonnelParMatricule(String matricule) {
        return personnelRepository.findByNumeroMatricule(matricule);
    }
    
    /**
     * Met à jour un personnel existant.
     * 
     * @param id ID personnel à modifier
     * @param dto Nouvelles données
     * @return Personnel modifié
     * @throws IllegalArgumentException si personnel inexistant ou validation échoue
     */
    @Transactional
    public Personnel mettreAJourPersonnel(Long id, PersonnelDTO dto) {
        valider(dto);
        
        Personnel personnel = personnelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Personnel introuvable avec ID : " + id));
        
        // Vérifier unicité matricule si modifié
        if (dto.getNumeroMatricule() != null && !dto.getNumeroMatricule().trim().isEmpty()) {
            Optional<Personnel> existant = personnelRepository.findByNumeroMatricule(dto.getNumeroMatricule());
            if (existant.isPresent() && !existant.get().getId().equals(id)) {
                throw new IllegalArgumentException("Un autre personnel a déjà ce matricule : " + dto.getNumeroMatricule());
            }
        }
        
        // Mise à jour champs
        personnel.setNom(dto.getNom());
        personnel.setPrenom(dto.getPrenom());
        personnel.setFonction(FonctionPersonnel.valueOf(dto.getFonction()));
        personnel.setSpecialisation(dto.getSpecialisation());
        personnel.setTelephone(dto.getTelephone());
        personnel.setEmail(dto.getEmail());
        personnel.setAdresse(dto.getAdresse());
        personnel.setNumeroMatricule(dto.getNumeroMatricule());
        personnel.setDateEmbauche(dto.getDateEmbauche());
        
        if (dto.getActif() != null) {
            personnel.setActif(dto.getActif());
        }
        
        return personnelRepository.save(personnel);
    }
    
    /**
     * Supprime un personnel.
     * 
     * ATTENTION : Vérifie relations avant suppression.
     * Si consultations/vaccinations liées → Exception (utiliser désactiverPersonnel à la place).
     * 
     * @param id ID personnel
     * @throws IllegalArgumentException si personnel introuvable ou a des relations
     */
    @Transactional
    public void supprimerPersonnel(Long id) {
        Personnel personnel = personnelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Personnel introuvable avec ID : " + id));
        
        // Vérifier relations
        if (!personnel.getConsultations().isEmpty() || !personnel.getVaccinations().isEmpty()) {
            throw new IllegalArgumentException(
                "Impossible de supprimer : ce personnel a des consultations ou vaccinations liées. " +
                "Utilisez la désactivation à la place."
            );
        }
        
        personnelRepository.deleteById(id);
    }
    
    /**
     * Désactive un personnel (soft delete).
     * 
     * @param id ID personnel
     * @return Personnel désactivé
     */
    @Transactional
    public Personnel desactiverPersonnel(Long id) {
        Personnel personnel = personnelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Personnel introuvable avec ID : " + id));
        
        personnel.setActif(false);
        return personnelRepository.save(personnel);
    }
    
    /**
     * Active un personnel.
     * 
     * @param id ID personnel
     * @return Personnel activé
     */
    @Transactional
    public Personnel activerPersonnel(Long id) {
        Personnel personnel = personnelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Personnel introuvable avec ID : " + id));
        
        personnel.setActif(true);
        return personnelRepository.save(personnel);
    }
    
    
    // ========== MÉTHODES STATISTIQUES ==========
    
    /**
     * Obtient statistiques consultations pour un personnel sur une période.
     * 
     * @param personnelId ID personnel
     * @param debut Date début période
     * @param fin Date fin période
     * @return Map avec clés : "nbConsultations", "dateDebut", "dateFin", "personnel"
     */
    @Transactional(readOnly = true)
    public Map<String, Object> obtenirStatistiquesConsultations(Long personnelId, LocalDate debut, LocalDate fin) {
        Personnel personnel = personnelRepository.findById(personnelId)
                .orElseThrow(() -> new IllegalArgumentException("Personnel introuvable avec ID : " + personnelId));
        
        LocalDateTime dateTimeDebut = debut.atStartOfDay();
        LocalDateTime dateTimeFin = fin.atTime(23, 59, 59);
        
        long nbConsultations = personnel.getConsultations().stream()
                .filter(c -> !c.getDateConsultation().isBefore(dateTimeDebut) && 
                            !c.getDateConsultation().isAfter(dateTimeFin))
                .count();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("nbConsultations", nbConsultations);
        stats.put("dateDebut", debut);
        stats.put("dateFin", fin);
        stats.put("personnel", personnel);
        
        return stats;
    }
    
    /**
     * Obtient le top N personnels les plus actifs sur une période.
     * 
     * @param debut Date début
     * @param fin Date fin
     * @param limit Nombre max résultats
     * @return Liste [Personnel, Long nbConsultations] triée décroissant
     */
    @Transactional(readOnly = true)
    public List<Object[]> obtenirTopPersonnelActif(LocalDate debut, LocalDate fin, int limit) {
        LocalDateTime dateTimeDebut = debut.atStartOfDay();
        LocalDateTime dateTimeFin = fin.atTime(23, 59, 59);
        
        List<Object[]> resultats = personnelRepository.compterConsultationsParPersonnel(dateTimeDebut, dateTimeFin);
        
        return resultats.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    /**
     * Compte personnel par fonction.
     * 
     * @return Map FonctionPersonnel → nombre personnel
     */
    @Transactional(readOnly = true)
    public Map<FonctionPersonnel, Long> compterPersonnelParFonction() {
        List<Personnel> toutPersonnel = personnelRepository.findAll();
        
        return toutPersonnel.stream()
                .collect(Collectors.groupingBy(
                    Personnel::getFonction,
                    Collectors.counting()
                ));
    }
    
    
    // ========== MÉTHODES DISPONIBILITÉS ==========
    
    /**
     * Obtient toutes les disponibilités d'un personnel.
     * 
     * @param personnelId ID personnel
     * @return Liste disponibilités
     */
    @Transactional(readOnly = true)
    public List<DisponibilitePersonnel> obtenirDisponibilites(Long personnelId) {
        return disponibiliteRepository.findByPersonnelId(personnelId);
    }
    
    /**
     * Vérifie si un personnel est disponible à une date/heure donnée.
     * 
     * @param personnelId ID personnel
     * @param date Date à vérifier
     * @param heure Heure à vérifier
     * @return true si disponible
     */
    @Transactional(readOnly = true)
    public boolean estDisponible(Long personnelId, LocalDate date, LocalTime heure) {
        // Convertir date → JourSemaine
        JourSemaine jour = JourSemaine.fromDayOfWeek(date.getDayOfWeek());
        
        // Chercher disponibilités actives ce jour
        List<DisponibilitePersonnel> dispos = disponibiliteRepository.findDisponibilitesActives(personnelId, jour, date);
        
        // Vérifier si heure dans un créneau
        for (DisponibilitePersonnel dispo : dispos) {
            if (!heure.isBefore(dispo.getHeureDebut()) && !heure.isAfter(dispo.getHeureFin())) {
                return true;
            }
        }
        
        return false;
    }
    
    
    // ========== MÉTHODES PRIVÉES ==========
    
    /**
     * Valide un PersonnelDTO.
     * 
     * @param dto DTO à valider
     * @throws IllegalArgumentException si validation échoue
     */
    private void valider(PersonnelDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Les données du personnel ne peuvent pas être nulles");
        }
        
        if (dto.getNom() == null || dto.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom est obligatoire");
        }
        
        if (dto.getPrenom() == null || dto.getPrenom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le prénom est obligatoire");
        }
        
        if (dto.getFonction() == null || dto.getFonction().trim().isEmpty()) {
            throw new IllegalArgumentException("La fonction est obligatoire");
        }
        
        // Vérifier que fonction existe
        try {
            FonctionPersonnel.valueOf(dto.getFonction());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Fonction invalide : " + dto.getFonction());
        }
    }
    
    /**
     * Convertit PersonnelDTO → Personnel Entity.
     * 
     * @param dto DTO source
     * @return Entity Personnel
     */
    private Personnel mapToEntity(PersonnelDTO dto) {
        Personnel personnel = new Personnel();
        personnel.setNom(dto.getNom());
        personnel.setPrenom(dto.getPrenom());
        personnel.setFonction(FonctionPersonnel.valueOf(dto.getFonction()));
        personnel.setSpecialisation(dto.getSpecialisation());
        personnel.setTelephone(dto.getTelephone());
        personnel.setEmail(dto.getEmail());
        personnel.setAdresse(dto.getAdresse());
        personnel.setNumeroMatricule(dto.getNumeroMatricule());
        personnel.setDateEmbauche(dto.getDateEmbauche());
        
        return personnel;
    }
}
