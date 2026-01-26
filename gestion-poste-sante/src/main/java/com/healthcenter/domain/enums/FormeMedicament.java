package com.healthcenter.domain.enums;

/**
 * ENUM = Liste de valeurs fixes pour la forme du médicament.
 * 
 * Utilisé dans Entity Medicament (@Enumerated(STRING))
 * Stocké en DB comme VARCHAR (nom de l'enum, ex: "COMPRIME")
 */
public enum FormeMedicament {
    /**
     * Comprimé (forme solide à avaler)
     */
    COMPRIME,
    
    /**
     * Sirop (forme liquide à boire)
     */
    SIROP,
    
    /**
     * Injection (forme injectable)
     */
    INJECTION,
    
    /**
     * Pommade (application cutanée)
     */
    POMMADE,
    
    /**
     * Gélule (capsule à avaler)
     */
    GELULE;
    
    /**
     * Retourne libellé français pour affichage UI.
     * @return Nom formaté
     */
    public String getLibelle() {
        switch (this) {
            case COMPRIME: return "Comprimé";
            case SIROP: return "Sirop";
            case INJECTION: return "Injection";
            case POMMADE: return "Pommade";
            case GELULE: return "Gélule";
            default: return this.name();
        }
    }
}
