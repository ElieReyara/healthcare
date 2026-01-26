package com.healthcenter.domain.enums;

/**
 * Types d'actions auditées dans le système
 * Permet la traçabilité complète des opérations
 */
public enum ActionAudit {
    
    CREATE("Création"),
    UPDATE("Modification"),
    DELETE("Suppression"),
    READ("Consultation"),
    LOGIN("Connexion"),
    LOGOUT("Déconnexion"),
    EXPORT("Exportation"),
    GENERATE_REPORT("Génération rapport"),
    BACKUP("Sauvegarde"),
    RESTORE("Restauration");
    
    private final String libelle;
    
    ActionAudit(String libelle) {
        this.libelle = libelle;
    }
    
    public String getLibelle() {
        return libelle;
    }
    
    @Override
    public String toString() {
        return libelle;
    }
}
