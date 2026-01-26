package com.healthcenter.service;

import com.healthcenter.domain.entities.DisponibilitePersonnel;
import com.healthcenter.domain.entities.Personnel;
import com.healthcenter.domain.enums.JourSemaine;
import com.healthcenter.dto.DisponibilitePersonnelDTO;
import com.healthcenter.repository.DisponibilitePersonnelRepository;
import com.healthcenter.repository.PersonnelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SERVICE - Disponibilité Personnel
 * 
 * Gère les créneaux horaires de disponibilité du personnel.
 * 
 * @author Health Center Team
 */
@Service
public class DisponibilitePersonnelService {
    
    @Autowired
    private DisponibilitePersonnelRepository disponibiliteRepository;
    
    @Autowired
    private PersonnelRepository personnelRepository;
    
    
    /**
     * Crée une nouvelle disponibilité.
     * 
     * @param dto Données disponibilité
     * @return Disponibilité créée
     * @throws IllegalArgumentException si validation échoue
     */
    @Transactional
    public DisponibilitePersonnel creerDisponibilite(DisponibilitePersonnelDTO dto) {
        valider(dto);
        
        Personnel personnel = personnelRepository.findById(dto.getPersonnelId())
                .orElseThrow(() -> new IllegalArgumentException("Personnel introuvable avec ID : " + dto.getPersonnelId()));
        
        DisponibilitePersonnel dispo = new DisponibilitePersonnel();
        dispo.setPersonnel(personnel);
        dispo.setJourSemaine(JourSemaine.valueOf(dto.getJourSemaine()));
        dispo.setHeureDebut(dto.getHeureDebut());
        dispo.setHeureFin(dto.getHeureFin());
        dispo.setDateDebut(dto.getDateDebut());
        dispo.setDateFin(dto.getDateFin());
        
        return disponibiliteRepository.save(dispo);
    }
    
    /**
     * Liste disponibilités d'un personnel.
     * 
     * @param personnelId ID personnel
     * @return Liste disponibilités
     */
    @Transactional(readOnly = true)
    public List<DisponibilitePersonnel> obtenirDisponibilitesParPersonnel(Long personnelId) {
        return disponibiliteRepository.findByPersonnelId(personnelId);
    }
    
    /**
     * Liste disponibilités pour un jour donné (tous personnels).
     * 
     * @param jour Jour semaine
     * @return Liste disponibilités ce jour
     */
    @Transactional(readOnly = true)
    public List<DisponibilitePersonnel> obtenirDisponibilitesJour(JourSemaine jour) {
        return disponibiliteRepository.findByJourSemaine(jour);
    }
    
    /**
     * Obtient disponibilités actives à une date précise.
     * 
     * @param personnelId ID personnel
     * @param date Date à vérifier
     * @return Liste disponibilités valides cette date
     */
    @Transactional(readOnly = true)
    public List<DisponibilitePersonnel> obtenirDisponibilitesActives(Long personnelId, LocalDate date) {
        JourSemaine jour = JourSemaine.fromDayOfWeek(date.getDayOfWeek());
        return disponibiliteRepository.findDisponibilitesActives(personnelId, jour, date);
    }
    
    /**
     * Met à jour une disponibilité.
     * 
     * @param id ID disponibilité
     * @param dto Nouvelles données
     * @return Disponibilité modifiée
     * @throws IllegalArgumentException si introuvable ou validation échoue
     */
    @Transactional
    public DisponibilitePersonnel mettreAJourDisponibilite(Long id, DisponibilitePersonnelDTO dto) {
        valider(dto);
        
        DisponibilitePersonnel dispo = disponibiliteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Disponibilité introuvable avec ID : " + id));
        
        dispo.setJourSemaine(JourSemaine.valueOf(dto.getJourSemaine()));
        dispo.setHeureDebut(dto.getHeureDebut());
        dispo.setHeureFin(dto.getHeureFin());
        dispo.setDateDebut(dto.getDateDebut());
        dispo.setDateFin(dto.getDateFin());
        
        return disponibiliteRepository.save(dispo);
    }
    
    /**
     * Supprime une disponibilité.
     * 
     * @param id ID disponibilité
     */
    @Transactional
    public void supprimerDisponibilite(Long id) {
        if (!disponibiliteRepository.existsById(id)) {
            throw new IllegalArgumentException("Disponibilité introuvable avec ID : " + id);
        }
        disponibiliteRepository.deleteById(id);
    }
    
    /**
     * Définit le planning complet d'un personnel.
     * 
     * Supprime anciennes disponibilités, crée nouvelles.
     * 
     * @param personnelId ID personnel
     * @param planning Map JourSemaine → Liste créneaux (ex: "08:00-12:00")
     * @return Liste nouvelles disponibilités créées
     */
    @Transactional
    public List<DisponibilitePersonnel> definirPlanning(Long personnelId, Map<JourSemaine, List<String>> planning) {
        Personnel personnel = personnelRepository.findById(personnelId)
                .orElseThrow(() -> new IllegalArgumentException("Personnel introuvable avec ID : " + personnelId));
        
        // Supprimer anciennes disponibilités
        List<DisponibilitePersonnel> anciennes = disponibiliteRepository.findByPersonnelId(personnelId);
        disponibiliteRepository.deleteAll(anciennes);
        
        // Créer nouvelles disponibilités
        List<DisponibilitePersonnel> nouvelles = new ArrayList<>();
        
        for (Map.Entry<JourSemaine, List<String>> entry : planning.entrySet()) {
            JourSemaine jour = entry.getKey();
            List<String> creneaux = entry.getValue();
            
            for (String creneau : creneaux) {
                // Parser "08:00-12:00"
                String[] parts = creneau.split("-");
                if (parts.length != 2) {
                    throw new IllegalArgumentException("Format créneau invalide : " + creneau + ". Format attendu : HH:mm-HH:mm");
                }
                
                LocalTime heureDebut = LocalTime.parse(parts[0].trim());
                LocalTime heureFin = LocalTime.parse(parts[1].trim());
                
                if (!heureDebut.isBefore(heureFin)) {
                    throw new IllegalArgumentException("Heure début doit être avant heure fin : " + creneau);
                }
                
                DisponibilitePersonnel dispo = new DisponibilitePersonnel(personnel, jour, heureDebut, heureFin);
                nouvelles.add(disponibiliteRepository.save(dispo));
            }
        }
        
        return nouvelles;
    }
    
    
    // ========== MÉTHODES PRIVÉES ==========
    
    /**
     * Valide un DisponibilitePersonnelDTO.
     * 
     * @param dto DTO à valider
     * @throws IllegalArgumentException si validation échoue
     */
    private void valider(DisponibilitePersonnelDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Les données de disponibilité ne peuvent pas être nulles");
        }
        
        if (dto.getPersonnelId() == null) {
            throw new IllegalArgumentException("L'ID du personnel est obligatoire");
        }
        
        if (dto.getJourSemaine() == null || dto.getJourSemaine().trim().isEmpty()) {
            throw new IllegalArgumentException("Le jour de la semaine est obligatoire");
        }
        
        if (dto.getHeureDebut() == null) {
            throw new IllegalArgumentException("L'heure de début est obligatoire");
        }
        
        if (dto.getHeureFin() == null) {
            throw new IllegalArgumentException("L'heure de fin est obligatoire");
        }
        
        if (!dto.getHeureDebut().isBefore(dto.getHeureFin())) {
            throw new IllegalArgumentException("L'heure de début doit être avant l'heure de fin");
        }
        
        // Vérifier que jour existe
        try {
            JourSemaine.valueOf(dto.getJourSemaine());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Jour invalide : " + dto.getJourSemaine());
        }
    }
}
