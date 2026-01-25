package com.healthcenter.controller;

import com.healthcenter.domain.entities.Consultation;
import com.healthcenter.domain.entities.Patient;
import com.healthcenter.service.ConsultationService;
import com.healthcenter.service.PatientService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * CONTROLLER JAVAFX = Gère les événements UI pour Consultations.
 * 
 * @Component : Spring gère ce controller (injection possible)
 * FXML IDs (fx:id) : liés aux composants UI via @FXML
 */
@Component
public class ConsultationController {
    
    @Autowired
    private ConsultationService consultationService;
    
    @Autowired
    private PatientService patientService;
    
    @Autowired
    private ConfigurableApplicationContext springContext;
    
    // ========== COMPOSANTS FXML (liés au fichier .fxml) ==========
    
    @FXML private TextField searchField;
    @FXML private ComboBox<Patient> patientFilter;
    @FXML private TableView<Consultation> consultationTable;
    @FXML private TableColumn<Consultation, Long> colId;
    @FXML private TableColumn<Consultation, String> colPatient;
    @FXML private TableColumn<Consultation, LocalDateTime> colDate;
    @FXML private TableColumn<Consultation, String> colSymptomes;
    @FXML private TableColumn<Consultation, String> colDiagnostic;
    
    // Liste observable (se met à jour automatiquement dans l'UI)
    private ObservableList<Consultation> consultationData = FXCollections.observableArrayList();
    private ObservableList<Patient> patientsData = FXCollections.observableArrayList();
    
    // Formatter pour dates (affichage français)
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    
    /**
     * Méthode appelée automatiquement après chargement FXML.
     * Initialise les colonnes et charge les données.
     */
    @FXML
    public void initialize() {
        // CONFIGURATION DES COLONNES (binding données ↔ affichage)
        
        // ID
        colId.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleLongProperty(cellData.getValue().getId()).asObject());
        
        // Patient (nom complet : "Nom Prénom")
        colPatient.setCellValueFactory(cellData -> {
            Patient patient = cellData.getValue().getPatient();
            String nomComplet = patient != null 
                ? patient.getNom() + " " + patient.getPrenom()
                : "Inconnu";
            return new SimpleStringProperty(nomComplet);
        });
        
        // Date consultation (formatée)
        colDate.setCellValueFactory(cellData -> 
            new SimpleObjectProperty<>(cellData.getValue().getDateConsultation()));
        
        // Formatter personnalisé pour colonne date
        colDate.setCellFactory(column -> new TableCell<Consultation, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(DATE_FORMATTER.format(item));
                }
            }
        });
        
        // Symptômes (tronqués si > 50 caractères)
        colSymptomes.setCellValueFactory(cellData -> {
            String symptomes = cellData.getValue().getSymptomes();
            if (symptomes == null || symptomes.isEmpty()) {
                return new SimpleStringProperty("--");
            }
            String texte = symptomes.length() > 50 
                ? symptomes.substring(0, 47) + "..."
                : symptomes;
            return new SimpleStringProperty(texte);
        });
        
        // Diagnostic (tronqué si > 50 caractères)
        colDiagnostic.setCellValueFactory(cellData -> {
            String diagnostic = cellData.getValue().getDiagnostic();
            if (diagnostic == null || diagnostic.isEmpty()) {
                return new SimpleStringProperty("--");
            }
            String texte = diagnostic.length() > 50 
                ? diagnostic.substring(0, 47) + "..."
                : diagnostic;
            return new SimpleStringProperty(texte);
        });
        
        // Lier la liste observable au tableau
        consultationTable.setItems(consultationData);
        
        // SETUP COMBOBOX FILTRE PATIENTS
        setupPatientFilter();
        
        // Charger les données initiales
        chargerDonnees();
    }
    
    
    // ========== SETUP COMBOBOX ==========
    
    /**
     * Configure le ComboBox de filtrage par patient.
     */
    private void setupPatientFilter() {
        // Charger tous les patients
        List<Patient> patients = patientService.obtenirTousLesPatients();
        patientsData.clear();
        patientsData.addAll(patients);
        
        // Ajouter option "Tous les patients" en premier
        Patient tousPatients = new Patient();
        tousPatients.setNom("Tous");
        tousPatients.setPrenom("les patients");
        patientsData.add(0, tousPatients);
        
        patientFilter.setItems(patientsData);
        
        // Sélectionner "Tous" par défaut
        patientFilter.getSelectionModel().selectFirst();
        
        // Formatter affichage ComboBox : "Nom Prénom"
        patientFilter.setConverter(new StringConverter<Patient>() {
            @Override
            public String toString(Patient patient) {
                if (patient == null) return "";
                return patient.getNom() + " " + patient.getPrenom();
            }
            
            @Override
            public Patient fromString(String string) {
                return null;  // Non utilisé
            }
        });
    }
    
    
    // ========== HANDLERS (méthodes appelées par boutons FXML) ==========
    
    /**
     * onAction="#handleNouveau" dans FXML → appelle cette méthode.
     */
    @FXML
    private void handleNouveau() {
        try {
            // Charger FXML du formulaire
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/consultation-form.fxml")
            );
            loader.setControllerFactory(springContext::getBean);  // Spring injection
            
            Parent root = loader.load();
            
            // Récupérer le controller et initialiser en mode création
            ConsultationFormController formController = loader.getController();
            formController.initForCreation();
            
            // Créer fenêtre modale (Dialog)
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Nouvelle Consultation");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(consultationTable.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);
            
            // Afficher et attendre fermeture
            dialogStage.showAndWait();
            
            // Si sauvegarde réussie, recharger la liste
            if (formController.isSavedSuccessfully()) {
                chargerDonnees();
                showInfo("Succès", "Consultation créée avec succès");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur", "Impossible d'ouvrir le formulaire : " + e.getMessage());
        }
    }
    
    @FXML
    private void handleModifier() {
        Consultation selected = consultationTable.getSelectionModel().getSelectedItem();
        
        if (selected == null) {
            showWarning("Sélection requise", "Veuillez sélectionner une consultation à modifier");
            return;
        }
        
        try {
            // Charger FXML du formulaire
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/consultation-form.fxml")
            );
            loader.setControllerFactory(springContext::getBean);
            
            Parent root = loader.load();
            
            // Initialiser en mode édition
            ConsultationFormController formController = loader.getController();
            formController.initForEdit(selected);
            
            // Créer fenêtre modale
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Modifier Consultation");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(consultationTable.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);
            
            dialogStage.showAndWait();
            
            // Recharger si modification
            if (formController.isSavedSuccessfully()) {
                chargerDonnees();
                showInfo("Succès", "Consultation modifiée avec succès");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur", "Impossible d'ouvrir le formulaire : " + e.getMessage());
        }
    }
    
    @FXML
    private void handleSupprimer() {
        Consultation selected = consultationTable.getSelectionModel().getSelectedItem();
        
        if (selected == null) {
            showWarning("Sélection requise", "Veuillez sélectionner une consultation à supprimer");
            return;
        }
        
        // Afficher infos consultation dans confirmation
        String patientNom = selected.getPatient().getNom() + " " + selected.getPatient().getPrenom();
        String dateConsult = DATE_FORMATTER.format(selected.getDateConsultation());
        
        // Confirmation
        Optional<ButtonType> result = showConfirmation(
            "Confirmer la suppression",
            "Supprimer la consultation du " + dateConsult + " pour " + patientNom + " ?\n\n" +
            "Cette action est irréversible."
        );
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                consultationService.supprimerConsultation(selected.getId());
                chargerDonnees();
                showInfo("Succès", "Consultation supprimée avec succès");
            } catch (Exception e) {
                showError("Erreur", "Impossible de supprimer : " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleFilterByPatient() {
        Patient selectedPatient = patientFilter.getSelectionModel().getSelectedItem();
        
        if (selectedPatient == null) {
            chargerDonnees();  // Afficher tout si aucune sélection
            return;
        }
        
        // Si "Tous les patients" (ID null = objet créé manuellement)
        if (selectedPatient.getId() == null) {
            chargerDonnees();
            return;
        }
        
        // Filtrer par patient sélectionné
        List<Consultation> consultations = 
            consultationService.obtenirConsultationsParPatient(selectedPatient.getId());
        
        consultationData.clear();
        consultationData.addAll(consultations);
        
        showInfo("Filtre appliqué", 
            consultations.size() + " consultation(s) trouvée(s) pour " + 
            selectedPatient.getNom() + " " + selectedPatient.getPrenom());
    }
    
    @FXML
    private void handleRefresh() {
        // Réinitialiser filtre patient
        patientFilter.getSelectionModel().selectFirst();
        searchField.clear();
        
        chargerDonnees();
        showInfo("Actualisation", "Données rechargées");
    }
    
    
    // ========== MÉTHODES UTILITAIRES ==========
    
    /**
     * Charge toutes les consultations depuis le service.
     */
    private void chargerDonnees() {
        List<Consultation> consultations = consultationService.obtenirToutesLesConsultations();
        consultationData.clear();
        consultationData.addAll(consultations);
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
