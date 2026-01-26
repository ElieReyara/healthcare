package com.healthcenter.controller;

import com.healthcenter.domain.entities.Utilisateur;
import com.healthcenter.domain.enums.ActionAudit;
import com.healthcenter.security.SessionManager;
import com.healthcenter.service.AuditService;
import com.healthcenter.service.UtilisateurService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Contrôleur pour l'écran de connexion
 */
@Component
public class LoginController {
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private Button btnConnecter;
    
    @FXML
    private ProgressIndicator loadingIndicator;
    
    @Autowired
    private UtilisateurService utilisateurService;
    
    @Autowired
    private AuditService auditService;
    
    private Runnable onLoginSuccess;
    
    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
        loadingIndicator.setVisible(false);
        
        // Enter dans le champ password déclenche la connexion
        passwordField.setOnAction(event -> handleConnecter());
    }
    
    /**
     * Gère la tentative de connexion
     */
    @FXML
    private void handleConnecter() {
        errorLabel.setVisible(false);
        
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        
        // Validation des champs
        if (username.isEmpty() || password.isEmpty()) {
            afficherErreur("Veuillez remplir tous les champs");
            return;
        }
        
        // Afficher indicateur de chargement
        loadingIndicator.setVisible(true);
        btnConnecter.setDisable(true);
        
        // Authentification asynchrone pour ne pas bloquer l'UI
        new Thread(() -> {
            Optional<Utilisateur> utilisateurOpt = utilisateurService.authentifier(username, password);
            
            Platform.runLater(() -> {
                loadingIndicator.setVisible(false);
                btnConnecter.setDisable(false);
                
                if (utilisateurOpt.isPresent()) {
                    Utilisateur utilisateur = utilisateurOpt.get();
                    
                    // Enregistrer l'utilisateur dans la session
                    SessionManager.getInstance().setUtilisateur(utilisateur);
                    
                    // Log audit
                    auditService.logAction(utilisateur, ActionAudit.LOGIN, "SYSTEM", null, 
                        "Connexion réussie depuis l'écran de login");
                    
                    // Callback succès
                    if (onLoginSuccess != null) {
                        onLoginSuccess.run();
                    }
                } else {
                    afficherErreur("Identifiants incorrects ou compte inactif");
                    passwordField.clear();
                    passwordField.requestFocus();
                }
            });
        }).start();
    }
    
    /**
     * Gère l'annulation (quitter l'application)
     */
    @FXML
    private void handleAnnuler() {
        Platform.exit();
    }
    
    /**
     * Affiche un message d'erreur
     */
    private void afficherErreur(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
    
    /**
     * Définit le callback à exécuter après connexion réussie
     */
    public void setOnLoginSuccess(Runnable callback) {
        this.onLoginSuccess = callback;
    }
}
