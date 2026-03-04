package com.healthcenter.controller;

import com.healthcenter.domain.entities.Personnel;
import com.healthcenter.domain.enums.RoleUtilisateur;
import com.healthcenter.service.PersonnelService;
import com.healthcenter.service.UtilisateurService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UtilisateurFormController {

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private PersonnelService personnelService;

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<Personnel> personnelCombo;
    @FXML private ComboBox<RoleUtilisateur> roleCombo;
    @FXML private Label errorLabel;

    private boolean savedSuccessfully = false;

    @FXML
    public void initialize() {
        roleCombo.setItems(FXCollections.observableArrayList(RoleUtilisateur.values()));
        roleCombo.setValue(RoleUtilisateur.RECEPTIONNISTE);
        roleCombo.setDisable(true);

        personnelCombo.setConverter(new StringConverter<Personnel>() {
            @Override
            public String toString(Personnel personnel) {
                if (personnel == null) {
                    return "";
                }
                String matricule = personnel.getNumeroMatricule() != null ? personnel.getNumeroMatricule() : "SANS-MATRICULE";
                return matricule + " - " + personnel.getPrenom() + " " + personnel.getNom();
            }

            @Override
            public Personnel fromString(String string) {
                return null;
            }
        });

        chargerPersonnelDisponible();
    }

    public void initForCreation() {
        savedSuccessfully = false;
    }

    @FXML
    private void handleSave() {
        errorLabel.setVisible(false);

        String username = usernameField.getText() != null ? usernameField.getText().trim() : "";
        String password = passwordField.getText() != null ? passwordField.getText().trim() : "";
        Personnel personnel = personnelCombo.getValue();
        RoleUtilisateur role = roleCombo.getValue();

        if (username.isEmpty() || password.isEmpty() || personnel == null || role == null) {
            showValidation("Tous les champs marqués * sont obligatoires.");
            return;
        }
        if (password.length() < 6) {
            showValidation("Mot de passe trop court (minimum 6 caractères).");
            return;
        }

        try {
            utilisateurService.creerUtilisateurPourPersonnel(username, password, personnel.getId(), role);
            savedSuccessfully = true;
            closeWindow();
        } catch (IllegalArgumentException e) {
            showValidation(e.getMessage());
        } catch (Exception e) {
            showValidation("Erreur inattendue : " + e.getMessage());
        }
    }

    @FXML
    private void handlePersonnelSelected() {
        Personnel personnel = personnelCombo.getValue();
        if (personnel == null || personnel.getFonction() == null) {
            roleCombo.setValue(RoleUtilisateur.RECEPTIONNISTE);
            return;
        }
        roleCombo.setValue(utilisateurService.determinerRoleParFonction(personnel.getFonction()));
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

    private void chargerPersonnelDisponible() {
        List<Personnel> personnelsActifs = personnelService.obtenirPersonnelActif();
        Set<Long> personnelIdsAvecCompte = utilisateurService.obtenirTousUtilisateurs().stream()
                .filter(user -> user.getPersonnel() != null)
                .map(user -> user.getPersonnel().getId())
                .collect(Collectors.toSet());

        List<Personnel> disponibles = personnelsActifs.stream()
                .filter(personnel -> personnel.getId() != null && !personnelIdsAvecCompte.contains(personnel.getId()))
                .toList();

        personnelCombo.setItems(FXCollections.observableArrayList(disponibles));
        if (disponibles.isEmpty()) {
            showValidation("Aucun personnel disponible sans compte utilisateur.");
        }
    }
}
