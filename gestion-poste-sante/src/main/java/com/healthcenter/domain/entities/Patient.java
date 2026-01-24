package com.healthcenter.domain.entities;

import com.healthcenter.domain.enums.Sexe;
import jakarta.persistence.*;  // Annotations JPA (Jakarta = nouveau nom de Java EE)
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * ENTITY = Classe Java mappée à une table SQL.
 * Chaque instance = 1 ligne dans la table "patients".
 */
@Entity  // ← Dit à JPA : "Cette classe = table SQL"
@Table(name = "patients")  // ← Nom de table (optionnel, par défaut = nom classe en minuscule)
public class Patient {

    // ========== ATTRIBUTS (colonnes SQL) ==========
    
    /**
     * Clé primaire auto-incrémentée.
     * @Id = clé primaire
     * @GeneratedValue = PostgreSQL génère automatiquement (SERIAL)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Nom du patient (obligatoire, max 100 caractères).
     * @Column configure les contraintes SQL
     */
    @Column(nullable = false, length = 100)
    private String nom;
    
    @Column(nullable = false, length = 100)
    private String prenom;
    
    /**
     * Date de naissance.
     * LocalDate (Java 8+) = type moderne pour dates (remplace java.util.Date)
     */
    @Column(name = "date_naissance")  // ← Nom colonne SQL (snake_case)
    private LocalDate dateNaissance;
    
    /**
     * Sexe stocké comme STRING dans SQL.
     * @Enumerated(EnumType.STRING) = stocke "HOMME" pas "0"
     * Pourquoi STRING ? Si tu ajoutes AUTRE, les anciens index numériques cassent
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Sexe sexe;
    
    @Column(length = 20)
    private String telephone;
    
    @Column(length = 255)
    private String adresse;
    
    /**
     * Numéro unique du carnet de santé.
     * @Column(unique = true) = contrainte UNIQUE en SQL
     */
    @Column(name = "numero_carnet", unique = true, length = 50)
    private String numeroCarnet;
    
    /**
     * RELATION : Un patient a plusieurs consultations (1-to-Many).
     * 
     * @OneToMany = 1 Patient → N Consultations
     * mappedBy = "patient" → Dans Consultation.java, il y a un attribut "patient"
     * cascade = CascadeType.ALL → Si on supprime Patient, supprime ses Consultations
     * orphanRemoval = true → Si Consultation retirée de la liste, delete de la DB
     */
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Consultation> consultations = new ArrayList<>();
    
    
    // ========== CONSTRUCTEURS ==========
    
    /**
     * Constructeur vide OBLIGATOIRE pour JPA.
     * JPA utilise la réflexion pour créer des instances.
     */
    public Patient() {
    }
    
    /**
     * Constructeur avec paramètres (pour faciliter création en code).
     */
    public Patient(String nom, String prenom, LocalDate dateNaissance, Sexe sexe) {
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.sexe = sexe;
    }
    
    
    // ========== GETTERS / SETTERS (ENCAPSULATION) ==========
    
    public Long getId() {
        return id;
    }
    
    // Pas de setId() → ID géré uniquement par la DB
    
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
    
    public LocalDate getDateNaissance() {
        return dateNaissance;
    }
    
    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }
    
    public Sexe getSexe() {
        return sexe;
    }
    
    public void setSexe(Sexe sexe) {
        this.sexe = sexe;
    }
    
    public String getTelephone() {
        return telephone;
    }
    
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    
    public String getAdresse() {
        return adresse;
    }
    
    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
    
    public String getNumeroCarnet() {
        return numeroCarnet;
    }
    
    public void setNumeroCarnet(String numeroCarnet) {
        this.numeroCarnet = numeroCarnet;
    }
    
    public List<Consultation> getConsultations() {
        return consultations;
    }
    
    public void setConsultations(List<Consultation> consultations) {
        this.consultations = consultations;
    }
    
    
    // ========== MÉTHODES UTILITAIRES ==========
    
    /**
     * Calcule l'âge actuel du patient.
     * Logique métier simple dans l'Entity (acceptable si pure calcul).
     */
    public int getAge() {
        if (dateNaissance == null) return 0;
        return java.time.Period.between(dateNaissance, LocalDate.now()).getYears();
    }
    
    /**
     * toString() pour debug (affichage console).
     */
    @Override
    public String toString() {
        return "Patient{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", age=" + getAge() +
                '}';
    }
}
