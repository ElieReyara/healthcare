package com.healthcenter.dto;

import java.time.LocalDate;

/**
 * DTO Vaccination = Objet transfert données pour vaccinations (UI ↔ Service).
 * 
 * POJO sans annotations JPA.
 */
public class VaccinationDTO {
    
    private Long patientId;
    private String vaccin; // Nom enum TypeVaccin
    private LocalDate dateAdministration;
    private LocalDate dateRappel;
    private String numeroLot;
    private String observations;
    
    
    // ========== CONSTRUCTEURS ==========
    
    public VaccinationDTO() {
    }
    
    public VaccinationDTO(Long patientId, String vaccin, LocalDate dateAdministration, 
                         LocalDate dateRappel, String numeroLot, String observations) {
        this.patientId = patientId;
        this.vaccin = vaccin;
        this.dateAdministration = dateAdministration;
        this.dateRappel = dateRappel;
        this.numeroLot = numeroLot;
        this.observations = observations;
    }
    
    
    // ========== GETTERS & SETTERS ==========
    
    public Long getPatientId() {
        return patientId;
    }
    
    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }
    
    public String getVaccin() {
        return vaccin;
    }
    
    public void setVaccin(String vaccin) {
        this.vaccin = vaccin;
    }
    
    public LocalDate getDateAdministration() {
        return dateAdministration;
    }
    
    public void setDateAdministration(LocalDate dateAdministration) {
        this.dateAdministration = dateAdministration;
    }
    
    public LocalDate getDateRappel() {
        return dateRappel;
    }
    
    public void setDateRappel(LocalDate dateRappel) {
        this.dateRappel = dateRappel;
    }
    
    public String getNumeroLot() {
        return numeroLot;
    }
    
    public void setNumeroLot(String numeroLot) {
        this.numeroLot = numeroLot;
    }
    
    public String getObservations() {
        return observations;
    }
    
    public void setObservations(String observations) {
        this.observations = observations;
    }
}
