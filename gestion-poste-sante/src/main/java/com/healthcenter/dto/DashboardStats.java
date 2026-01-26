package com.healthcenter.dto;

import java.time.LocalDateTime;

/**
 * DTO regroupant toutes les statistiques du dashboard principal.
 * Agrège les KPIs du poste de santé.
 */
public class DashboardStats {
    
    private Long nbTotalPatients;
    private Long nbPatientsMois;
    private Long nbTotalConsultations;
    private Long nbConsultationsMois;
    private Long nbConsultationsSemaine;
    private Long nbConsultationsAujourdhui;
    private Long nbTotalVaccinations;
    private Long nbVaccinationsMois;
    private Long nbMedicamentsStock;
    private Long nbMedicamentsRupture;
    private Long nbPersonnelActif;
    private LocalDateTime dateGeneration;
    
    /**
     * Constructeur vide.
     */
    public DashboardStats() {
        this.dateGeneration = LocalDateTime.now();
    }
    
    /**
     * Constructeur avec tous les paramètres.
     */
    public DashboardStats(Long nbTotalPatients, Long nbPatientsMois, Long nbTotalConsultations,
                         Long nbConsultationsMois, Long nbConsultationsSemaine, Long nbConsultationsAujourdhui,
                         Long nbTotalVaccinations, Long nbVaccinationsMois, Long nbMedicamentsStock,
                         Long nbMedicamentsRupture, Long nbPersonnelActif) {
        this();
        this.nbTotalPatients = nbTotalPatients;
        this.nbPatientsMois = nbPatientsMois;
        this.nbTotalConsultations = nbTotalConsultations;
        this.nbConsultationsMois = nbConsultationsMois;
        this.nbConsultationsSemaine = nbConsultationsSemaine;
        this.nbConsultationsAujourdhui = nbConsultationsAujourdhui;
        this.nbTotalVaccinations = nbTotalVaccinations;
        this.nbVaccinationsMois = nbVaccinationsMois;
        this.nbMedicamentsStock = nbMedicamentsStock;
        this.nbMedicamentsRupture = nbMedicamentsRupture;
        this.nbPersonnelActif = nbPersonnelActif;
    }
    
    // Getters et Setters
    
    public Long getNbTotalPatients() {
        return nbTotalPatients;
    }
    
    public void setNbTotalPatients(Long nbTotalPatients) {
        this.nbTotalPatients = nbTotalPatients;
    }
    
    public Long getNbPatientsMois() {
        return nbPatientsMois;
    }
    
    public void setNbPatientsMois(Long nbPatientsMois) {
        this.nbPatientsMois = nbPatientsMois;
    }
    
    public Long getNbTotalConsultations() {
        return nbTotalConsultations;
    }
    
    public void setNbTotalConsultations(Long nbTotalConsultations) {
        this.nbTotalConsultations = nbTotalConsultations;
    }
    
    public Long getNbConsultationsMois() {
        return nbConsultationsMois;
    }
    
    public void setNbConsultationsMois(Long nbConsultationsMois) {
        this.nbConsultationsMois = nbConsultationsMois;
    }
    
    public Long getNbConsultationsSemaine() {
        return nbConsultationsSemaine;
    }
    
    public void setNbConsultationsSemaine(Long nbConsultationsSemaine) {
        this.nbConsultationsSemaine = nbConsultationsSemaine;
    }
    
    public Long getNbConsultationsAujourdhui() {
        return nbConsultationsAujourdhui;
    }
    
    public void setNbConsultationsAujourdhui(Long nbConsultationsAujourdhui) {
        this.nbConsultationsAujourdhui = nbConsultationsAujourdhui;
    }
    
    public Long getNbTotalVaccinations() {
        return nbTotalVaccinations;
    }
    
    public void setNbTotalVaccinations(Long nbTotalVaccinations) {
        this.nbTotalVaccinations = nbTotalVaccinations;
    }
    
    public Long getNbVaccinationsMois() {
        return nbVaccinationsMois;
    }
    
    public void setNbVaccinationsMois(Long nbVaccinationsMois) {
        this.nbVaccinationsMois = nbVaccinationsMois;
    }
    
    public Long getNbMedicamentsStock() {
        return nbMedicamentsStock;
    }
    
    public void setNbMedicamentsStock(Long nbMedicamentsStock) {
        this.nbMedicamentsStock = nbMedicamentsStock;
    }
    
    public Long getNbMedicamentsRupture() {
        return nbMedicamentsRupture;
    }
    
    public void setNbMedicamentsRupture(Long nbMedicamentsRupture) {
        this.nbMedicamentsRupture = nbMedicamentsRupture;
    }
    
    public Long getNbPersonnelActif() {
        return nbPersonnelActif;
    }
    
    public void setNbPersonnelActif(Long nbPersonnelActif) {
        this.nbPersonnelActif = nbPersonnelActif;
    }
    
    public LocalDateTime getDateGeneration() {
        return dateGeneration;
    }
    
    public void setDateGeneration(LocalDateTime dateGeneration) {
        this.dateGeneration = dateGeneration;
    }
}
