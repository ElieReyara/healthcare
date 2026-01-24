package com.healthcenter.controller;

import com.healthcenter.domain.entities.Patient;
import com.healthcenter.domain.enums.Sexe;
import com.healthcenter.dto.PatientDTO;
import com.healthcenter.service.PatientService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Controller du formulaire Patient (création/modification).
 */
@Component
public class PatientFormController {
    
    @Autowired
    private PatientService patientService;
    
    // ========== COMPOSANTS FXML ==========
    
    @FXML private Label titleLabel;
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private DatePicker dateNaissancePicker;
    @FXML private RadioButton radioHomme;
    @FXML private RadioButton radioFemme;
    @FXML private RadioButton radioAutre;
    @FXML private TextField telephoneField;
    @FXML private TextField adresseField;
    @FXML private TextField numeroCarnetField;
    @FXML private Label errorLabel;
    
    // ========== VARIABLES INTERNES ==========
    
    private Patient patientEnEdition;  // null si création, sinon modification
    private boolean savedSuccessfully = false;
    
    
    /**
     * Initialise le formulaire en MODE CRÉATION.
     */
    public void initForCreation() {
        titleLabel.setText("Nouveau Patient");
        patientEnEdition = null;
    }
    
    /**
     * Initialise le formulaire en MODE MODIFICATION.
     * Pré-remplit les champs avec les données du patient.
     */
    public void initForEdit(Patient patient) {
        titleLabel.setText("Modifier Patient");
        patientEnEdition = patient;
        
        // Pré-remplir les champs
        nomField.setText(patient.getNom());
        prenomField.setText(patient.getPrenom());
        dateNaissancePicker.setValue(patient.getDateNaissance());
        
        // Sélectionner le bon radio button
        switch (patient.getSexe()) {
            case HOMME -> radioHomme.setSelected(true);
            case FEMME -> radioFemme.setSelected(true);
        }
        
        telephoneField.setText(patient.getTelephone());
        adresseField.setText(patient.getAdresse());
        numeroCarnetField.setText(patient.getNumeroCarnet());
    }
    
    
    // ========== HANDLERS ==========
    
    @FXML
    private void handleSave() {
        errorLabel.setVisible(false);
        
        try {
            // VALIDATION
            if (!validerFormulaire()) {
                return;  // Erreur affichée dans validerFormulaire()
            }
            
            // CRÉER DTO depuis formulaire
            PatientDTO dto = new PatientDTO();
            dto.setNom(nomField.getText().trim());
            dto.setPrenom(prenomField.getText().trim());
            dto.setDateNaissance(dateNaissancePicker.getValue());
            dto.setSexe(getSexeSelectionne());
            dto.setTelephone(telephoneField.getText().trim());
            dto.setAdresse(adresseField.getText().trim());
            dto.setNumeroCarnet(numeroCarnetField.getText().trim());
            
            // SAUVEGARDER (création ou modification)
            if (patientEnEdition == null) {
                // MODE CRÉATION
                patientService.creerPatient(dto);
            } else {
                // MODE MODIFICATION
                patientService.mettreAJourPatient(patientEnEdition.getId(), dto);
            }
            
            savedSuccessfully = true;
            fermerFenetre();
            
        } catch (IllegalArgumentException e) {
            // Erreur validation métier (ex: numéro carnet existe déjà)
            afficherErreur(e.getMessage());
        } catch (Exception e) {
            afficherErreur("Erreur inattendue : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleCancel() {
        fermerFenetre();
    }
    
    
    // ========== VALIDATION ==========
    
    /**
     * Valide le formulaire.
     * @return true si valide, false sinon (affiche message erreur)
     */
    private boolean validerFormulaire() {
        // Nom obligatoire
        if (nomField.getText() == null || nomField.getText().trim().isEmpty()) {
            afficherErreur("Le nom est obligatoire");
            nomField.requestFocus();
            return false;
        }
        
        // Prénom obligatoire
        if (prenomField.getText() == null || prenomField.getText().trim().isEmpty()) {
            afficherErreur("Le prénom est obligatoire");
            prenomField.requestFocus();
            return false;
        }
        
        // Date naissance obligatoire
        if (dateNaissancePicker.getValue() == null) {
            afficherErreur("La date de naissance est obligatoire");
            dateNaissancePicker.requestFocus();
            return false;
        }
        
        // Date naissance pas dans le futur
        if (dateNaissancePicker.getValue().isAfter(LocalDate.now())) {
            afficherErreur("La date de naissance ne peut pas être dans le futur");
            dateNaissancePicker.requestFocus();
            return false;
        }
        
        // Sexe obligatoire (toujours sélectionné via RadioButton)
        
        return true;
    }
    
    
    // ========== HELPERS ==========
    
    private Sexe getSexeSelectionne() {
        if (radioHomme.isSelected()) 
            {return Sexe.HOMME;}
        return Sexe.FEMME;
    
    }
    
    private void afficherErreur(String message) {
        errorLabel.setText("❌ " + message);
        errorLabel.setVisible(true);
    }
    
    private void fermerFenetre() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Pour savoir si sauvegarde réussie (appelé par PatientController).
     */
    public boolean isSavedSuccessfully() {
        return savedSuccessfully;
    }
}
