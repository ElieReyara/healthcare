package com.healthcenter.domain.entities;

import com.healthcenter.domain.enums.RoleUtilisateur;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity représentant un utilisateur de l'application
 * Utilisé pour l'authentification et la gestion des permissions
 */
@Entity
@Table(name = "utilisateurs")
public class Utilisateur {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    
    @Column(nullable = false, length = 255)
    private String password; // Hash BCrypt
    
    @Column(nullable = false, length = 100)
    private String nom;
    
    @Column(nullable = false, length = 100)
    private String prenom;
    
    @Column(length = 100)
    private String email;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleUtilisateur role;
    
    @Column(nullable = false)
    private Boolean actif = true;
    
    @Column(name = "date_creation")
    private LocalDateTime dateCreation;
    
    @Column(name = "derniere_connexion")
    private LocalDateTime derniereConnexion;
    
    @OneToOne
    @JoinColumn(name = "personnel_id")
    private Personnel personnel; // Liaison optionnelle avec Personnel médical
    
    // Constructeurs
    public Utilisateur() {
        this.dateCreation = LocalDateTime.now();
        this.actif = true;
    }
    
    public Utilisateur(String username, String password, String nom, String prenom, RoleUtilisateur role) {
        this();
        this.username = username;
        this.password = password;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
    }
    
    // Méthodes utilitaires
    public String getNomComplet() {
        return prenom + " " + nom;
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
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
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public RoleUtilisateur getRole() {
        return role;
    }
    
    public void setRole(RoleUtilisateur role) {
        this.role = role;
    }
    
    public Boolean getActif() {
        return actif;
    }
    
    public void setActif(Boolean actif) {
        this.actif = actif;
    }
    
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }
    
    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
    
    public LocalDateTime getDerniereConnexion() {
        return derniereConnexion;
    }
    
    public void setDerniereConnexion(LocalDateTime derniereConnexion) {
        this.derniereConnexion = derniereConnexion;
    }
    
    public Personnel getPersonnel() {
        return personnel;
    }
    
    public void setPersonnel(Personnel personnel) {
        this.personnel = personnel;
    }
}
