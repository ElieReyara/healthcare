package com.healthcenter.controller;

import com.healthcenter.domain.enums.RoleUtilisateur;
import com.healthcenter.service.UtilisateurService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UtilisateurFormController {

    @Autowired
    private UtilisateurService utilisateurService;

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private ComboBox<RoleUtilisateur> roleCombo;
    @FXML private Label errorLabel;

    private boolean savedSuccessfully = false;

    @FXML
    public void initialize() {
        roleCombo.setItems(FXCollections.observableArrayList(RoleUtilisateur.values()));
        roleCombo.setValue(RoleUtilisateur.RECEPTIONNISTE);
    }

    public void initForCreation() {
        savedSuccessfully = false;
    }

    @FXML
    private void handleSave() {
        errorLabel.setVisible(false);

        String username = usernameField.getText() != null ? usernameField.getText().trim() : "";
        String password = passwordField.getText() != null ? passwordField.getText().trim() : "";
        String nom = nomField.getText() != null ? nomField.getText().trim() : "";
        String prenom = prenomField.getText() != null ? prenomField.getText().trim() : "";
        RoleUtilisateur role = roleCombo.getValue();

        if (username.isEmpty() || password.isEmpty() || nom.isEmpty() || prenom.isEmpty() || role == null) {
            showValidation("Tous les champs marqués * sont obligatoires.");
            return;
        }
        if (password.length() < 6) {
            showValidation("Mot de passe trop court (minimum 6 caractères).");
            return;
        }

        try {
            utilisateurService.creerUtilisateur(username, password, nom, prenom, role);
            savedSuccessfully = true;
            closeWindow();
        } catch (IllegalArgumentException e) {
            showValidation(e.getMessage());
        } catch (Exception e) {
            showValidation("Erreur inattendue : " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        savedSuccessfully = false;
        closeWindow();
    }

    public boolean isSavedSuccessfully() {
        return savedSuccessfully;
    }

    private void closeWindow() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }

    private void showValidation(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
