package com.healthcenter.dto;

import java.time.LocalDateTime;

/**
 * DTO = Objet simple pour transfert UI ↔ Service.
 * 
 * DIFFÉRENCE avec Entity Consultation :
 * - Pas d'annotations JPA (@Entity, @Column, @ManyToOne)
 * - Pas d'objet Patient complet (uniquement patientId)
 * - Pas d'ID consultation (géré par DB après création)
 * - Validation UI-friendly (pas de contraintes DB)
 * 
 * Utilisé pour :
 * - Formulaires JavaFX (création/modification consultation)
 * - Transfert données Controller → Service
 * - Évite exposition Entity complète dans UI
 */
public class ConsultationDTO {
    
    /**
     * ID du patient concerné (foreign key).
     * Pas l'objet Patient complet pour éviter over-fetching.
     */
    private Long patientId;

    /**
     * ID du personnel médical affecté.
     */
    private Long personnelId;
    
    /**
     * Date et heure de la consultation.
     * LocalDateTime (Java 8+) = type moderne pour timestamps.
     */
    private LocalDateTime dateConsultation;
    
    /**
     * Symptômes rapportés par le patient.
     * Optionnel (peut être null).
     */
    private String symptomes;
    
    /**
     * Diagnostic posé par le personnel médical.
     * Optionnel (peut être null).
     */
    private String diagnostic;
    
    /**
     * Prescription médicale (médicaments + posologie).
     * Optionnel (peut être null).
     */
    private String prescription;
    
    
    // ========== CONSTRUCTEURS ==========
    
    /**
     * Constructeur vide (requis pour frameworks).
     */
    public ConsultationDTO() {
    }
    
    /**
     * Constructeur avec paramètres obligatoires.
     * Facilite la création en code.
     * 
     * @param patientId ID du patient (obligatoire)
     * @param dateConsultation Date/heure consultation (obligatoire)
     */
    public ConsultationDTO(Long patientId, LocalDateTime dateConsultation) {
        this.patientId = patientId;
        this.dateConsultation = dateConsultation;
    }
    
    /**
     * Constructeur complet avec tous les champs.
     * Utile pour mapping Entity → DTO.
     * 
     * @param patientId ID du patient
     * @param dateConsultation Date/heure consultation
     * @param symptomes Symptômes rapportés
     * @param diagnostic Diagnostic posé
     * @param prescription Prescription médicale
     */
    public ConsultationDTO(Long patientId, LocalDateTime dateConsultation, 
                          String symptomes, String diagnostic, String prescription) {
        this.patientId = patientId;
        this.dateConsultation = dateConsultation;
        this.symptomes = symptomes;
        this.diagnostic = diagnostic;
        this.prescription = prescription;
    }
    
    
    // ========== GETTERS / SETTERS (ENCAPSULATION) ==========
    
    public Long getPatientId() {
        return patientId;
    }
    
    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getPersonnelId() {
        return personnelId;
    }

    public void setPersonnelId(Long personnelId) {
        this.personnelId = personnelId;
    }
    
    public LocalDateTime getDateConsultation() {
        return dateConsultation;
    }
    
    public void setDateConsultation(LocalDateTime dateConsultation) {
        this.dateConsultation = dateConsultation;
    }
    
    public String getSymptomes() {
        return symptomes;
    }
    
    public void setSymptomes(String symptomes) {
        this.symptomes = symptomes;
    }
    
    public String getDiagnostic() {
        return diagnostic;
    }
    
    public void setDiagnostic(String diagnostic) {
        this.diagnostic = diagnostic;
    }
    
    public String getPrescription() {
        return prescription;
    }
    
    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }
    
    
    // ========== MÉTHODES UTILITAIRES ==========
    
    /**
     * toString() pour debug (affichage console).
     */
    @Override
    public String toString() {
        return "ConsultationDTO{" +
                "patientId=" + patientId +
            ", personnelId=" + personnelId +
                ", dateConsultation=" + dateConsultation +
                ", symptomes='" + symptomes + '\'' +
                ", diagnostic='" + diagnostic + '\'' +
                '}';
    }
    
    /**
     * Validation basique (appelée avant envoi au Service).
     * @return true si données minimales présentes
     */
    public boolean isValid() {
        return patientId != null && dateConsultation != null;
    }
}
