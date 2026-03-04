package com.healthcenter.security;

import com.healthcenter.domain.entities.Utilisateur;
import com.healthcenter.domain.enums.RoleUtilisateur;

import java.time.LocalDateTime;

/**
 * Gestionnaire de session utilisateur (Singleton)
 * Maintient l'état de l'utilisateur connecté à travers l'application
 */
public class SessionManager {
    
    private static SessionManager instance;
    
    private Utilisateur utilisateurConnecte;
    private LocalDateTime dateConnexion;
    
    // Constructeur privé pour pattern Singleton
    private SessionManager() {
    }
    
    /**
     * Récupère l'instance unique du SessionManager
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    /**
     * Définit l'utilisateur connecté
     */
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateurConnecte = utilisateur;
        this.dateConnexion = LocalDateTime.now();
    }
    
    /**
     * Récupère l'utilisateur actuellement connecté
     */
    public Utilisateur getUtilisateur() {
        return utilisateurConnecte;
    }
    
    /**
     * Vérifie si un utilisateur est connecté
     */
    public boolean isConnecte() {
        return utilisateurConnecte != null;
    }
    
    /**
     * Vérifie si l'utilisateur connecté a un rôle spécifique
     */
    public boolean hasRole(RoleUtilisateur role) {
        return isConnecte() && utilisateurConnecte.getRole() == role;
    }
    
    /**
     * Vérifie si l'utilisateur a accès à un module
     */
    public boolean hasPermission(String module) {
        if (!isConnecte()) {
            return false;
        }

        RoleUtilisateur role = utilisateurConnecte.getRole();
        if (role == null) {
            return false;
        }

        if (role.hasAccess(module)) {
            return true;
        }

        // Permissions dérivées (ex: PATIENTS_READ, PATIENTS_CREATE)
        for (String permission : role.getPermissions()) {
            if (permission != null && permission.startsWith(module + "_")) {
                return true;
            }
        }

        // Cas spécifiques de permissions "limited"
        if ("CONSULTATIONS".equals(module) && role.hasAccess("CONSULTATIONS_LIMITED")) {
            return true;
        }

        return false;
    }
    
    /**
     * Déconnecte l'utilisateur
     */
    public void deconnecter() {
        this.utilisateurConnecte = null;
        this.dateConnexion = null;
    }
    
    /**
     * Récupère la date de connexion
     */
    public LocalDateTime getDateConnexion() {
        return dateConnexion;
    }
}
