package com.healthcenter.controller;

import com.healthcenter.domain.entities.Medicament;
import com.healthcenter.domain.enums.FormeMedicament;
import com.healthcenter.dto.MedicamentDTO;
import com.healthcenter.service.MedicamentService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * FORM CONTROLLER = Formulaire création/édition Médicament.
 */
@Component
public class MedicamentFormController {
    
    @Autowired
    private MedicamentService medicamentService;
    
    // ========== COMPOSANTS FXML ==========
    
    @FXML private TextField nomField;
    @FXML private TextField dosageField;
    @FXML private ComboBox<FormeMedicament> formeCombo;
    @FXML private TextField prixField;
    @FXML private Spinner<Integer> stockSpinner;
    @FXML private Spinner<Integer> seuilSpinner;
    @FXML private Label errorLabel;
    
    private Medicament medicamentEnEdition;
    private boolean savedSuccessfully = false;
    
    
    /**
     * Initialisation après chargement FXML.
     */
    @FXML
    public void initialize() {
        // Configuration ComboBox Forme
        formeCombo.setItems(FXCollections.observableArrayList(FormeMedicament.values()));
        formeCombo.setConverter(new StringConverter<FormeMedicament>() {
            @Override
            public String toString(FormeMedicament forme) {
                return forme != null ? forme.getLibelle() : "";
            }
            
            @Override
            public FormeMedicament fromString(String string) {
                return null;
            }
        });
        
        // Configuration Spinners
        stockSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9999, 0));
        stockSpinner.setEditable(true);
        
        seuilSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9999, 0));
        seuilSpinner.setEditable(true);
    }
    
    
    // ========== MODES D'INITIALISATION ==========
    
    /**
     * Mode CRÉATION.
     */
    public void initForCreation() {
        medicamentEnEdition = null;
        savedSuccessfully = false;
        errorLabel.setVisible(false);
    }
    
    /**
     * Mode ÉDITION.
     */
    public void initForEdit(Medicament medicament) {
        this.medicamentEnEdition = medicament;
        this.savedSuccessfully = false;
        
        // Pré-remplir le formulaire
        nomField.setText(medicament.getNom());
        dosageField.setText(medicament.getDosage());
        formeCombo.setValue(medicament.getForme());
        prixField.setText(medicament.getPrix() != null ? medicament.getPrix().toString() : "");
        stockSpinner.getValueFactory().setValue(medicament.getStockActuel());
        seuilSpinner.getValueFactory().setValue(medicament.getSeuilAlerte() != null ? medicament.getSeuilAlerte() : 0);
        
        errorLabel.setVisible(false);
    }
    
    
    // ========== HANDLERS ==========
    
    @FXML
    private void handleSave() {
        // Validation formulaire
        String erreur = validerFormulaire();
        if (erreur != null) {
            errorLabel.setText(erreur);
            errorLabel.setVisible(true);
            return;
        }
        
        try {
            // Création DTO
            MedicamentDTO dto = new MedicamentDTO();
            dto.setNom(nomField.getText().trim());
            dto.setDosage(dosageField.getText().trim());
            dto.setForme(formeCombo.getValue().name());
            dto.setPrix(new BigDecimal(prixField.getText().trim()));
            dto.setStockActuel(stockSpinner.getValue());
            dto.setSeuilAlerte(seuilSpinner.getValue() > 0 ? seuilSpinner.getValue() : null);
            
            if (medicamentEnEdition == null) {
                // MODE CRÉATION
                medicamentService.creerMedicament(dto);
            } else {
                // MODE ÉDITION
                medicamentService.mettreAJourMedicament(medicamentEnEdition.getId(), dto);
            }
            
            savedSuccessfully = true;
            fermerFenetre();
            
        } catch (IllegalArgumentException e) {
            errorLabel.setText(e.getMessage());
            errorLabel.setVisible(true);
        } catch (Exception e) {
            errorLabel.setText("Erreur : " + e.getMessage());
            errorLabel.setVisible(true);
        }
    }
    
    @FXML
    private void handleCancel() {
        savedSuccessfully = false;
        fermerFenetre();
    }
    
    
    // ========== VALIDATION ==========
    
    private String validerFormulaire() {
        // Nom obligatoire
        if (nomField.getText() == null || nomField.getText().trim().isEmpty()) {
            return "Le nom est obligatoire";
        }
        
        // Dosage obligatoire
        if (dosageField.getText() == null || dosageField.getText().trim().isEmpty()) {
            return "Le dosage est obligatoire";
        }
        
        // Forme obligatoire
        if (formeCombo.getValue() == null) {
            return "La forme est obligatoire";
        }
        
        // Prix obligatoire et valide
        if (prixField.getText() == null || prixField.getText().trim().isEmpty()) {
            return "Le prix est obligatoire";
        }
        
        try {
            BigDecimal prix = new BigDecimal(prixField.getText().trim());
            if (prix.compareTo(BigDecimal.ZERO) <= 0) {
                return "Le prix doit être positif";
            }
        } catch (NumberFormatException e) {
            return "Prix invalide (format attendu: 1500.50)";
        }
        
        return null; // Formulaire valide
    }
    
    
    // ========== UTILITAIRES ==========
    
    private void fermerFenetre() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }
    
    public boolean isSavedSuccessfully() {
        return savedSuccessfully;
    }
}
