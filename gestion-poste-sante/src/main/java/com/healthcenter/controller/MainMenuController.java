package com.healthcenter.controller;

import com.healthcenter.App;
import com.healthcenter.security.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
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

    @FXML
    private Label currentModuleLabel;
    
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
     * Handler Menu : Médicaments
     */
    @FXML
    private void handleMedicaments() {
        chargerMedicaments();
    }
    
    /**
     * Handler Menu : Vaccinations
     */
    @FXML
    private void handleVaccinations() {
        chargerVaccinations();
    }
    
    /**
     * Handler Menu : Personnel
     */
    @FXML
    private void handlePersonnel() {
        chargerPersonnel();
    }
    
    /**
     * Handler Menu : Statistiques
     */
    @FXML
    private void handleStatistiques() {
        chargerStatistiques();
    }
    
    /**
     * Handler Menu : Rapports
     */
    @FXML
    private void handleRapports() {
        chargerRapports();
    }
    
    @FXML
    private void handleDeconnexion() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Déconnexion");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Voulez-vous vous déconnecter ?");

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        SessionManager.getInstance().deconnecter();
        if (App.getInstance() != null) {
            App.getInstance().afficherLogin();
        } else {
            Platform.exit();
        }
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
        alert.setHeaderText("HealthCare");
        alert.setContentText(
            "Application de gestion HealthCare\n\n" +
            "Version 2.2\n" +
            "© 2026 - Tous droits réservés\n\n" +
            "Modules disponibles :\n" +
            "✅ Patients\n" +
            "✅ Consultations\n" +
            "✅ Médicaments + Stock\n" +
            "✅ Vaccinations + Calendrier Vaccinal\n" +
            "✅ Personnel Médical\n" +
            "✅ Statistiques + Tableaux de bord\n" +
            "✅ Rapports PDF/Excel"
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
     * Charge la vue Médicaments dans le contentPane.
     */
    private void chargerMedicaments() {
        chargerVue("/fxml/medicament-list.fxml", "Médicaments");
    }
    
    /**
     * Charge la vue Vaccinations dans le contentPane.
     */
    private void chargerVaccinations() {
        chargerVue("/fxml/vaccination-list.fxml", "Vaccinations");
    }
    
    /**
     * Charge la vue Personnel dans le contentPane.
     */
    private void chargerPersonnel() {
        chargerVue("/fxml/personnel-list.fxml", "Personnel");
    }
    
    /**
     * Charge la vue Statistiques dans le contentPane.
     */
    private void chargerStatistiques() {
        chargerVue("/fxml/dashboard.fxml", "Statistiques");
    }
    
    /**
     * Charge la vue Rapports dans le contentPane.
     */
    private void chargerRapports() {
        chargerVue("/fxml/rapport.fxml", "Rapports");
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

            if (currentModuleLabel != null) {
                currentModuleLabel.setText("Module : " + nomModule);
            }
            
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
