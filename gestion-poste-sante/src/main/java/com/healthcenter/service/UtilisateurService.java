package com.healthcenter.service;

import com.healthcenter.domain.entities.Utilisateur;
import com.healthcenter.domain.entities.Personnel;
import com.healthcenter.domain.enums.RoleUtilisateur;
import com.healthcenter.repository.PersonnelRepository;
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

    @Autowired
    private PersonnelRepository personnelRepository;
    
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
     * Crée un utilisateur lié à un personnel existant (obligatoire).
     */
    @Transactional
    public Utilisateur creerUtilisateurPourPersonnel(String username, String password, Long personnelId, RoleUtilisateur role) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom d'utilisateur est obligatoire");
        }
        if (password == null || password.trim().length() < 6) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 6 caractères");
        }
        if (personnelId == null) {
            throw new IllegalArgumentException("Le personnel est obligatoire");
        }
        if (role == null) {
            throw new IllegalArgumentException("Le rôle est obligatoire");
        }

        String normalizedUsername = username.trim();
        if (utilisateurRepository.findByUsername(normalizedUsername).isPresent()) {
            throw new IllegalArgumentException("Le nom d'utilisateur existe déjà");
        }

        Personnel personnel = personnelRepository.findById(personnelId)
            .orElseThrow(() -> new IllegalArgumentException("Personnel introuvable"));

        if (Boolean.FALSE.equals(personnel.getActif())) {
            throw new IllegalArgumentException("Impossible de créer un compte pour un personnel inactif");
        }

        if (utilisateurRepository.existsByPersonnelId(personnelId)) {
            throw new IllegalArgumentException("Ce personnel possède déjà un compte utilisateur");
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setUsername(normalizedUsername);
        utilisateur.setPassword(passwordEncoder.encode(password));
        utilisateur.setNom(personnel.getNom());
        utilisateur.setPrenom(personnel.getPrenom());
        utilisateur.setRole(role);
        utilisateur.setActif(true);
        utilisateur.setDateCreation(LocalDateTime.now());
        utilisateur.setPersonnel(personnel);

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
