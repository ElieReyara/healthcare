package com.healthcenter.repository;

import com.healthcenter.domain.entities.Utilisateur;
import com.healthcenter.domain.enums.RoleUtilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des utilisateurs
 */
@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    
    /**
     * Recherche un utilisateur par son username
     */
    Optional<Utilisateur> findByUsername(String username);
    
    /**
     * Recherche un utilisateur par son email
     */
    Optional<Utilisateur> findByEmail(String email);
    
    /**
     * Liste tous les utilisateurs actifs
     */
    List<Utilisateur> findByActifTrue();
    
    /**
     * Liste les utilisateurs par rôle
     */
    List<Utilisateur> findByRole(RoleUtilisateur role);

    /**
     * Vérifie si un compte utilisateur existe déjà pour un personnel.
     */
    boolean existsByPersonnelId(Long personnelId);
}
