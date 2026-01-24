package com.healthcenter.domain.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "consultations")
public class Consultation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * RELATION inverse : Plusieurs consultations → 1 Patient.
     * @ManyToOne = N Consultations → 1 Patient
     * @JoinColumn = colonne SQL "patient_id" (foreign key)
     */
    @ManyToOne(fetch = FetchType.LAZY)  // LAZY = charge Patient uniquement si demandé
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
    
    @Column(nullable = false)
    private LocalDateTime dateConsultation;
    
    @Column(length = 500)
    private String symptomes;
    
    @Column(length = 500)
    private String diagnostic;
    
    @Column(length = 1000)
    private String prescription;
    
    // Constructeurs
    public Consultation() {}
    
    // Getters/Setters (génère-les toi-même ou avec IDE : Alt+Insert)
    
    public Long getId() { return id; }
    
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
    
    public LocalDateTime getDateConsultation() { return dateConsultation; }
    public void setDateConsultation(LocalDateTime dateConsultation) { 
        this.dateConsultation = dateConsultation; 
    }
    
    public String getSymptomes() { return symptomes; }
    public void setSymptomes(String symptomes) { this.symptomes = symptomes; }
    
    public String getDiagnostic() { return diagnostic; }
    public void setDiagnostic(String diagnostic) { this.diagnostic = diagnostic; }
    
    public String getPrescription() { return prescription; }
    public void setPrescription(String prescription) { this.prescription = prescription; }
}
