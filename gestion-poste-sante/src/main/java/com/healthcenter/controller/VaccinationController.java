package com.healthcenter.controller;

import com.healthcenter.domain.entities.Patient;
import com.healthcenter.domain.entities.Vaccination;
import com.healthcenter.domain.enums.StatutVaccination;
import com.healthcenter.domain.enums.TypeVaccin;
import com.healthcenter.service.PatientService;
import com.healthcenter.service.VaccinationService;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * CONTROLLER JavaFX = Gère la liste des vaccinations.
 * 
 * Fonctionnalités :
 * - Affichage tableau vaccinations avec filtres
 * - Alertes visuelles rappels (proches/retard)
 * - Navigation vers formulaire et carnet vaccinal
 */
@Component
public class VaccinationController {
    
    @Autowired
    private VaccinationService vaccinationService;
    
    @Autowired
    private PatientService patientService;
    
    @Autowired
    private ConfigurableApplicationContext springContext;
    
    // ========== COMPOSANTS FXML ==========
    
    @FXML private TableView<Vaccination> vaccinationTable;
    @FXML private TableColumn<Vaccination, Long> colId;
    @FXML private TableColumn<Vaccination, String> colPatient;
    @FXML private TableColumn<Vaccination, String> colVaccin;
    @FXML private TableColumn<Vaccination, LocalDate> colDateAdmin;
    @FXML private TableColumn<Vaccination, LocalDate> colDateRappel;
    @FXML private TableColumn<Vaccination, String> colStatut;
    @FXML private TableColumn<Vaccination, String> colNumeroLot;
    
    @FXML private ComboBox<Patient> patientFilter;
    @FXML private ComboBox<TypeVaccin> vaccinFilter;
    @FXML private CheckBox rappelsSeulementCheckbox;
    
    private ObservableList<Vaccination> vaccinationData = FXCollections.observableArrayList();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    
    /**
     * Initialisation après chargement FXML.
     */
    @FXML
    public void initialize() {
        try {
            configurerColonnes();
            setupFiltres();
            chargerDonnees();
        } catch (Exception e) {
            System.err.println("❌ Erreur initialisation VaccinationController : " + e.getMessage());
            e.printStackTrace();
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur d'initialisation du module Vaccinations");
            alert.setContentText("Détails : " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    
    // ========== CONFIGURATION ==========
    
    private void configurerColonnes() {
        colId.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleLongProperty(cellData.getValue().getId()).asObject());
        
        colPatient.setCellValueFactory(cellData -> {
            Patient patient = cellData.getValue().getPatient();
            String nomComplet = patient.getPrenom() + " " + patient.getNom();
            return new SimpleStringProperty(nomComplet);
        });
        
        colVaccin.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getVaccin().getLibelle()));
        
        colDateAdmin.setCellValueFactory(cellData -> 
            new SimpleObjectProperty<>(cellData.getValue().getDateAdministration()));
        
        colDateRappel.setCellValueFactory(cellData -> 
            new SimpleObjectProperty<>(cellData.getValue().getDateRappel()));
        
        colNumeroLot.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNumeroLot()));
        
        // Colonne Statut avec couleurs
        colStatut.setCellValueFactory(cellData -> {
            StatutVaccination statut = cellData.getValue().getStatut();
            return new SimpleStringProperty(statut != null ? statut.getLibelle() : "");
        });
        
        colStatut.setCellFactory(column -> new TableCell<Vaccination, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    Vaccination vaccination = getTableView().getItems().get(getIndex());
                    if (vaccination.getStatut() == StatutVaccination.ADMINISTRE) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                        setText("✅ " + item);
                    } else if (vaccination.getStatut() == StatutVaccination.RAPPEL_PREVU) {
                        setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                        setText("🔔 " + item);
                    } else if (vaccination.getStatut() == StatutVaccination.RAPPEL_EN_RETARD) {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                        setText("⚠️ " + item);
                    }
                }
            }
        });
        
        vaccinationTable.setItems(vaccinationData);
    }
    
    private void setupFiltres() {
        // ComboBox Patients
        List<Patient> patients = patientService.obtenirTousLesPatients();
        patientFilter.setItems(FXCollections.observableArrayList(patients));
        patientFilter.setPromptText("Tous les patients");
        patientFilter.setConverter(new StringConverter<Patient>() {
            @Override
            public String toString(Patient patient) {
                return patient != null ? patient.getPrenom() + " " + patient.getNom() : "";
            }
            @Override
            public Patient fromString(String string) {
                return null;
            }
        });
        
        // ComboBox Vaccins
        vaccinFilter.setItems(FXCollections.observableArrayList(TypeVaccin.values()));
        vaccinFilter.setPromptText("Tous les vaccins");
        vaccinFilter.setConverter(new StringConverter<TypeVaccin>() {
            @Override
            public String toString(TypeVaccin vaccin) {
                return vaccin != null ? vaccin.getLibelle() : "";
            }
            @Override
            public TypeVaccin fromString(String string) {
                return null;
            }
        });
        
        // Listeners pour filtres
        patientFilter.setOnAction(event -> handleFilterByPatient());
        vaccinFilter.setOnAction(event -> handleFilterByVaccin());
    }
    
    
    // ========== HANDLERS ==========
    
    @FXML
    private void handleNouveau() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/vaccination-form.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();
            
            VaccinationFormController formController = loader.getController();
            formController.initForCreation();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Nouvelle Vaccination");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(vaccinationTable.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);
            dialogStage.showAndWait();
            
            if (formController.isSavedSuccessfully()) {
                chargerDonnees();
                showInfo("Succès", "Vaccination enregistrée avec succès");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur", "Impossible d'ouvrir le formulaire : " + e.getMessage());
        }
    }
    
    @FXML
    private void handleModifier() {
        Vaccination selected = vaccinationTable.getSelectionModel().getSelectedItem();
        
        if (selected == null) {
            showWarning("Sélection requise", "Veuillez sélectionner une vaccination à modifier");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/vaccination-form.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();
            
            VaccinationFormController formController = loader.getController();
            formController.initForEdit(selected);
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Modifier Vaccination");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(vaccinationTable.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);
            dialogStage.showAndWait();
            
            if (formController.isSavedSuccessfully()) {
                chargerDonnees();
                showInfo("Succès", "Vaccination modifiée avec succès");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur", "Impossible d'ouvrir le formulaire : " + e.getMessage());
        }
    }
    
    @FXML
    private void handleSupprimer() {
        Vaccination selected = vaccinationTable.getSelectionModel().getSelectedItem();
        
        if (selected == null) {
            showWarning("Sélection requise", "Veuillez sélectionner une vaccination à supprimer");
            return;
        }
        
        Optional<ButtonType> result = showConfirmation(
            "Confirmer la suppression",
            "Supprimer la vaccination du " + selected.getDateAdministration().format(DATE_FORMATTER) + 
            " pour " + selected.getPatient().getPrenom() + " " + selected.getPatient().getNom() + " ?"
        );
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                vaccinationService.supprimerVaccination(selected.getId());
                chargerDonnees();
                showInfo("Succès", "Vaccination supprimée avec succès");
            } catch (Exception e) {
                showError("Erreur", "Impossible de supprimer : " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleVoirCarnet() {
        Vaccination selected = vaccinationTable.getSelectionModel().getSelectedItem();
        
        if (selected == null) {
            showWarning("Sélection requise", "Veuillez sélectionner une vaccination");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/carnet-vaccinal.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();
            
            CarnetVaccinalController carnetController = loader.getController();
            carnetController.initWithPatient(selected.getPatient());
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Carnet Vaccinal - " + selected.getPatient().getPrenom() + " " + selected.getPatient().getNom());
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(vaccinationTable.getScene().getWindow());
            dialogStage.setScene(new Scene(root, 900, 600));
            dialogStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur", "Impossible d'ouvrir le carnet vaccinal : " + e.getMessage());
        }
    }
    
    @FXML
    private void handleFilterByPatient() {
        Patient selectedPatient = patientFilter.getValue();
        
        if (selectedPatient == null) {
            chargerDonnees();
            return;
        }
        
        List<Vaccination> vaccinations = vaccinationService.obtenirVaccinationsParPatient(selectedPatient.getId());
        vaccinationData.clear();
        vaccinationData.addAll(vaccinations);
    }
    
    @FXML
    private void handleFilterByVaccin() {
        TypeVaccin selectedVaccin = vaccinFilter.getValue();
        
        if (selectedVaccin == null) {
            chargerDonnees();
            return;
        }
        
        List<Vaccination> vaccinations = vaccinationService.obtenirVaccinationsParVaccin(selectedVaccin);
        vaccinationData.clear();
        vaccinationData.addAll(vaccinations);
    }
    
    @FXML
    private void handleFilterRappels() {
        if (rappelsSeulementCheckbox.isSelected()) {
            List<Vaccination> rappels = vaccinationService.obtenirToutesVaccinations().stream()
                .filter(v -> v.getStatut() == StatutVaccination.RAPPEL_PREVU || 
                            v.getStatut() == StatutVaccination.RAPPEL_EN_RETARD)
                .toList();
            vaccinationData.clear();
            vaccinationData.addAll(rappels);
        } else {
            chargerDonnees();
        }
    }
    
    @FXML
    private void handleVoirRappelsEnRetard() {
        List<Vaccination> rappelsEnRetard = vaccinationService.obtenirRappelsEnRetard();
        vaccinationData.clear();
        vaccinationData.addAll(rappelsEnRetard);
        
        if (rappelsEnRetard.isEmpty()) {
            showInfo("Rappels en retard", "Aucun rappel en retard ✅");
        } else {
            showWarning("Rappels en retard", 
                rappelsEnRetard.size() + " rappel(s) en retard détecté(s) !");
        }
    }
    
    @FXML
    private void handleVoirRappelsProchains() {
        List<Vaccination> rappelsProchains = vaccinationService.obtenirRappelsProchains(7);
        vaccinationData.clear();
        vaccinationData.addAll(rappelsProchains);
        
        if (rappelsProchains.isEmpty()) {
            showInfo("Rappels prochains", "Aucun rappel dans les 7 prochains jours ✅");
        } else {
            showInfo("Rappels prochains", 
                rappelsProchains.size() + " rappel(s) prévu(s) dans les 7 prochains jours 🔔");
        }
    }
    
    @FXML
    private void handleRefresh() {
        patientFilter.setValue(null);
        vaccinFilter.setValue(null);
        rappelsSeulementCheckbox.setSelected(false);
        chargerDonnees();
    }
    
    
    // ========== UTILITAIRES ==========
    
    private void chargerDonnees() {
        List<Vaccination> vaccinations = vaccinationService.obtenirToutesVaccinations();
        vaccinationData.clear();
        vaccinationData.addAll(vaccinations);
    }
    
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
