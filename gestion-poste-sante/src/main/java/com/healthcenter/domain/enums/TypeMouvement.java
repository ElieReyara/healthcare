package com.healthcenter.domain.enums;

/**
 * ENUM = Type de mouvement de stock (entrée ou sortie).
 * 
 * Utilisé dans Entity MouvementStock (@Enumerated(STRING))
 * 
 * - ENTREE : Ajout de stock (réapprovisionnement, retour)
 * - SORTIE : Diminution de stock (vente, consultation, péremption)
 */
public enum TypeMouvement {
    /**
     * Entrée de stock (augmentation).
     * Exemples : Réapprovisionnement, Retour fournisseur, Donation
     */
    ENTREE,
    
    /**
     * Sortie de stock (diminution).
     * Exemples : Consultation patient, Vente, Péremption, Casse
     */
    SORTIE;
    
    /**
     * Retourne libellé français pour affichage UI.
     * @return Nom formaté
     */
    public String getLibelle() {
        switch (this) {
            case ENTREE: return "Entrée";
            case SORTIE: return "Sortie";
            default: return this.name();
        }
    }
}
