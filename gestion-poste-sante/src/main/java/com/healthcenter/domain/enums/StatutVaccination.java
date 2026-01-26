package com.healthcenter.domain.enums;

/**
 * ENUM StatutVaccination = Statut d'une vaccination (administrée, rappel prévu, rappel en retard).
 */
public enum StatutVaccination {
    ADMINISTRE("Administré"),
    RAPPEL_PREVU("Rappel prévu"),
    RAPPEL_EN_RETARD("Rappel en retard");
    
    private final String libelle;
    
    /**
     * Constructeur.
     * 
     * @param libelle Libellé français du statut
     */
    StatutVaccination(String libelle) {
        this.libelle = libelle;
    }
    
    /**
     * Retourne le libellé pour affichage UI.
     * 
     * @return Libellé français
     */
    public String getLibelle() {
        return libelle;
    }
}
