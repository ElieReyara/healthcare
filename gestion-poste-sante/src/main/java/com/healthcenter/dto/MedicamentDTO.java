package com.healthcenter.dto;

import java.math.BigDecimal;

/**
 * DTO = Objet simple pour transfert UI ↔ Service.
 * 
 * DIFFÉRENCE avec Entity Medicament :
 * - Pas d'annotations JPA (@Entity, @Column, @OneToMany)
 * - Forme en String (pas enum FormeMedicament)
 * - Pas d'ID (géré par DB après création)
 * - Pas de liste mouvements (gestion séparée)
 * 
 * Utilisé pour :
 * - Formulaires JavaFX (création/modification médicament)
 * - Transfert données Controller → Service
 * - Évite exposition Entity complète dans UI
 */
public class MedicamentDTO {
    
    /**
     * Nom commercial du médicament.
     * Obligatoire.
     */
    private String nom;
    
    /**
     * Dosage (concentration).
     * Optionnel. Exemples : "500mg", "10ml"
     */
    private String dosage;
    
    /**
     * Forme pharmaceutique (en String pour UI).
     * Valeurs possibles : "COMPRIME", "SIROP", "INJECTION", "POMMADE", "GELULE"
     * Obligatoire.
     */
    private String forme;
    
    /**
     * Prix unitaire en FCFA.
     * Optionnel (peut être null).
     */
    private BigDecimal prix;
    
    /**
     * Stock actuel disponible.
     * Obligatoire (minimum 0).
     */
    private Integer stockActuel;
    
    /**
     * Seuil d'alerte pour rupture de stock.
     * Optionnel (null = pas d'alerte).
     */
    private Integer seuilAlerte;
    
    
    // ========== CONSTRUCTEURS ==========
    
    /**
     * Constructeur vide (requis pour frameworks).
     */
    public MedicamentDTO() {
    }
    
    /**
     * Constructeur complet avec tous les champs.
     * Utile pour mapping Entity → DTO.
     * 
     * @param nom Nom médicament
     * @param dosage Dosage
     * @param forme Forme (String)
     * @param prix Prix unitaire
     * @param stockActuel Stock actuel
     * @param seuilAlerte Seuil alerte
     */
    public MedicamentDTO(String nom, String dosage, String forme, 
                        BigDecimal prix, Integer stockActuel, Integer seuilAlerte) {
        this.nom = nom;
        this.dosage = dosage;
        this.forme = forme;
        this.prix = prix;
        this.stockActuel = stockActuel;
        this.seuilAlerte = seuilAlerte;
    }
    
    
    // ========== GETTERS / SETTERS ==========
    
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
    
    public String getForme() {
        return forme;
    }
    
    public void setForme(String forme) {
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
    
    
    // ========== MÉTHODES UTILITAIRES ==========
    
    /**
     * toString() pour debug (affichage console).
     */
    @Override
    public String toString() {
        return "MedicamentDTO{" +
                "nom='" + nom + '\'' +
                ", dosage='" + dosage + '\'' +
                ", forme='" + forme + '\'' +
                ", stockActuel=" + stockActuel +
                ", seuilAlerte=" + seuilAlerte +
                '}';
    }
    
    /**
     * Validation basique (appelée avant envoi au Service).
     * @return true si données minimales présentes
     */
    public boolean isValid() {
        return nom != null && !nom.trim().isEmpty() 
            && forme != null && !forme.trim().isEmpty()
            && stockActuel != null && stockActuel >= 0;
    }
}
