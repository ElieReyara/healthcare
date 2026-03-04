package com.healthcenter.controller;

import com.healthcenter.domain.entities.Personnel;
import com.healthcenter.domain.enums.FonctionPersonnel;
import com.healthcenter.dto.PersonnelDTO;
import com.healthcenter.service.PersonnelService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * CONTROLLER JavaFX - Formulaire Personnel
 * 
 * Gère la création et modification d'un personnel.
 * 
 * Modes :
 * - Création : formulaire vide
 * - Édition : formulaire pré-rempli avec données personnel
 * 
 * @author Health Center Team
 */
@Component
public class PersonnelFormController {
    
    @Autowired
    private PersonnelService personnelService;
    
    // ========== COMPOSANTS FXML ==========
    
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private ComboBox<FonctionPersonnel> fonctionCombo;
    @FXML private TextField specialisationField;
    @FXML private TextField telephoneField;
    @FXML private TextField emailField;
    @FXML private TextArea adresseArea;
    @FXML private TextField numeroMatriculeField;
    @FXML private DatePicker dateEmbauchePicker;
    @FXML private CheckBox actifCheckbox;
    @FXML private Label errorLabel;
    
    private Personnel personnelEnCours;
    private boolean savedSuccessfully = false;
    
    
    /**
     * Initialisation après chargement FXML.
     */
    @FXML
    public void initialize() {
        // Remplir ComboBox fonctions
        fonctionCombo.getItems().addAll(FonctionPersonnel.values());
        
        // Converter pour afficher libellés
        fonctionCombo.setConverter(new javafx.util.StringConverter<FonctionPersonnel>() {
            @Override
            public String toString(FonctionPersonnel fonction) {
                return fonction == null ? "" : fonction.getLibelle();
            }
            
            @Override
            public FonctionPersonnel fromString(String string) {
                return null;
            }
        });
        
        // Listener changement fonction
        fonctionCombo.setOnAction(event -> handleFonctionSelected());

        numeroMatriculeField.setDisable(true);
        numeroMatriculeField.setPromptText("Généré automatiquement (>= 1000)");
        
        errorLabel.setVisible(false);
    }
    
    /**
     * Initialise formulaire en mode CRÉATION.
     */
    public void initForCreation() {
        actifCheckbox.setSelected(true);
        numeroMatriculeField.clear();
        numeroMatriculeField.setText("Automatique");
        personnelEnCours = null;
    }
    
    /**
     * Initialise formulaire en mode ÉDITION.
     * 
     * @param personnel Personnel à modifier
     */
    public void initForEdit(Personnel personnel) {
        personnelEnCours = personnel;
        
        // Pré-remplir champs
        nomField.setText(personnel.getNom());
        prenomField.setText(personnel.getPrenom());
        fonctionCombo.setValue(personnel.getFonction());
        specialisationField.setText(personnel.getSpecialisation());
        telephoneField.setText(personnel.getTelephone());
        emailField.setText(personnel.getEmail());
        adresseArea.setText(personnel.getAdresse());
        numeroMatriculeField.setText(personnel.getNumeroMatricule());
        dateEmbauchePicker.setValue(personnel.getDateEmbauche());
        actifCheckbox.setSelected(personnel.getActif());
    }
    
    /**
     * Handler changement fonction.
     * 
     * Affiche champ spécialisation si MEDECIN sélectionné.
     */
    @FXML
    private void handleFonctionSelected() {
        FonctionPersonnel fonction = fonctionCombo.getValue();
        
        if (fonction == FonctionPersonnel.MEDECIN) {
            specialisationField.setPromptText("Ex: Pédiatrie, Gynécologie...");
            specialisationField.setDisable(false);
        } else {
            specialisationField.setPromptText("Non applicable");
            specialisationField.setDisable(true);
            specialisationField.clear();
        }
    }
    
    /**
     * Handler bouton Enregistrer.
     */
    @FXML
    private void handleSave() {
        if (!validerFormulaire()) {
            return;
        }
        
        try {
            PersonnelDTO dto = creerDTO();
            
            if (personnelEnCours == null) {
                // Mode création
                personnelService.creerPersonnel(dto);
            } else {
                // Mode édition
                personnelService.mettreAJourPersonnel(personnelEnCours.getId(), dto);
            }
            
            savedSuccessfully = true;
            fermer();
        } catch (IllegalArgumentException e) {
            afficherErreur(e.getMessage());
        } catch (Exception e) {
            afficherErreur("Erreur inattendue : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Handler bouton Annuler.
     */
    @FXML
    private void handleCancel() {
        savedSuccessfully = false;
        fermer();
    }
    
    /**
     * Valide le formulaire.
     * 
     * @return true si formulaire valide
     */
    private boolean validerFormulaire() {
        errorLabel.setVisible(false);
        
        // Champs obligatoires
        if (nomField.getText() == null || nomField.getText().trim().isEmpty()) {
            afficherErreur("Le nom est obligatoire");
            nomField.requestFocus();
            return false;
        }
        
        if (prenomField.getText() == null || prenomField.getText().trim().isEmpty()) {
            afficherErreur("Le prénom est obligatoire");
            prenomField.requestFocus();
            return false;
        }
        
        if (fonctionCombo.getValue() == null) {
            afficherErreur("La fonction est obligatoire");
            fonctionCombo.requestFocus();
            return false;
        }
        
        // Validation email format si fourni
        String email = emailField.getText();
        if (email != null && !email.trim().isEmpty()) {
            if (!isEmailValide(email)) {
                afficherErreur("Format email invalide");
                emailField.requestFocus();
                return false;
            }
        }
        
        // Validation téléphone format Sénégal si fourni (optionnel)
        String telephone = telephoneField.getText();
        if (telephone != null && !telephone.trim().isEmpty()) {
            if (!isTelephoneSenegalValide(telephone)) {
                afficherErreur("Format téléphone invalide. Exemples valides : 77 123 45 67, +221771234567, 771234567");
                telephoneField.requestFocus();
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Crée un DTO depuis les champs du formulaire.
     * 
     * @return PersonnelDTO rempli
     */
    private PersonnelDTO creerDTO() {
        PersonnelDTO dto = new PersonnelDTO();
        dto.setNom(nomField.getText().trim());
        dto.setPrenom(prenomField.getText().trim());
        dto.setFonction(fonctionCombo.getValue().name());
        dto.setSpecialisation(specialisationField.getText() != null ? specialisationField.getText().trim() : null);
        dto.setTelephone(telephoneField.getText() != null ? telephoneField.getText().trim() : null);
        dto.setEmail(emailField.getText() != null ? emailField.getText().trim() : null);
        dto.setAdresse(adresseArea.getText() != null ? adresseArea.getText().trim() : null);
        dto.setNumeroMatricule(numeroMatriculeField.getText() != null ? numeroMatriculeField.getText().trim() : null);
        dto.setDateEmbauche(dateEmbauchePicker.getValue());
        dto.setActif(actifCheckbox.isSelected());
        
        return dto;
    }
    
    /**
     * @return true si formulaire sauvegardé avec succès
     */
    public boolean isSavedSuccessfully() {
        return savedSuccessfully;
    }
    
    /**
     * Ferme la fenêtre.
     */
    private void fermer() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Affiche message d'erreur.
     * 
     * @param message Message erreur
     */
    private void afficherErreur(String message) {
        errorLabel.setText("❌ " + message);
        errorLabel.setVisible(true);
    }
    
    
    // ========== VALIDATIONS ==========
    
    /**
     * Valide format email.
     * 
     * @param email Email à valider
     * @return true si format valide
     */
    private boolean isEmailValide(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
    
    /**
     * Valide format téléphone Sénégal.
     * 
     * Formats acceptés :
     * - 77 123 45 67
     * - 771234567
     * - +221771234567
     * - +221 77 123 45 67
     * 
     * @param telephone Téléphone à valider
     * @return true si format valide
     */
    private boolean isTelephoneSenegalValide(String telephone) {
        // Nettoyer espaces
        String tel = telephone.replaceAll("\\s+", "");
        
        // Formats acceptés
        return tel.matches("^(\\+221)?[37][0378]\\d{7}$");
    }
}
