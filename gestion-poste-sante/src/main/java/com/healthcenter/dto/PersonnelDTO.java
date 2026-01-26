package com.healthcenter.dto;

import java.time.LocalDate;

/**
 * DTO - Personnel
 * 
 * Objet de transfert de données (POJO pur sans annotations JPA).
 * Utilisé pour :
 * - Transfert données UI → Service (création/modification)
 * - Découplage couches Présentation/Domain
 * 
 * @author Health Center Team
 */
public class PersonnelDTO {
    
    private String nom;
    private String prenom;
    private String fonction; // Nom de l'enum FonctionPersonnel
    private String specialisation;
    private String telephone;
    private String email;
    private String adresse;
    private String numeroMatricule;
    private LocalDate dateEmbauche;
    private Boolean actif;
    
    
    // ========== CONSTRUCTEURS ==========
    
    /**
     * Constructeur vide.
     */
    public PersonnelDTO() {
    }
    
    /**
     * Constructeur avec tous les paramètres.
     */
    public PersonnelDTO(String nom, String prenom, String fonction, String specialisation, 
                        String telephone, String email, String adresse, String numeroMatricule, 
                        LocalDate dateEmbauche, Boolean actif) {
        this.nom = nom;
        this.prenom = prenom;
        this.fonction = fonction;
        this.specialisation = specialisation;
        this.telephone = telephone;
        this.email = email;
        this.adresse = adresse;
        this.numeroMatricule = numeroMatricule;
        this.dateEmbauche = dateEmbauche;
        this.actif = actif;
    }
    
    
    // ========== GETTERS / SETTERS ==========
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getPrenom() {
        return prenom;
    }
    
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
    
    public String getFonction() {
        return fonction;
    }
    
    public void setFonction(String fonction) {
        this.fonction = fonction;
    }
    
    public String getSpecialisation() {
        return specialisation;
    }
    
    public void setSpecialisation(String specialisation) {
        this.specialisation = specialisation;
    }
    
    public String getTelephone() {
        return telephone;
    }
    
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getAdresse() {
        return adresse;
    }
    
    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
    
    public String getNumeroMatricule() {
        return numeroMatricule;
    }
    
    public void setNumeroMatricule(String numeroMatricule) {
        this.numeroMatricule = numeroMatricule;
    }
    
    public LocalDate getDateEmbauche() {
        return dateEmbauche;
    }
    
    public void setDateEmbauche(LocalDate dateEmbauche) {
        this.dateEmbauche = dateEmbauche;
    }
    
    public Boolean getActif() {
        return actif;
    }
    
    public void setActif(Boolean actif) {
        this.actif = actif;
    }
}
