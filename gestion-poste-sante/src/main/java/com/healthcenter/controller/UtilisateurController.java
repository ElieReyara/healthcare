package com.healthcenter.controller;

import com.healthcenter.domain.entities.Utilisateur;
import com.healthcenter.domain.enums.RoleUtilisateur;
import com.healthcenter.security.SessionManager;
import com.healthcenter.service.UtilisateurService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private ConfigurableApplicationContext springContext;

    @FXML private TableView<Utilisateur> utilisateurTable;
    @FXML private TableColumn<Utilisateur, String> colUsername;
    @FXML private TableColumn<Utilisateur, String> colNomComplet;
    @FXML private TableColumn<Utilisateur, String> colRole;
    @FXML private TableColumn<Utilisateur, String> colActif;
    @FXML private TableColumn<Utilisateur, String> colDerniereConnexion;
    @FXML private Label infoLabel;

    private final ObservableList<Utilisateur> utilisateurData = FXCollections.observableArrayList();
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        if (!SessionManager.getInstance().hasRole(RoleUtilisateur.ADMIN)) {
            infoLabel.setText("Accès réservé à l'administrateur");
            utilisateurTable.setDisable(true);
            return;
        }

        colUsername.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getUsername()));
        colNomComplet.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNomComplet()));
        colRole.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getRole() != null ? cell.getValue().getRole().getLibelle() : ""
        ));
        colActif.setCellValueFactory(cell -> new SimpleStringProperty(Boolean.TRUE.equals(cell.getValue().getActif()) ? "Actif" : "Inactif"));
        colDerniereConnexion.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getDerniereConnexion() != null ? cell.getValue().getDerniereConnexion().format(DTF) : "Jamais"
        ));

        utilisateurTable.setItems(utilisateurData);
        chargerDonnees();
    }

    @FXML
    private void handleNouveau() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/utilisateur-form.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            UtilisateurFormController controller = loader.getController();
            controller.initForCreation();

            Stage stage = new Stage();
            stage.setTitle("Nouveau profil");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

            if (controller.isSavedSuccessfully()) {
                chargerDonnees();
                showInfo("Succès", "Profil créé avec succès");
            }
        } catch (Exception e) {
            showError("Erreur", "Impossible d'ouvrir le formulaire : " + e.getMessage());
        }
    }

    @FXML
    private void handleActiver() {
        Utilisateur selected = utilisateurTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("Information", "Sélectionnez un profil.");
            return;
        }
        try {
            utilisateurService.activerUtilisateur(selected.getId());
            chargerDonnees();
        } catch (Exception e) {
            showError("Erreur", e.getMessage());
        }
    }

    @FXML
    private void handleDesactiver() {
        Utilisateur selected = utilisateurTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("Information", "Sélectionnez un profil.");
            return;
        }

        Utilisateur current = SessionManager.getInstance().getUtilisateur();
        if (current != null && current.getId() != null && current.getId().equals(selected.getId())) {
            showError("Action refusée", "Vous ne pouvez pas désactiver votre propre compte.");
            return;
        }

        try {
            utilisateurService.desactiverUtilisateur(selected.getId());
            chargerDonnees();
        } catch (Exception e) {
            showError("Erreur", e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        chargerDonnees();
    }

    private void chargerDonnees() {
        List<Utilisateur> users = utilisateurService.obtenirTousUtilisateurs();
        utilisateurData.setAll(users);
        infoLabel.setText("Profils: " + users.size());
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
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
}
