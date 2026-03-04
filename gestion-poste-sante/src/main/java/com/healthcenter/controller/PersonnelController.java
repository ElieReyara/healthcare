package com.healthcenter.controller;

import com.healthcenter.domain.entities.Personnel;
import com.healthcenter.domain.enums.RoleUtilisateur;
import com.healthcenter.domain.enums.FonctionPersonnel;
import com.healthcenter.security.SessionManager;
import com.healthcenter.service.PersonnelService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * CONTROLLER JavaFX - Liste Personnel
 * 
 * Gère l'affichage et les actions sur la liste du personnel :
 * - Tableau personnel avec filtres (fonction, recherche, actif/inactif)
 * - Actions CRUD (Nouveau, Modifier, Supprimer)
 * - Activation/Désactivation personnel
 * - Navigation vers fiche détails
 * 
 * @author Health Center Team
 */
@Component
public class PersonnelController {
    
    @Autowired
    private PersonnelService personnelService;
    
    @Autowired
    private ConfigurableApplicationContext springContext;
    
    // ========== COMPOSANTS FXML ==========
    
    @FXML private TableView<Personnel> personnelTable;
    @FXML private TableColumn<Personnel, Long> colId;
    @FXML private TableColumn<Personnel, String> colMatricule;
    @FXML private TableColumn<Personnel, String> colNom;
    @FXML private TableColumn<Personnel, String> colPrenom;
    @FXML private TableColumn<Personnel, String> colFonction;
    @FXML private TableColumn<Personnel, String> colSpecialisation;
    @FXML private TableColumn<Personnel, String> colTelephone;
    @FXML private TableColumn<Personnel, String> colEmail;
    @FXML private TableColumn<Personnel, String> colStatut;
    
    @FXML private TextField searchField;
    @FXML private ComboBox<FonctionPersonnel> fonctionFilter;
    @FXML private CheckBox actifSeulementCheckbox;
    
    private ObservableList<Personnel> personnelData = FXCollections.observableArrayList();
    
    
    /**
     * Initialisation après chargement FXML.
     */
    @FXML
    public void initialize() {
        try {
            configurerColonnes();
            appliquerVisibiliteColonnesParRole();
            configurerFiltres();
            chargerDonnees();
        } catch (Exception e) {
            afficherErreur("Erreur initialisation PersonnelController", e);
        }
    }
    
    /**
     * Configure les colonnes du TableView.
     */
    private void configurerColonnes() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMatricule.setCellValueFactory(new PropertyValueFactory<>("numeroMatricule"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        
        // Colonne Fonction avec libellé
        colFonction.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFonctionLibelle())
        );
        
        colSpecialisation.setCellValueFactory(new PropertyValueFactory<>("specialisation"));
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        // Colonne Statut avec couleurs
        colStatut.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getActif() ? "✅ Actif" : "❌ Inactif"
            )
        );
        
        colStatut.setCellFactory(column -> new TableCell<Personnel, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.contains("Actif")) {
                        setTextFill(Color.GREEN);
                        setStyle("-fx-font-weight: bold;");
                    } else {
                        setTextFill(Color.GRAY);
                    }
                }
            }
        });
        
        personnelTable.setItems(personnelData);
    }

    private void appliquerVisibiliteColonnesParRole() {
        boolean isAdmin = SessionManager.getInstance().hasRole(RoleUtilisateur.ADMIN);

        // Le matricule reste visible pour faciliter l'identification
        colMatricule.setVisible(true);

        // Masquer l'ID pour les profils non-admin
        colId.setVisible(isAdmin);
    }
    
    /**
     * Configure les filtres (ComboBox fonction, CheckBox actif).
     */
    private void configurerFiltres() {
        // Remplir ComboBox fonctions
        fonctionFilter.getItems().add(null); // Option "Tous"
        fonctionFilter.getItems().addAll(FonctionPersonnel.values());
        fonctionFilter.setPromptText("Toutes les fonctions");
        
        // Converter pour afficher libellés
        fonctionFilter.setConverter(new javafx.util.StringConverter<FonctionPersonnel>() {
            @Override
            public String toString(FonctionPersonnel fonction) {
                return fonction == null ? "Toutes les fonctions" : fonction.getLibelle();
            }
            
            @Override
            public FonctionPersonnel fromString(String string) {
                return null;
            }
        });
        
        // Listener changement fonction
        fonctionFilter.setOnAction(event -> handleFilterByFonction());
        
        // CheckBox actifs coché par défaut
        actifSeulementCheckbox.setSelected(true);
        actifSeulementCheckbox.setOnAction(event -> handleToggleActifs());
    }
    
    /**
     * Charge les données depuis le service.
     */
    private void chargerDonnees() {
        try {
            List<Personnel> personnel;
            
            if (actifSeulementCheckbox.isSelected()) {
                personnel = personnelService.obtenirPersonnelActif();
            } else {
                personnel = personnelService.obtenirToutPersonnel();
            }
            
            personnelData.setAll(personnel);
            System.out.println("✅ " + personnel.size() + " personnel(s) chargé(s)");
        } catch (Exception e) {
            afficherErreur("Erreur chargement personnel", e);
        }
    }
    
    
    // ========== HANDLERS ACTIONS ==========
    
    /**
     * Ouvre formulaire création nouveau personnel.
     */
    @FXML
    private void handleNouveau() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/personnel-form.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();
            
            PersonnelFormController controller = loader.getController();
            controller.initForCreation();
            
            Stage stage = new Stage();
            stage.setTitle("Nouveau Personnel");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            if (controller.isSavedSuccessfully()) {
                chargerDonnees();
            }
        } catch (IOException e) {
            afficherErreur("Erreur ouverture formulaire", e);
        }
    }
    
    /**
     * Ouvre formulaire modification personnel sélectionné.
     */
    @FXML
    private void handleModifier() {
        Personnel selected = personnelTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherInfo("Aucune sélection", "Veuillez sélectionner un personnel à modifier.");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/personnel-form.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();
            
            PersonnelFormController controller = loader.getController();
            controller.initForEdit(selected);
            
            Stage stage = new Stage();
            stage.setTitle("Modifier Personnel");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            if (controller.isSavedSuccessfully()) {
                chargerDonnees();
            }
        } catch (IOException e) {
            afficherErreur("Erreur ouverture formulaire", e);
        }
    }
    
    /**
     * Supprime le personnel sélectionné (après confirmation).
     */
    @FXML
    private void handleSupprimer() {
        Personnel selected = personnelTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherInfo("Aucune sélection", "Veuillez sélectionner un personnel à supprimer.");
            return;
        }
        
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation Suppression");
        confirmation.setHeaderText("Supprimer " + selected.getNomComplet() + " ?");
        confirmation.setContentText(
            "ATTENTION : La suppression est définitive.\n" +
            "Si ce personnel a des consultations ou vaccinations, utilisez plutôt la désactivation."
        );
        
        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                personnelService.supprimerPersonnel(selected.getId());
                chargerDonnees();
                afficherSucces("Personnel supprimé avec succès");
            } catch (IllegalArgumentException e) {
                afficherErreur("Impossible de supprimer", e);
            }
        }
    }
    
    /**
     * Ouvre fiche détails personnel sélectionné.
     */
    @FXML
    private void handleVoirDetails() {
        Personnel selected = personnelTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherInfo("Aucune sélection", "Veuillez sélectionner un personnel.");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/personnel-details.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();
            
            PersonnelDetailsController controller = loader.getController();
            controller.initWithPersonnel(selected);
            
            Stage stage = new Stage();
            stage.setTitle("Fiche Personnel - " + selected.getNomComplet());
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            afficherErreur("Erreur ouverture fiche détails", e);
        }
    }
    
    /**
     * Désactive le personnel sélectionné (soft delete).
     */
    @FXML
    private void handleDesactiver() {
        Personnel selected = personnelTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherInfo("Aucune sélection", "Veuillez sélectionner un personnel.");
            return;
        }
        
        if (!selected.getActif()) {
            afficherInfo("Déjà inactif", "Ce personnel est déjà désactivé.");
            return;
        }
        
        try {
            personnelService.desactiverPersonnel(selected.getId());
            chargerDonnees();
            afficherSucces("Personnel désactivé avec succès");
        } catch (Exception e) {
            afficherErreur("Erreur désactivation", e);
        }
    }
    
    /**
     * Active le personnel sélectionné.
     */
    @FXML
    private void handleActiver() {
        Personnel selected = personnelTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherInfo("Aucune sélection", "Veuillez sélectionner un personnel.");
            return;
        }
        
        if (selected.getActif()) {
            afficherInfo("Déjà actif", "Ce personnel est déjà actif.");
            return;
        }
        
        try {
            personnelService.activerPersonnel(selected.getId());
            chargerDonnees();
            afficherSucces("Personnel activé avec succès");
        } catch (Exception e) {
            afficherErreur("Erreur activation", e);
        }
    }
    
    /**
     * Filtre par fonction sélectionnée.
     */
    @FXML
    private void handleFilterByFonction() {
        FonctionPersonnel fonction = fonctionFilter.getValue();
        
        if (fonction == null) {
            chargerDonnees(); // Tous
        } else {
            List<Personnel> personnel = personnelService.obtenirPersonnelParFonction(fonction);
            
            // Filtrer selon checkbox actif
            if (actifSeulementCheckbox.isSelected()) {
                personnel = personnel.stream().filter(Personnel::getActif).toList();
            }
            
            personnelData.setAll(personnel);
        }
    }
    
    /**
     * Recherche par nom/prénom.
     */
    @FXML
    private void handleSearch() {
        String terme = searchField.getText();
        List<Personnel> personnel = personnelService.rechercherPersonnelParNom(terme);
        
        // Filtrer selon checkbox actif
        if (actifSeulementCheckbox.isSelected()) {
            personnel = personnel.stream().filter(Personnel::getActif).toList();
        }
        
        personnelData.setAll(personnel);
    }
    
    /**
     * Toggle affichage actifs seulement / tous.
     */
    @FXML
    private void handleToggleActifs() {
        chargerDonnees();
    }
    
    /**
     * Actualise les données.
     */
    @FXML
    private void handleRefresh() {
        chargerDonnees();
        fonctionFilter.setValue(null);
        searchField.clear();
    }
    
    
    // ========== HELPERS UI ==========
    
    private void afficherErreur(String titre, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(titre);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
        e.printStackTrace();
    }
    
    private void afficherInfo(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void afficherSucces(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
