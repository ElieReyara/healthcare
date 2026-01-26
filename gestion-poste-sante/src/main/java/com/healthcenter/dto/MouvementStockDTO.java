package com.healthcenter.dto;

/**
 * DTO = Objet simple pour transfert UI ↔ Service (mouvement de stock).
 * 
 * DIFFÉRENCE avec Entity MouvementStock :
 * - Pas d'annotations JPA
 * - Type en String (pas enum TypeMouvement)
 * - Pas de medicament Entity (uniquement medicamentId)
 * - Pas de stockAvant/stockApres (calculé automatiquement par Service)
 * - Pas de dateMouvement (généré automatiquement)
 * 
 * Utilisé pour :
 * - Formulaires JavaFX (ajustement stock)
 * - Transfert données Controller → Service
 */
public class MouvementStockDTO {
    
    /**
     * ID du médicament concerné.
     * Obligatoire.
     */
    private Long medicamentId;
    
    /**
     * Type de mouvement (en String pour UI).
     * Valeurs possibles : "ENTREE", "SORTIE"
     * Obligatoire.
     */
    private String type;
    
    /**
     * Quantité déplacée (toujours positif).
     * Obligatoire (minimum 1).
     */
    private Integer quantite;
    
    /**
     * Motif du mouvement (raison).
     * Optionnel (peut être vide).
     * Exemples : "Réapprovisionnement", "Consultation patient X"
     */
    private String motif;
    
    
    // ========== CONSTRUCTEURS ==========
    
    /**
     * Constructeur vide (requis pour frameworks).
     */
    public MouvementStockDTO() {
    }
    
    /**
     * Constructeur avec tous les paramètres.
     * 
     * @param medicamentId ID du médicament
     * @param type Type mouvement ("ENTREE" ou "SORTIE")
     * @param quantite Quantité
     * @param motif Motif du mouvement
     */
    public MouvementStockDTO(Long medicamentId, String type, Integer quantite, String motif) {
        this.medicamentId = medicamentId;
        this.type = type;
        this.quantite = quantite;
        this.motif = motif;
    }
    
    
    // ========== GETTERS / SETTERS ==========
    
    public Long getMedicamentId() {
        return medicamentId;
    }
    
    public void setMedicamentId(Long medicamentId) {
        this.medicamentId = medicamentId;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public Integer getQuantite() {
        return quantite;
    }
    
    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }
    
    public String getMotif() {
        return motif;
    }
    
    public void setMotif(String motif) {
        this.motif = motif;
    }
    
    
    // ========== MÉTHODES UTILITAIRES ==========
    
    /**
     * toString() pour debug (affichage console).
     */
    @Override
    public String toString() {
        return "MouvementStockDTO{" +
                "medicamentId=" + medicamentId +
                ", type='" + type + '\'' +
                ", quantite=" + quantite +
                ", motif='" + motif + '\'' +
                '}';
    }
    
    /**
     * Validation basique (appelée avant envoi au Service).
     * @return true si données minimales présentes
     */
    public boolean isValid() {
        return medicamentId != null 
            && type != null && !type.trim().isEmpty()
            && quantite != null && quantite > 0;
    }
}
