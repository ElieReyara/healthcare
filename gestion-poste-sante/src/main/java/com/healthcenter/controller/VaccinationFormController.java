package com.healthcenter.controller;

import com.healthcenter.domain.entities.CalendrierVaccinal;
import com.healthcenter.domain.entities.Patient;
import com.healthcenter.domain.entities.Vaccination;
import com.healthcenter.domain.enums.TypeVaccin;
import com.healthcenter.dto.VaccinationDTO;
import com.healthcenter.service.CalendrierVaccinalService;
import com.healthcenter.service.PatientService;
import com.healthcenter.service.VaccinationService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * FORM CONTROLLER = Formulaire création/édition Vaccination.
 * 
 * Fonctionnalités :
 * - Calcul automatique date rappel selon calendrier
 * - Affichage infos calendrier vaccinal
 * - Validation dates (pas de date future)
 */
@Component
public class VaccinationFormController {
    
    @Autowired
    private VaccinationService vaccinationService;
    
    @Autowired
    private PatientService patientService;
    
    @Autowired
    private CalendrierVaccinalService calendrierService;
    
    // ========== COMPOSANTS FXML ==========
    
    @FXML private Label titleLabel;
    @FXML private ComboBox<Patient> patientCombo;
    @FXML private ComboBox<TypeVaccin> vaccinCombo;
    @FXML private Label infoCalendrierLabel;
    @FXML private DatePicker dateAdministrationPicker;
    @FXML private DatePicker dateRappelPicker;
    @FXML private CheckBox calculerRappelAutoCheckbox;
    @FXML private TextField numeroLotField;
    @FXML private TextArea observationsArea;
    @FXML private Label errorLabel;
    
    private Vaccination vaccinationEnEdition;
    private boolean savedSuccessfully = false;
    
    
    /**
     * Initialisation après chargement FXML.
     */
    @FXML
    public void initialize() {
        try {
            // Configuration ComboBox Patients
            List<Patient> patients = patientService.obtenirTousLesPatients();
            patientCombo.setItems(FXCollections.observableArrayList(patients));
            patientCombo.setConverter(new StringConverter<Patient>() {
                @Override
                public String toString(Patient patient) {
                    return patient != null ? patient.getPrenom() + " " + patient.getNom() : "";
                }
                @Override
                public Patient fromString(String string) {
                    return null;
                }
            });

            // ⚠️ Si aucun patient : désactiver formulaire
            if (patients.isEmpty()) {
                patientCombo.setDisable(true);
                errorLabel.setText("Aucun patient enregistré. Créez d'abord un patient.");
                errorLabel.setStyle("-fx-text-fill: red;");
                errorLabel.setVisible(true);
            }
            
            // Configuration ComboBox Vaccins
            vaccinCombo.setItems(FXCollections.observableArrayList(TypeVaccin.values()));
            vaccinCombo.setConverter(new StringConverter<TypeVaccin>() {
                @Override
                public String toString(TypeVaccin vaccin) {
                    return vaccin != null ? vaccin.getLibelle() : "";
                }
                @Override
                public TypeVaccin fromString(String string) {
                    return null;
                }
            });
            
            // Date administration par défaut = aujourd'hui
            dateAdministrationPicker.setValue(LocalDate.now());
            
            // Checkbox calcul auto rappel cochée par défaut
            calculerRappelAutoCheckbox.setSelected(true);
            dateRappelPicker.setDisable(true);
        } catch (Exception e) {
            System.err.println("❌ Erreur initialisation VaccinationFormController : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    // ========== MODES D'INITIALISATION ==========
    
    /**
     * Mode CRÉATION.
     */
    public void initForCreation() {
        vaccinationEnEdition = null;
        savedSuccessfully = false;
        titleLabel.setText("💉 Nouvelle Vaccination");
        errorLabel.setVisible(false);
    }
    
    /**
     * Mode ÉDITION.
     */
    public void initForEdit(Vaccination vaccination) {
        this.vaccinationEnEdition = vaccination;
        this.savedSuccessfully = false;
        
        titleLabel.setText("✏️ Modifier Vaccination");
        
        // Pré-remplir formulaire
        patientCombo.setValue(vaccination.getPatient());
        vaccinCombo.setValue(vaccination.getVaccin());
        dateAdministrationPicker.setValue(vaccination.getDateAdministration());
        
        if (vaccination.getDateRappel() != null) {
            calculerRappelAutoCheckbox.setSelected(false);
            dateRappelPicker.setDisable(false);
            dateRappelPicker.setValue(vaccination.getDateRappel());
        }
        
        numeroLotField.setText(vaccination.getNumeroLot());
        observationsArea.setText(vaccination.getObservations());
        
        // Afficher info calendrier
        handleVaccinSelected();
        
        errorLabel.setVisible(false);
    }
    
    
    // ========== HANDLERS ==========
    
    @FXML
    private void handleVaccinSelected() {
        TypeVaccin selectedVaccin = vaccinCombo.getValue();
        
        if (selectedVaccin == null) {
            infoCalendrierLabel.setText("");
            return;
        }
        
        Optional<CalendrierVaccinal> calendrierOpt = calendrierService.obtenirCalendrierParVaccin(selectedVaccin);
        
        if (calendrierOpt.isEmpty()) {
            infoCalendrierLabel.setText("ℹ️ Pas de calendrier défini pour ce vaccin");
            infoCalendrierLabel.setStyle("-fx-text-fill: gray; -fx-font-style: italic;");
            return;
        }
        
        CalendrierVaccinal calendrier = calendrierOpt.get();
        int ageJours = calendrier.getAgeRecommande();
        int ageMois = ageJours / 30;
        int ageSemaines = ageJours / 7;
        
        String ageText;
        if (ageJours == 0) {
            ageText = "naissance";
        } else if (ageMois > 0) {
            ageText = ageMois + " mois";
        } else {
            ageText = ageSemaines + " semaines";
        }
        
        String rappelText = calendrier.getNombreRappels() > 0 
            ? calendrier.getNombreRappels() + " rappel(s) à " + calendrier.getDelaiRappel() + " jours d'intervalle"
            : "Pas de rappels";
        
        infoCalendrierLabel.setText(
            "📋 Âge recommandé : " + ageText + " | " + rappelText
        );
        infoCalendrierLabel.setStyle("-fx-text-fill: #2196F3; -fx-font-style: italic;");
    }
    
    @FXML
    private void handlePatientSelected() {
        // Vérifier si patient a déjà reçu ce vaccin
        Patient selectedPatient = patientCombo.getValue();
        TypeVaccin selectedVaccin = vaccinCombo.getValue();
        
        if (selectedPatient != null && selectedVaccin != null) {
            List<Vaccination> vaccinations = vaccinationService.obtenirVaccinationsParPatient(selectedPatient.getId());
            boolean dejaRecu = vaccinations.stream()
                .anyMatch(v -> v.getVaccin() == selectedVaccin);
            
            if (dejaRecu && vaccinationEnEdition == null) {
                errorLabel.setText("⚠️ Attention : Ce patient a déjà reçu ce vaccin");
                errorLabel.setStyle("-fx-text-fill: orange;");
                errorLabel.setVisible(true);
            }
        }
    }
    
    @FXML
    private void handleCalculerRappelAuto() {
        if (calculerRappelAutoCheckbox.isSelected()) {
            dateRappelPicker.setDisable(true);
            dateRappelPicker.setValue(null);
        } else {
            dateRappelPicker.setDisable(false);
        }
    }
    
    @FXML
    private void handleSave() {
        // Validation
        if (!validerFormulaire()) {
            return;
        }
        
        try {
            // Création DTO
            VaccinationDTO dto = new VaccinationDTO();
            dto.setPatientId(patientCombo.getValue().getId());
            dto.setVaccin(vaccinCombo.getValue().name());
            dto.setDateAdministration(dateAdministrationPicker.getValue());
            
            // Date rappel : auto ou manuelle
            if (calculerRappelAutoCheckbox.isSelected()) {
                dto.setDateRappel(null); // Service calculera automatiquement
            } else {
                dto.setDateRappel(dateRappelPicker.getValue());
            }
            
            dto.setNumeroLot(numeroLotField.getText());
            dto.setObservations(observationsArea.getText());
            
            if (vaccinationEnEdition == null) {
                // MODE CRÉATION
                vaccinationService.creerVaccination(dto);
            } else {
                // MODE ÉDITION
                vaccinationService.mettreAJourVaccination(vaccinationEnEdition.getId(), dto);
            }
            
            savedSuccessfully = true;
            fermerFenetre();
            
        } catch (IllegalArgumentException e) {
            errorLabel.setText(e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setVisible(true);
        } catch (Exception e) {
            errorLabel.setText("Erreur : " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setVisible(true);
        }
    }
    
    @FXML
    private void handleCancel() {
        savedSuccessfully = false;
        fermerFenetre();
    }
    
    
    // ========== VALIDATION ==========
    
    private boolean validerFormulaire() {
        // Patient obligatoire
        if (patientCombo.getValue() == null) {
            errorLabel.setText("Le patient est obligatoire");
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setVisible(true);
            return false;
        }
        
        // Vaccin obligatoire
        if (vaccinCombo.getValue() == null) {
            errorLabel.setText("Le vaccin est obligatoire");
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setVisible(true);
            return false;
        }
        
        // Date administration obligatoire
        if (dateAdministrationPicker.getValue() == null) {
            errorLabel.setText("La date d'administration est obligatoire");
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setVisible(true);
            return false;
        }
        
        // Date administration pas dans le futur
        if (dateAdministrationPicker.getValue().isAfter(LocalDate.now())) {
            errorLabel.setText("La date d'administration ne peut pas être dans le futur");
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setVisible(true);
            return false;
        }
        
        // Si date rappel manuelle, vérifier cohérence
        if (!calculerRappelAutoCheckbox.isSelected() && dateRappelPicker.getValue() != null) {
            if (dateRappelPicker.getValue().isBefore(dateAdministrationPicker.getValue())) {
                errorLabel.setText("La date de rappel doit être après la date d'administration");
                errorLabel.setStyle("-fx-text-fill: red;");
                errorLabel.setVisible(true);
                return false;
            }
        }
        
        return true;
    }
    
    
    // ========== UTILITAIRES ==========
    
    private void fermerFenetre() {
        Stage stage = (Stage) patientCombo.getScene().getWindow();
        stage.close();
    }
    
    public boolean isSavedSuccessfully() {
        return savedSuccessfully;
    }
}
