package com.healthcenter.dto;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO - Disponibilité Personnel
 * 
 * Objet de transfert de données pour les créneaux horaires.
 * 
 * @author Health Center Team
 */
public class DisponibilitePersonnelDTO {
    
    private Long personnelId;
    private String jourSemaine; // Nom de l'enum JourSemaine
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    
    
    // ========== CONSTRUCTEURS ==========
    
    /**
     * Constructeur vide.
     */
    public DisponibilitePersonnelDTO() {
    }
    
    /**
     * Constructeur avec tous les paramètres.
     */
    public DisponibilitePersonnelDTO(Long personnelId, String jourSemaine, LocalTime heureDebut, 
                                      LocalTime heureFin, LocalDate dateDebut, LocalDate dateFin) {
        this.personnelId = personnelId;
        this.jourSemaine = jourSemaine;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }
    
    
    // ========== GETTERS / SETTERS ==========
    
    public Long getPersonnelId() {
        return personnelId;
    }
    
    public void setPersonnelId(Long personnelId) {
        this.personnelId = personnelId;
    }
    
    public String getJourSemaine() {
        return jourSemaine;
    }
    
    public void setJourSemaine(String jourSemaine) {
        this.jourSemaine = jourSemaine;
    }
    
    public LocalTime getHeureDebut() {
        return heureDebut;
    }
    
    public void setHeureDebut(LocalTime heureDebut) {
        this.heureDebut = heureDebut;
    }
    
    public LocalTime getHeureFin() {
        return heureFin;
    }
    
    public void setHeureFin(LocalTime heureFin) {
        this.heureFin = heureFin;
    }
    
    public LocalDate getDateDebut() {
        return dateDebut;
    }
    
    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }
    
    public LocalDate getDateFin() {
        return dateFin;
    }
    
    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }
}
