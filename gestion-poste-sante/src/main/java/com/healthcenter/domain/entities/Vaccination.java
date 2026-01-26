package com.healthcenter.domain.entities;

import com.healthcenter.domain.enums.StatutVaccination;
import com.healthcenter.domain.enums.TypeVaccin;
import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * ENTITY Vaccination = Représente une vaccination administrée à un patient.
 * 
 * Relation ManyToOne vers Patient (obligatoire).
 * Gère automatiquement le statut selon la date de rappel.
 */
@Entity
@Table(name = "vaccinations")
public class Vaccination {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
    
    /**
     * RELATION : Plusieurs vaccinations → 1 Personnel.
     * Personnel qui a administré la vaccination.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personnel_id")
    private Personnel personnel;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TypeVaccin vaccin;
    
    @Column(name = "date_administration", nullable = false)
    private LocalDate dateAdministration;
    
    @Column(name = "date_rappel")
    private LocalDate dateRappel;
    
    @Column(name = "numero_lot", length = 50)
    private String numeroLot;
    
    @Column(length = 500)
    private String observations;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private StatutVaccination statut;
    
    @Column(name = "date_creation", updatable = false)
    private LocalDate dateCreation;
    
    @Column(name = "date_mise_a_jour")
    private LocalDate dateMiseAJour;
    
    
    // ========== CONSTRUCTEURS ==========
    
    public Vaccination() {
    }
    
    public Vaccination(Patient patient, TypeVaccin vaccin, LocalDate dateAdministration) {
        this.patient = patient;
        this.vaccin = vaccin;
        this.dateAdministration = dateAdministration;
        this.statut = StatutVaccination.ADMINISTRE;
    }
    
    
    // ========== LIFECYCLE CALLBACKS ==========
    
    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDate.now();
        dateMiseAJour = LocalDate.now();
        if (statut == null) {
            statut = calculerStatut();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        dateMiseAJour = LocalDate.now();
        statut = calculerStatut();
    }
    
    
    // ========== MÉTHODES UTILITAIRES ==========
    
    /**
     * Vérifie si le rappel est proche (dans les 7 prochains jours).
     * 
     * @return true si date rappel existe et dans moins de 7 jours
     */
    public boolean isRappelProche() {
        if (dateRappel == null) {
            return false;
        }
        LocalDate dans7Jours = LocalDate.now().plusDays(7);
        return dateRappel.isAfter(LocalDate.now()) && dateRappel.isBefore(dans7Jours.plusDays(1));
    }
    
    /**
     * Vérifie si le rappel est en retard.
     * 
     * @return true si date rappel existe et est passée
     */
    public boolean isRappelEnRetard() {
        if (dateRappel == null) {
            return false;
        }
        return dateRappel.isBefore(LocalDate.now());
    }
    
    /**
     * Calcule le statut automatiquement selon la date de rappel.
     * 
     * @return Statut calculé
     */
    public StatutVaccination calculerStatut() {
        if (dateRappel == null) {
            return StatutVaccination.ADMINISTRE;
        }
        
        if (isRappelEnRetard()) {
            return StatutVaccination.RAPPEL_EN_RETARD;
        }
        
        return StatutVaccination.RAPPEL_PREVU;
    }
    
    
    // ========== GETTERS & SETTERS ==========
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Patient getPatient() {
        return patient;
    }
    
    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    
    public TypeVaccin getVaccin() {
        return vaccin;
    }
    
    public void setVaccin(TypeVaccin vaccin) {
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
    
    public StatutVaccination getStatut() {
        return statut;
    }
    
    public void setStatut(StatutVaccination statut) {
        this.statut = statut;
    }
    
    public LocalDate getDateCreation() {
        return dateCreation;
    }
    
    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }
    
    public LocalDate getDateMiseAJour() {
        return dateMiseAJour;
    }
    
    public void setDateMiseAJour(LocalDate dateMiseAJour) {
        this.dateMiseAJour = dateMiseAJour;
    }
    
    public Personnel getPersonnel() {
        return personnel;
    }
    
    public void setPersonnel(Personnel personnel) {
        this.personnel = personnel;
    }
}
