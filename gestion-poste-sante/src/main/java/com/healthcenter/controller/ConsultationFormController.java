package com.healthcenter.controller;

import com.healthcenter.domain.entities.Consultation;
import com.healthcenter.domain.entities.Patient;
import com.healthcenter.dto.ConsultationDTO;
import com.healthcenter.service.ConsultationService;
import com.healthcenter.service.PatientService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Controller du formulaire Consultation (création/modification).
 * 
 * Différences vs PatientFormController :
 * - ComboBox Patient (relation obligatoire)
 * - Date + Heure séparés (LocalDateTime = LocalDate + LocalTime)
 * - TextArea multi-lignes (symptômes, diagnostic, prescription)
 */
@Component
public class ConsultationFormController {
    
    @Autowired
    private ConsultationService consultationService;
    
    @Autowired
    private PatientService patientService;
    
    // ========== COMPOSANTS FXML ==========
    
    @FXML private Label titleLabel;
    @FXML private ComboBox<Patient> patientCombo;
    @FXML private DatePicker datePicker;
    @FXML private TextField heureField;
    @FXML private TextArea symptomesArea;
    @FXML private TextArea diagnosticArea;
    @FXML private TextArea prescriptionArea;
    @FXML private Label errorLabel;
    
    // ========== VARIABLES INTERNES ==========
    
    private Consultation consultationEnEdition;  // null si création, sinon modification
    private boolean savedSuccessfully = false;
    private ObservableList<Patient> patientsData = FXCollections.observableArrayList();
    
    // Formatter pour heure (HH:mm)
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    
    /**
     * Méthode appelée automatiquement après chargement FXML.
     * Initialise le ComboBox patients.
     */
    @FXML
    public void initialize() {
        setupPatientComboBox();
    }
    
    
    // ========== SETUP COMBOBOX ==========
    
    /**
     * Configure le ComboBox de sélection patient.
     */
    private void setupPatientComboBox() {
        // Charger tous les patients
        List<Patient> patients = patientService.obtenirTousLesPatients();
        patientsData.clear();
        patientsData.addAll(patients);
        
        patientCombo.setItems(patientsData);
        
        // Formatter affichage ComboBox : "Nom Prénom (N° Carnet)"
        patientCombo.setConverter(new StringConverter<Patient>() {
            @Override
            public String toString(Patient patient) {
                if (patient == null) return "";
                
                String display = patient.getNom() + " " + patient.getPrenom();
                
                // Ajouter numéro carnet si disponible
                if (patient.getNumeroCarnet() != null && !patient.getNumeroCarnet().isEmpty()) {
                    display += " (" + patient.getNumeroCarnet() + ")";
                }
                
                return display;
            }
            
            @Override
            public Patient fromString(String string) {
                return null;  // Non utilisé (lecture seule)
            }
        });
        
        // Message si aucun patient
        if (patientsData.isEmpty()) {
            patientCombo.setDisable(true);
            afficherErreur("Aucun patient enregistré. Créez d'abord un patient.");
        }
    }
    
    
    // ========== MODES INITIALISATION ==========
    
    /**
     * Initialise le formulaire en MODE CRÉATION.
     */
    public void initForCreation() {
        titleLabel.setText("Nouvelle Consultation");
        consultationEnEdition = null;
        
        // Pré-remplir date aujourd'hui + heure actuelle
        datePicker.setValue(LocalDate.now());
        heureField.setText(LocalTime.now().format(TIME_FORMATTER));
    }
    
    /**
     * Initialise le formulaire en MODE MODIFICATION.
     * Pré-remplit les champs avec les données de la consultation.
     * 
     * @param consultation Consultation à modifier
     */
    public void initForEdit(Consultation consultation) {
        titleLabel.setText("Modifier Consultation");
        consultationEnEdition = consultation;
        
        // Pré-remplir patient
        patientCombo.setValue(consultation.getPatient());
        
        // Pré-remplir date + heure (extraits de LocalDateTime)
        LocalDateTime dateConsultation = consultation.getDateConsultation();
        datePicker.setValue(dateConsultation.toLocalDate());
        heureField.setText(dateConsultation.toLocalTime().format(TIME_FORMATTER));
        
        // Pré-remplir TextArea
        symptomesArea.setText(consultation.getSymptomes());
        diagnosticArea.setText(consultation.getDiagnostic());
        prescriptionArea.setText(consultation.getPrescription());
    }
    
    
    // ========== HANDLERS ==========
    
    /**
     * Handler bouton Enregistrer.
     * Valide formulaire → crée ConsultationDTO → appelle Service.
     */
    @FXML
    private void handleSave() {
        errorLabel.setVisible(false);
        
        try {
            // VALIDATION
            if (!validerFormulaire()) {
                return;  // Erreur affichée dans validerFormulaire()
            }
            
            // CRÉER DTO depuis formulaire
            ConsultationDTO dto = new ConsultationDTO();
            dto.setPatientId(patientCombo.getValue().getId());
            dto.setDateConsultation(construireLocalDateTime());
            dto.setSymptomes(symptomesArea.getText().trim());
            dto.setDiagnostic(diagnosticArea.getText().trim());
            dto.setPrescription(prescriptionArea.getText().trim());
            
            // SAUVEGARDER (création ou modification)
            if (consultationEnEdition == null) {
                // MODE CRÉATION
                consultationService.creerConsultation(dto);
            } else {
                // MODE MODIFICATION
                consultationService.mettreAJourConsultation(consultationEnEdition.getId(), dto);
            }
            
            savedSuccessfully = true;
            fermerFenetre();
            
        } catch (IllegalArgumentException e) {
            // Erreur validation métier (ex: patient inexistant)
            afficherErreur(e.getMessage());
        } catch (Exception e) {
            afficherErreur("Erreur inattendue : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Handler bouton Annuler.
     * Ferme la fenêtre sans sauvegarder.
     */
    @FXML
    private void handleCancel() {
        fermerFenetre();
    }
    
    
    // ========== VALIDATION ==========
    
    /**
     * Valide le formulaire avant sauvegarde.
     * @return true si valide, false sinon (affiche message erreur)
     */
    private boolean validerFormulaire() {
        // Patient obligatoire
        if (patientCombo.getValue() == null) {
            afficherErreur("Veuillez sélectionner un patient");
            patientCombo.requestFocus();
            return false;
        }
        
        // Date obligatoire
        if (datePicker.getValue() == null) {
            afficherErreur("La date de consultation est obligatoire");
            datePicker.requestFocus();
            return false;
        }
        
        // Date ne peut pas être dans le futur
        if (datePicker.getValue().isAfter(LocalDate.now())) {
            afficherErreur("La date de consultation ne peut pas être dans le futur");
            datePicker.requestFocus();
            return false;
        }
        
        // Heure obligatoire
        String heureText = heureField.getText();
        if (heureText == null || heureText.trim().isEmpty()) {
            afficherErreur("L'heure est obligatoire");
            heureField.requestFocus();
            return false;
        }
        
        // Heure format valide (HH:mm)
        try {
            LocalTime.parse(heureText.trim(), TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            afficherErreur("Format heure invalide. Utilisez HH:mm (ex: 14:30)");
            heureField.requestFocus();
            return false;
        }
        
        // Date + heure ne peut pas être dans le futur
        LocalDateTime dateTimeConsultation = construireLocalDateTime();
        if (dateTimeConsultation.isAfter(LocalDateTime.now())) {
            afficherErreur("La date/heure de consultation ne peut pas être dans le futur");
            heureField.requestFocus();
            return false;
        }
        
        // Au moins symptômes OU diagnostic renseigné
        String symptomes = symptomesArea.getText();
        String diagnostic = diagnosticArea.getText();
        
        boolean aSymptomes = symptomes != null && !symptomes.trim().isEmpty();
        boolean aDiagnostic = diagnostic != null && !diagnostic.trim().isEmpty();
        
        if (!aSymptomes && !aDiagnostic) {
            afficherErreur("Veuillez renseigner au moins les symptômes ou le diagnostic");
            symptomesArea.requestFocus();
            return false;
        }
        
        // Vérifier limites caractères
        if (symptomes != null && symptomes.length() > 500) {
            afficherErreur("Symptômes : maximum 500 caractères (" + symptomes.length() + " actuellement)");
            symptomesArea.requestFocus();
            return false;
        }
        
        if (diagnostic != null && diagnostic.length() > 500) {
            afficherErreur("Diagnostic : maximum 500 caractères (" + diagnostic.length() + " actuellement)");
            diagnosticArea.requestFocus();
            return false;
        }
        
        String prescription = prescriptionArea.getText();
        if (prescription != null && prescription.length() > 1000) {
            afficherErreur("Prescription : maximum 1000 caractères (" + prescription.length() + " actuellement)");
            prescriptionArea.requestFocus();
            return false;
        }
        
        return true;
    }
    
    
    // ========== HELPERS ==========
    
    /**
     * Construit LocalDateTime depuis DatePicker + TextField heure.
     * @return LocalDateTime complet
     */
    private LocalDateTime construireLocalDateTime() {
        LocalDate date = datePicker.getValue();
        LocalTime time = LocalTime.parse(heureField.getText().trim(), TIME_FORMATTER);
        return LocalDateTime.of(date, time);
    }
    
    /**
     * Affiche message erreur en rouge.
     */
    private void afficherErreur(String message) {
        errorLabel.setText("❌ " + message);
        errorLabel.setVisible(true);
    }
    
    /**
     * Ferme la fenêtre (Stage).
     */
    private void fermerFenetre() {
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Pour savoir si sauvegarde réussie (appelé par ConsultationController).
     * @return true si sauvegarde OK
     */
    public boolean isSavedSuccessfully() {
        return savedSuccessfully;
    }
}
