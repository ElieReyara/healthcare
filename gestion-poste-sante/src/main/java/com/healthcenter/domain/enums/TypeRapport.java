package com.healthcenter.domain.enums;

/**
 * Enumération des types de rapports disponibles dans le système.
 * Définit les différents types de rapports pouvant être générés.
 */
public enum TypeRapport {
    
    ACTIVITE_GLOBALE("Rapport d'activité globale"),
    CONSULTATIONS("Rapport consultations"),
    VACCINATIONS("Rapport vaccinations"),
    MEDICAMENTS_STOCK("Rapport médicaments et stock"),
    PERSONNEL_ACTIVITE("Rapport activité personnel"),
    MALADIES_FREQUENTES("Rapport maladies fréquentes"),
    MENSUEL("Rapport mensuel"),
    ANNUEL("Rapport annuel");
    
    private final String libelle;
    
    /**
     * Constructeur.
     *
     * @param libelle Le libellé du type de rapport
     */
    TypeRapport(String libelle) {
        this.libelle = libelle;
    }
    
    /**
     * Retourne le libellé du type de rapport.
     *
     * @return Le libellé
     */
    public String getLibelle() {
        return libelle;
    }
    
    @Override
    public String toString() {
        return libelle;
    }
}
