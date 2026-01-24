package com.healthcenter.dto;

import com.healthcenter.domain.enums.Sexe;
import java.time.LocalDate;

/**
 * DTO = Objet simple pour transfert UI ↔ Service.
 * 
 * DIFFÉRENCE avec Entity :
 * - Pas d'annotations JPA
 * - Pas de relations (consultations)
 * - Pas d'ID (géré par DB)
 * - Validation UI-friendly
 */
public class PatientDTO {
    
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private Sexe sexe;
    private String telephone;
    private String adresse;
    private String numeroCarnet;
    
    // Constructeur vide
    public PatientDTO() {}
    
    // Constructeur complet
    public PatientDTO(String nom, String prenom, LocalDate dateNaissance, Sexe sexe) {
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.sexe = sexe;
    }
    
    // Getters/Setters (génère avec IDE ou manuellement)
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    
    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { 
        this.dateNaissance = dateNaissance; 
    }
    
    public Sexe getSexe() { return sexe; }
    public void setSexe(Sexe sexe) { this.sexe = sexe; }
    
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    
    public String getNumeroCarnet() { return numeroCarnet; }
    public void setNumeroCarnet(String numeroCarnet) { 
        this.numeroCarnet = numeroCarnet; 
    }
}
