package com.healthcenter.domain.enums;

/**
 * ENUM TypeVaccin = Types de vaccins selon calendrier vaccinal Sénégal.
 * 
 * Référence : Programme Élargi de Vaccination (PEV) Sénégal
 */
public enum TypeVaccin {
    BCG("BCG - Tuberculose"),
    POLIO_0("Polio 0 (naissance)"),
    POLIO_1("Polio 1"),
    POLIO_2("Polio 2"),
    POLIO_3("Polio 3"),
    PENTA_1("Penta 1 (DTCoq-HepB-Hib)"),
    PENTA_2("Penta 2 (DTCoq-HepB-Hib)"),
    PENTA_3("Penta 3 (DTCoq-HepB-Hib)"),
    PNEUMO_1("Pneumocoque 1"),
    PNEUMO_2("Pneumocoque 2"),
    PNEUMO_3("Pneumocoque 3"),
    ROTA_1("Rotavirus 1"),
    ROTA_2("Rotavirus 2"),
    VAR("VAR (Rougeole-Rubéole)"),
    FIEVRE_JAUNE("Fièvre Jaune"),
    MENINGITE("Méningite A");
    
    private final String libelle;
    
    /**
     * Constructeur.
     * 
     * @param libelle Libellé français du vaccin
     */
    TypeVaccin(String libelle) {
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
