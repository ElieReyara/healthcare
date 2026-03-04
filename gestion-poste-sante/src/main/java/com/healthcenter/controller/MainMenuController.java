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
import javafx.scene.control.MenuItem;
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

    @FXML private MenuItem menuPatients;
    @FXML private MenuItem menuConsultations;
    @FXML private MenuItem menuMedicaments;
    @FXML private MenuItem menuVaccinations;
    @FXML private MenuItem menuPersonnel;
    @FXML private MenuItem menuUtilisateurs;
    @FXML private MenuItem menuStatistiques;
    @FXML private MenuItem menuRapports;
    
    /**
     * Méthode appelée automatiquement après chargement FXML.
     */
    @FXML
    public void initialize() {
        appliquerPermissionsMenu();
        // Charger la vue Patients par défaut au démarrage
        chargerModuleParDefaut();
    }
    
    /**
     * Handler Menu : Patients
     */
    @FXML
    private void handlePatients() {
        if (!peutAccederModule("PATIENTS")) return;
        chargerPatients();
    }
    
    /**
     * Handler Menu : Consultations
     */
    @FXML
    private void handleConsultations() {
        if (!peutAccederModule("CONSULTATIONS")) return;
        chargerConsultations();
    }
    
    /**
     * Handler Menu : Médicaments
     */
    @FXML
    private void handleMedicaments() {
        if (!peutAccederModule("MEDICAMENTS")) return;
        chargerMedicaments();
    }
    
    /**
     * Handler Menu : Vaccinations
     */
    @FXML
    private void handleVaccinations() {
        if (!peutAccederModule("VACCINATIONS")) return;
        chargerVaccinations();
    }
    
    /**
     * Handler Menu : Personnel
     */
    @FXML
    private void handlePersonnel() {
        if (!peutAccederModule("PERSONNEL")) return;
        chargerPersonnel();
    }

    @FXML
    private void handleUtilisateurs() {
        if (!peutAccederModule("UTILISATEURS")) return;
        chargerUtilisateurs();
    }
    
    /**
     * Handler Menu : Statistiques
     */
    @FXML
    private void handleStatistiques() {
        if (!peutAccederModule("STATISTIQUES")) return;
        chargerStatistiques();
    }
    
    /**
     * Handler Menu : Rapports
     */
    @FXML
    private void handleRapports() {
        if (!peutAccederModule("RAPPORTS")) return;
        chargerRapports();
    }

    private void chargerModuleParDefaut() {
        if (SessionManager.getInstance().hasPermission("PATIENTS")) {
            chargerPatients();
            return;
        }
        if (SessionManager.getInstance().hasPermission("CONSULTATIONS")) {
            chargerConsultations();
            return;
        }
        if (SessionManager.getInstance().hasPermission("VACCINATIONS")) {
            chargerVaccinations();
            return;
        }
        if (SessionManager.getInstance().hasPermission("MEDICAMENTS")) {
            chargerMedicaments();
            return;
        }
        if (SessionManager.getInstance().hasPermission("PERSONNEL")) {
            chargerPersonnel();
            return;
        }
        if (SessionManager.getInstance().hasPermission("UTILISATEURS")) {
            chargerUtilisateurs();
            return;
        }
        if (SessionManager.getInstance().hasPermission("STATISTIQUES")) {
            chargerStatistiques();
            return;
        }
        if (SessionManager.getInstance().hasPermission("RAPPORTS")) {
            chargerRapports();
            return;
        }

        currentModuleLabel.setText("Module : Aucun accès");
    }

    private void appliquerPermissionsMenu() {
        setMenuAccess(menuPatients, "PATIENTS");
        setMenuAccess(menuConsultations, "CONSULTATIONS");
        setMenuAccess(menuMedicaments, "MEDICAMENTS");
        setMenuAccess(menuVaccinations, "VACCINATIONS");
        setMenuAccess(menuPersonnel, "PERSONNEL");
        setMenuAccess(menuUtilisateurs, "UTILISATEURS");
        setMenuAccess(menuStatistiques, "STATISTIQUES");
        setMenuAccess(menuRapports, "RAPPORTS");
    }

    private void setMenuAccess(MenuItem menuItem, String modulePermission) {
        if (menuItem == null) {
            return;
        }
        boolean allowed = SessionManager.getInstance().hasPermission(modulePermission);
        menuItem.setVisible(allowed);
        menuItem.setDisable(!allowed);
    }

    private boolean peutAccederModule(String modulePermission) {
        boolean allowed = SessionManager.getInstance().hasPermission(modulePermission);
        if (!allowed) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Accès refusé");
            alert.setHeaderText(null);
            alert.setContentText("Votre profil ne dispose pas des permissions pour ce module.");
            alert.showAndWait();
        }
        return allowed;
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

    private void chargerUtilisateurs() {
        chargerVue("/fxml/utilisateur-list.fxml", "Utilisateurs");
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
