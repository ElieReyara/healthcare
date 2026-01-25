package com.healthcenter.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Controller du menu principal avec navigation entre modules.
 */
@Component
public class MainMenuController {
    
    @Autowired
    private ConfigurableApplicationContext springContext;
    
    @FXML
    private BorderPane contentPane;
    
    /**
     * Méthode appelée automatiquement après chargement FXML.
     */
    @FXML
    public void initialize() {
        // Charger la vue Patients par défaut au démarrage
        chargerPatients();
    }
    
    /**
     * Handler Menu : Patients
     */
    @FXML
    private void handlePatients() {
        chargerPatients();
    }
    
    /**
     * Handler Menu : Consultations
     */
    @FXML
    private void handleConsultations() {
        chargerConsultations();
    }
    
    /**
     * Handler Menu : Quitter
     */
    @FXML
    private void handleQuitter() {
        Platform.exit();
    }
    
    /**
     * Handler Menu : À propos
     */
    @FXML
    private void handleAPropos() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.INFORMATION
        );
        alert.setTitle("À propos");
        alert.setHeaderText("Gestion Poste de Santé");
        alert.setContentText(
            "Application de gestion pour poste de santé\n\n" +
            "Version 1.0\n" +
            "© 2026 - Tous droits réservés\n\n" +
            "Modules disponibles :\n" +
            "✅ Patients\n" +
            "✅ Consultations"
        );
        alert.showAndWait();
    }
    
    // ========== CHARGEMENT VUES ==========
    
    /**
     * Charge la vue Patients dans le contentPane.
     */
    private void chargerPatients() {
        chargerVue("/fxml/patient-list.fxml", "Patients");
    }
    
    /**
     * Charge la vue Consultations dans le contentPane.
     */
    private void chargerConsultations() {
        chargerVue("/fxml/consultation-list.fxml", "Consultations");
    }
    
    /**
     * Méthode générique pour charger une vue FXML.
     * 
     * @param fxmlPath Chemin vers le fichier FXML
     * @param nomModule Nom du module (pour logs)
     */
    private void chargerVue(String fxmlPath, String nomModule) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(springContext::getBean);
            
            Parent vue = loader.load();
            
            // Remplacer le contenu du BorderPane central
            contentPane.setCenter(vue);
            
            System.out.println("✅ Module " + nomModule + " chargé");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur chargement " + nomModule + " : " + e.getMessage());
            e.printStackTrace();
            
            // Afficher erreur à l'utilisateur
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR
            );
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible de charger " + nomModule);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}
