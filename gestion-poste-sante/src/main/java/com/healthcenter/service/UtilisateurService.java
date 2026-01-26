package com.healthcenter.service;

import com.healthcenter.domain.entities.Utilisateur;
import com.healthcenter.domain.enums.RoleUtilisateur;
import com.healthcenter.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service de gestion des utilisateurs et de l'authentification
 */
@Service
public class UtilisateurService {
    
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Crée un nouvel utilisateur avec mot de passe hashé
     */
    @Transactional
    public Utilisateur creerUtilisateur(String username, String password, String nom, String prenom, RoleUtilisateur role) {
        // Vérifier si username existe déjà
        if (utilisateurRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Le nom d'utilisateur existe déjà");
        }
        
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setUsername(username);
        utilisateur.setPassword(passwordEncoder.encode(password)); // Hash BCrypt
        utilisateur.setNom(nom);
        utilisateur.setPrenom(prenom);
        utilisateur.setRole(role);
        utilisateur.setActif(true);
        utilisateur.setDateCreation(LocalDateTime.now());
        
        return utilisateurRepository.save(utilisateur);
    }
    
    /**
     * Authentifie un utilisateur
     * @return Optional contenant l'utilisateur si authentification réussie
     */
    @Transactional
    public Optional<Utilisateur> authentifier(String username, String password) {
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findByUsername(username);
        
        if (utilisateurOpt.isEmpty()) {
            return Optional.empty();
        }
        
        Utilisateur utilisateur = utilisateurOpt.get();
        
        // Vérifier si utilisateur actif
        if (!utilisateur.getActif()) {
            return Optional.empty();
        }
        
        // Vérifier mot de passe
        if (!passwordEncoder.matches(password, utilisateur.getPassword())) {
            return Optional.empty();
        }
        
        // Mettre à jour dernière connexion
        utilisateur.setDerniereConnexion(LocalDateTime.now());
        utilisateurRepository.save(utilisateur);
        
        return Optional.of(utilisateur);
    }
    
    /**
     * Change le mot de passe d'un utilisateur
     */
    @Transactional
    public void changerMotDePasse(Long utilisateurId, String ancienPassword, String nouveauPassword) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
        
        // Vérifier ancien mot de passe
        if (!passwordEncoder.matches(ancienPassword, utilisateur.getPassword())) {
            throw new IllegalArgumentException("Ancien mot de passe incorrect");
        }
        
        // Hash et sauvegarder nouveau mot de passe
        utilisateur.setPassword(passwordEncoder.encode(nouveauPassword));
        utilisateurRepository.save(utilisateur);
    }
    
    /**
     * Récupère un utilisateur par son username
     */
    public Optional<Utilisateur> obtenirUtilisateurParUsername(String username) {
        return utilisateurRepository.findByUsername(username);
    }
    
    /**
     * Liste tous les utilisateurs
     */
    public List<Utilisateur> obtenirTousUtilisateurs() {
        return utilisateurRepository.findAll();
    }
    
    /**
     * Liste les utilisateurs actifs
     */
    public List<Utilisateur> obtenirUtilisateursActifs() {
        return utilisateurRepository.findByActifTrue();
    }
    
    /**
     * Désactive un utilisateur (soft delete)
     */
    @Transactional
    public void desactiverUtilisateur(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
        utilisateur.setActif(false);
        utilisateurRepository.save(utilisateur);
    }
    
    /**
     * Active un utilisateur
     */
    @Transactional
    public void activerUtilisateur(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
        utilisateur.setActif(true);
        utilisateurRepository.save(utilisateur);
    }
}
