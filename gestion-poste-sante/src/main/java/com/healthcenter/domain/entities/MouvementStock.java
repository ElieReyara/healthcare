package com.healthcenter.domain.entities;

import com.healthcenter.domain.enums.TypeMouvement;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * ENTITY = Classe Java mappée à la table "mouvements_stock".
 * 
 * Représente un mouvement de stock (entrée ou sortie) pour traçabilité.
 * Relation : N MouvementStock → 1 Medicament (Many-to-One).
 * 
 * Chaque mouvement enregistre :
 * - Type (ENTREE/SORTIE)
 * - Quantité
 * - Stock avant/après (historique)
 * - Motif (raison du mouvement)
 */
@Entity
@Table(name = "mouvements_stock")
public class MouvementStock {
    
    // ========== ATTRIBUTS (colonnes SQL) ==========
    
    /**
     * Clé primaire auto-incrémentée.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Relation : Plusieurs mouvements → 1 Médicament.
     * 
     * @ManyToOne = N MouvementStock → 1 Medicament
     * @JoinColumn = colonne SQL "medicament_id" (foreign key)
     * fetch = LAZY = Medicament chargé uniquement si accédé
     * nullable = false = Mouvement DOIT avoir un médicament
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicament_id", nullable = false)
    private Medicament medicament;
    
    /**
     * Type de mouvement (ENTREE ou SORTIE).
     * @Enumerated(STRING) = stocke "ENTREE" au lieu de 0.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TypeMouvement type;
    
    /**
     * Quantité déplacée (toujours positif).
     * - ENTREE : quantité ajoutée au stock
     * - SORTIE : quantité retirée du stock
     */
    @Column(nullable = false)
    private Integer quantite;
    
    /**
     * Date et heure du mouvement.
     * LocalDateTime = type moderne pour timestamps.
     */
    @Column(name = "date_mouvement", nullable = false)
    private LocalDateTime dateMouvement;
    
    /**
     * Motif du mouvement (raison).
     * Exemples :
     * - ENTREE : "Réapprovisionnement", "Retour fournisseur"
     * - SORTIE : "Consultation patient X", "Péremption", "Casse"
     */
    @Column(length = 500)
    private String motif;
    
    /**
     * Stock AVANT le mouvement (historique).
     * Permet de reconstituer l'état du stock à un instant T.
     */
    @Column(name = "stock_avant")
    private Integer stockAvant;
    
    /**
     * Stock APRÈS le mouvement (historique).
     * stockApres = stockAvant + quantite (si ENTREE)
     * stockApres = stockAvant - quantite (si SORTIE)
     */
    @Column(name = "stock_apres")
    private Integer stockApres;
    
    
    // ========== CONSTRUCTEURS ==========
    
    /**
     * Constructeur vide OBLIGATOIRE pour JPA.
     */
    public MouvementStock() {
    }
    
    /**
     * Constructeur avec paramètres essentiels.
     * 
     * @param medicament Médicament concerné
     * @param type Type mouvement (ENTREE/SORTIE)
     * @param quantite Quantité déplacée
     * @param motif Raison du mouvement
     */
    public MouvementStock(Medicament medicament, TypeMouvement type, Integer quantite, String motif) {
        this.medicament = medicament;
        this.type = type;
        this.quantite = quantite;
        this.motif = motif;
        this.dateMouvement = LocalDateTime.now();
    }
    
    
    // ========== GETTERS / SETTERS ==========
    
    public Long getId() {
        return id;
    }
    
    // Pas de setId() → ID géré uniquement par la DB
    
    public Medicament getMedicament() {
        return medicament;
    }
    
    public void setMedicament(Medicament medicament) {
        this.medicament = medicament;
    }
    
    public TypeMouvement getType() {
        return type;
    }
    
    public void setType(TypeMouvement type) {
        this.type = type;
    }
    
    public Integer getQuantite() {
        return quantite;
    }
    
    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }
    
    public LocalDateTime getDateMouvement() {
        return dateMouvement;
    }
    
    public void setDateMouvement(LocalDateTime dateMouvement) {
        this.dateMouvement = dateMouvement;
    }
    
    public String getMotif() {
        return motif;
    }
    
    public void setMotif(String motif) {
        this.motif = motif;
    }
    
    public Integer getStockAvant() {
        return stockAvant;
    }
    
    public void setStockAvant(Integer stockAvant) {
        this.stockAvant = stockAvant;
    }
    
    public Integer getStockApres() {
        return stockApres;
    }
    
    public void setStockApres(Integer stockApres) {
        this.stockApres = stockApres;
    }
    
    
    // ========== MÉTHODES UTILITAIRES ==========
    
    /**
     * toString() pour debug (affichage console).
     * N'affiche PAS medicament.toString() pour éviter boucle infinie (lazy loading).
     */
    @Override
    public String toString() {
        return "MouvementStock{" +
                "id=" + id +
                ", medicament_id=" + (medicament != null ? medicament.getId() : null) +
                ", type=" + type +
                ", quantite=" + quantite +
                ", dateMouvement=" + dateMouvement +
                ", stockAvant=" + stockAvant +
                ", stockApres=" + stockApres +
                '}';
    }
    
    /**
     * equals() et hashCode() basés sur ID.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MouvementStock)) return false;
        MouvementStock that = (MouvementStock) o;
        return id != null && id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
