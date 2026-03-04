package com.healthcenter.domain.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Énumération des rôles utilisateurs avec leurs permissions
 * Chaque rôle définit l'accès aux différents modules de l'application
 */
public enum RoleUtilisateur {
    
    ADMIN("Administrateur", Arrays.asList("*")),
    
    MEDECIN("Médecin", Arrays.asList(
        "PATIENTS", "CONSULTATIONS", "VACCINATIONS", "STATISTIQUES"
    )),
    
    INFIRMIER("Infirmier", Arrays.asList(
        "PATIENTS", "VACCINATIONS", "MEDICAMENTS"
    )),
    
    SAGE_FEMME("Sage-femme", Arrays.asList(
        "PATIENTS", "CONSULTATIONS_LIMITED"
    )),
    
    GESTIONNAIRE("Gestionnaire", Arrays.asList(
        "STATISTIQUES", "RAPPORTS", "PERSONNEL", "MEDICAMENTS"
    )),
    
    RECEPTIONNISTE("Réceptionniste", Arrays.asList(
        "PATIENTS_READ", "CONSULTATIONS_LIMITED", "RAPPORTS", "STATISTIQUES"
    ));
    
    private final String libelle;
    private final List<String> permissions;
    
    RoleUtilisateur(String libelle, List<String> permissions) {
        this.libelle = libelle;
        this.permissions = permissions;
    }
    
    public String getLibelle() {
        return libelle;
    }
    
    public List<String> getPermissions() {
        return permissions;
    }
    
    /**
     * Vérifie si le rôle a accès à un module spécifique
     * @param module Le nom du module
     * @return true si le rôle a accès
     */
    public boolean hasAccess(String module) {
        if (permissions.contains("*")) {
            return true; // ADMIN a accès à tout
        }
        return permissions.contains(module);
    }
    
    @Override
    public String toString() {
        return libelle;
    }
}
