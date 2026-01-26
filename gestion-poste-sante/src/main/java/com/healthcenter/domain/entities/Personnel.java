package com.healthcenter.domain.entities;

import com.healthcenter.domain.enums.FonctionPersonnel;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * ENTITY - Personnel Médical
 * 
 * Représente le personnel du poste de santé (médecins, infirmiers, etc.).
 * Gère les informations personnelles, fonction, et relations avec consultations/vaccinations.
 * 
 * Relations :
 * - 1 Personnel → N Consultations (qui a réalisé quelle consultation)
 * - 1 Personnel → N Vaccinations (qui a administré quel vaccin)
 * - 1 Personnel → N DisponibilitePersonnel (planning horaires)
 * 
 * @author Health Center Team
 */
@Entity
@Table(name = "personnel")
public class Personnel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String nom;
    
    @Column(nullable = false, length = 100)
    private String prenom;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private FonctionPersonnel fonction;
    
    @Column(length = 200)
    private String specialisation;
    
    @Column(length = 20)
    private String telephone;
    
    @Column(length = 100)
    private String email;
    
    @Column(length = 500)
    private String adresse;
    
    @Column(unique = true, length = 50)
    private String numeroMatricule;
    
    @Column(name = "date_embauche")
    private LocalDate dateEmbauche;
    
    @Column(nullable = false)
    private Boolean actif = true;
    
    // Relations
    @OneToMany(mappedBy = "personnel", cascade = CascadeType.ALL)
    private List<Consultation> consultations = new ArrayList<>();
    
    @OneToMany(mappedBy = "personnel", cascade = CascadeType.ALL)
    private List<Vaccination> vaccinations = new ArrayList<>();
    
    @OneToMany(mappedBy = "personnel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DisponibilitePersonnel> disponibilites = new ArrayList<>();
    
    
    // ========== CONSTRUCTEURS ==========
    
    /**
     * Constructeur vide (requis par JPA).
     */
    public Personnel() {
    }
    
    /**
     * Constructeur avec champs obligatoires.
     * 
     * @param nom Nom de famille
     * @param prenom Prénom
     * @param fonction Fonction médicale
     */
    public Personnel(String nom, String prenom, FonctionPersonnel fonction) {
        this.nom = nom;
        this.prenom = prenom;
        this.fonction = fonction;
        this.actif = true;
    }
    
    
    // ========== MÉTHODES UTILITAIRES ==========
    
    /**
     * @return Nom complet "Prénom NOM"
     */
    public String getNomComplet() {
        return prenom + " " + nom.toUpperCase();
    }
    
    /**
     * @return Libellé lisible de la fonction (ex: "Médecin")
     */
    public String getFonctionLibelle() {
        return fonction != null ? fonction.getLibelle() : "";
    }
    
    /**
     * @return true si personnel est médical (contact patients), false si administratif
     */
    public boolean isPersonnelMedical() {
        return fonction != null && fonction.isEstMedical();
    }
    
    
    // ========== GETTERS / SETTERS ==========
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public FonctionPersonnel getFonction() {
        return fonction;
    }
    
    public void setFonction(FonctionPersonnel fonction) {
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
    
    public List<Consultation> getConsultations() {
        return consultations;
    }
    
    public void setConsultations(List<Consultation> consultations) {
        this.consultations = consultations;
    }
    
    public List<Vaccination> getVaccinations() {
        return vaccinations;
    }
    
    public void setVaccinations(List<Vaccination> vaccinations) {
        this.vaccinations = vaccinations;
    }
    
    public List<DisponibilitePersonnel> getDisponibilites() {
        return disponibilites;
    }
    
    public void setDisponibilites(List<DisponibilitePersonnel> disponibilites) {
        this.disponibilites = disponibilites;
    }
}
