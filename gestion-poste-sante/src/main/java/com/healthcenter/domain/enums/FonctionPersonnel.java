package com.healthcenter.domain.enums;

/**
 * ENUM - Fonction Personnel Médical
 * 
 * Représente les différentes fonctions du personnel médical au Sénégal :
 * - Personnel médical : MEDECIN, INFIRMIER, SAGE_FEMME, AIDE_SOIGNANT
 * - Personnel technique : PHARMACIEN, TECHNICIEN_LABO
 * - Personnel administratif : GESTIONNAIRE, RECEPTIONNISTE
 * 
 * @author Health Center Team
 */
public enum FonctionPersonnel {
    
    MEDECIN("Médecin", true),
    INFIRMIER("Infirmier(ère)", true),
    SAGE_FEMME("Sage-femme", true),
    AIDE_SOIGNANT("Aide-soignant(e)", true),
    PHARMACIEN("Pharmacien(ne)", false),
    TECHNICIEN_LABO("Technicien(ne) laboratoire", false),
    GESTIONNAIRE("Gestionnaire", false),
    RECEPTIONNISTE("Réceptionniste", false);
    
    private final String libelle;
    private final boolean estMedical;
    
    /**
     * Constructeur FonctionPersonnel.
     * 
     * @param libelle Libellé affiché dans l'interface utilisateur
     * @param estMedical true si fonction médicale (soins patients), false si support/admin
     */
    FonctionPersonnel(String libelle, boolean estMedical) {
        this.libelle = libelle;
        this.estMedical = estMedical;
    }
    
    /**
     * @return Libellé lisible de la fonction (ex: "Médecin")
     */
    public String getLibelle() {
        return libelle;
    }
    
    /**
     * @return true si personnel médical (contact direct patients), false sinon
     */
    public boolean isEstMedical() {
        return estMedical;
    }
    
    /**
     * @return Libellé pour affichage dans ComboBox JavaFX
     */
    @Override
    public String toString() {
        return libelle;
    }
}
