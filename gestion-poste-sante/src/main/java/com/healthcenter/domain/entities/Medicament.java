package com.healthcenter.domain.entities;

import com.healthcenter.domain.enums.FormeMedicament;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * ENTITY = Classe Java mappée à la table "medicaments".
 * 
 * Représente un médicament avec gestion de stock automatique.
 * Relation : 1 Medicament → N MouvementStock (historique).
 */
@Entity
@Table(name = "medicaments")
public class Medicament {
    
    // ========== ATTRIBUTS (colonnes SQL) ==========
    
    /**
     * Clé primaire auto-incrémentée.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Nom commercial du médicament.
     * @Column(nullable = false) = champ obligatoire en SQL.
     */
    @Column(nullable = false, length = 200)
    private String nom;
    
    /**
     * Dosage (concentration).
     * Exemples : "500mg", "10ml", "250UI"
     */
    @Column(length = 50)
    private String dosage;
    
    /**
     * Forme pharmaceutique (enum).
     * @Enumerated(STRING) = stocke "COMPRIME" au lieu de 0 (lisible en DB).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FormeMedicament forme;
    
    /**
     * Prix unitaire en FCFA (Francs CFA).
     * BigDecimal = précision financière (pas de float !).
     * precision = 10 chiffres total, scale = 2 décimales.
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal prix;
    
    /**
     * Stock actuel disponible.
     * Mis à jour automatiquement par MouvementStock.
     */
    @Column(nullable = false)
    private Integer stockActuel = 0;
    
    /**
     * Seuil d'alerte pour rupture de stock.
     * Si stockActuel < seuilAlerte → alerte visuelle.
     * Null = pas d'alerte configurée.
     */
    @Column(name = "seuil_alerte")
    private Integer seuilAlerte;
    
    /**
     * Relation inverse : Historique des mouvements de stock.
     * 
     * @OneToMany(mappedBy = "medicament") : 1 Medicament → N MouvementStock
     * cascade = ALL : Si suppression Medicament → supprime mouvements liés
     * orphanRemoval = true : Si mouvement retiré de liste → supprimé en DB
     */
    @OneToMany(mappedBy = "medicament", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MouvementStock> mouvements = new ArrayList<>();
    
    
    // ========== CONSTRUCTEURS ==========
    
    /**
     * Constructeur vide OBLIGATOIRE pour JPA.
     */
    public Medicament() {
    }
    
    /**
     * Constructeur avec paramètres essentiels.
     * 
     * @param nom Nom médicament
     * @param forme Forme pharmaceutique
     * @param stockActuel Stock initial
     */
    public Medicament(String nom, FormeMedicament forme, Integer stockActuel) {
        this.nom = nom;
        this.forme = forme;
        this.stockActuel = stockActuel;
    }
    
    
    // ========== GETTERS / SETTERS ==========
    
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
    
    public String getDosage() {
        return dosage;
    }
    
    public void setDosage(String dosage) {
        this.dosage = dosage;
    }
    
    public FormeMedicament getForme() {
        return forme;
    }
    
    public void setForme(FormeMedicament forme) {
        this.forme = forme;
    }
    
    public BigDecimal getPrix() {
        return prix;
    }
    
    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }
    
    public Integer getStockActuel() {
        return stockActuel;
    }
    
    public void setStockActuel(Integer stockActuel) {
        this.stockActuel = stockActuel;
    }
    
    public Integer getSeuilAlerte() {
        return seuilAlerte;
    }
    
    public void setSeuilAlerte(Integer seuilAlerte) {
        this.seuilAlerte = seuilAlerte;
    }
    
    public List<MouvementStock> getMouvements() {
        return mouvements;
    }
    
    public void setMouvements(List<MouvementStock> mouvements) {
        this.mouvements = mouvements;
    }
    
    
    // ========== MÉTHODES UTILITAIRES ==========
    
    /**
     * Vérifie si le stock est en dessous du seuil d'alerte.
     * 
     * @return true si stock faible, false sinon
     */
    public boolean isStockFaible() {
        if (seuilAlerte == null) {
            return false;  // Pas de seuil configuré = pas d'alerte
        }
        return stockActuel != null && stockActuel < seuilAlerte;
    }
    
    /**
     * toString() pour debug (affichage console).
     */
    @Override
    public String toString() {
        return "Medicament{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", dosage='" + dosage + '\'' +
                ", forme=" + forme +
                ", stockActuel=" + stockActuel +
                ", seuilAlerte=" + seuilAlerte +
                '}';
    }
    
    /**
     * equals() et hashCode() basés sur ID.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Medicament)) return false;
        Medicament that = (Medicament) o;
        return id != null && id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
