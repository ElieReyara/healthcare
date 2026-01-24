package com.healthcenter.controller;

import com.healthcenter.domain.entities.Patient;
import com.healthcenter.service.PatientService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * CONTROLLER JAVAFX = Gère les événements UI.
 * 
 * @Component : Spring gère ce controller (injection possible)
 * FXML IDs (fx:id) : liés aux composants UI via @FXML
 */
@Component
public class PatientController {
    
    @Autowired
    private PatientService patientService;
    
    @Autowired
    private ConfigurableApplicationContext springContext;
    
    // ========== COMPOSANTS FXML (liés au fichier .fxml) ==========
    
    @FXML private TextField searchField;
    @FXML private TableView<Patient> patientTable;
    @FXML private TableColumn<Patient, Long> colId;
    @FXML private TableColumn<Patient, String> colNom;
    @FXML private TableColumn<Patient, String> colPrenom;
    @FXML private TableColumn<Patient, String> colDateNaissance;
    @FXML private TableColumn<Patient, Integer> colAge;
    @FXML private TableColumn<Patient, String> colSexe;
    @FXML private TableColumn<Patient, String> colTelephone;
    @FXML private TableColumn<Patient, String> colNumeroCarnet;
    
    // Liste observable (se met à jour automatiquement dans l'UI)
    private ObservableList<Patient> patientData = FXCollections.observableArrayList();
    
    
    /**
     * Méthode appelée automatiquement après chargement FXML.
     * Initialise les colonnes et charge les données.
     */
    @FXML
    public void initialize() {
        // CONFIGURATION DES COLONNES (binding données ↔ affichage)
        
        // SimpleXXXProperty = wrapper JavaFX pour auto-update UI
        colId.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleLongProperty(cellData.getValue().getId()).asObject());
        
        colNom.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNom()));
        
        colPrenom.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getPrenom()));
        
        colDateNaissance.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDateNaissance().toString()));
        
        colAge.setCellValueFactory(cellData -> 
            new SimpleIntegerProperty(cellData.getValue().getAge()).asObject());
        
        colSexe.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getSexe().toString()));
        
        colTelephone.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getTelephone()));
        
        colNumeroCarnet.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNumeroCarnet()));
        
        // Lier la liste observable au tableau
        patientTable.setItems(patientData);
        
        // Charger les données initiales
        loadAllPatients();
    }
    
    
    // ========== HANDLERS (méthodes appelées par boutons FXML) ==========
    
    /**
     * onAction="#handleSearch" dans FXML → appelle cette méthode.
     */
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText();
        
        if (searchText == null || searchText.trim().isEmpty()) {
            loadAllPatients();
            return;
        }
        
        // Recherche via service
        List<Patient> results = patientService.rechercherParNom(searchText);
        patientData.clear();
        patientData.addAll(results);
        
        showInfo("Recherche", results.size() + " patient(s) trouvé(s)");
    }
    
    @FXML
    private void handleRefresh() {
        searchField.clear();
        loadAllPatients();
    }
    
    @FXML
        private void handleNewPatient() {
            try {
                // Charger FXML du formulaire
                FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/patient-form.fxml")
                );
                loader.setControllerFactory(springContext::getBean);  // Spring injection
                
                Parent root = loader.load();
                
                // Récupérer le controller et initialiser en mode création
                PatientFormController formController = loader.getController();
                formController.initForCreation();
                
                // Créer fenêtre modale (Dialog)
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Nouveau Patient");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.initOwner(patientTable.getScene().getWindow());
                dialogStage.setScene(new Scene(root));
                dialogStage.setResizable(false);
                
                // Afficher et attendre fermeture
                dialogStage.showAndWait();
                
                // Si sauvegarde réussie, recharger la liste
                if (formController.isSavedSuccessfully()) {
                    loadAllPatients();
                    showInfo("Succès", "Patient créé avec succès");
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                showError("Erreur", "Impossible d'ouvrir le formulaire : " + e.getMessage());
            }
        }

        @FXML
        private void handleEditPatient() {
            Patient selected = patientTable.getSelectionModel().getSelectedItem();
            
            if (selected == null) {
                showWarning("Sélection requise", "Veuillez sélectionner un patient à modifier");
                return;
            }
            
            try {
                // Charger FXML du formulaire
                FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/patient-form.fxml")
                );
                loader.setControllerFactory(springContext::getBean);
                
                Parent root = loader.load();
                
                // Initialiser en mode édition
                PatientFormController formController = loader.getController();
                formController.initForEdit(selected);
                
                // Créer fenêtre modale
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Modifier Patient");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.initOwner(patientTable.getScene().getWindow());
                dialogStage.setScene(new Scene(root));
                dialogStage.setResizable(false);
                
                dialogStage.showAndWait();
                
                // Recharger si modification
                if (formController.isSavedSuccessfully()) {
                    loadAllPatients();
                    showInfo("Succès", "Patient modifié avec succès");
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                showError("Erreur", "Impossible d'ouvrir le formulaire : " + e.getMessage());
            }
        }

    
    @FXML
    private void handleDeletePatient() {
        Patient selected = patientTable.getSelectionModel().getSelectedItem();
        
        if (selected == null) {
            showWarning("Sélection requise", "Veuillez sélectionner un patient à supprimer");
            return;
        }
        
        // Confirmation
        Optional<ButtonType> result = showConfirmation(
            "Confirmer la suppression",
            "Supprimer le patient " + selected.getPrenom() + " " + selected.getNom() + " ?\n" +
            "Cette action est irréversible."
        );
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                patientService.supprimerPatient(selected.getId());
                loadAllPatients();
                showInfo("Succès", "Patient supprimé avec succès");
            } catch (Exception e) {
                showError("Erreur", "Impossible de supprimer : " + e.getMessage());
            }
        }
    }
    
    
    // ========== MÉTHODES UTILITAIRES ==========
    
    private void loadAllPatients() {
        List<Patient> patients = patientService.obtenirTousLesPatients();
        patientData.clear();
        patientData.addAll(patients);
    }
    
    // Dialogs (popups)
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private Optional<ButtonType> showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }
}
