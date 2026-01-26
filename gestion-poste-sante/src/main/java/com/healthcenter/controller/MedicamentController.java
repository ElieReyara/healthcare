package com.healthcenter.controller;

import com.healthcenter.domain.entities.Medicament;
import com.healthcenter.domain.entities.MouvementStock;
import com.healthcenter.domain.enums.FormeMedicament;
import com.healthcenter.domain.enums.TypeMouvement;
import com.healthcenter.service.MedicamentService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * CONTROLLER JAVAFX = Gère les événements UI pour Médicaments.
 */
@Component
public class MedicamentController {
    
    @Autowired
    private MedicamentService medicamentService;
    
    @Autowired
    private ConfigurableApplicationContext springContext;
    
    // ========== COMPOSANTS FXML ==========
    
    @FXML private TextField searchField;
    @FXML private ComboBox<FormeMedicament> formeFilter;
    @FXML private TableView<Medicament> medicamentTable;
    @FXML private TableColumn<Medicament, Long> colId;
    @FXML private TableColumn<Medicament, String> colNom;
    @FXML private TableColumn<Medicament, String> colDosage;
    @FXML private TableColumn<Medicament, FormeMedicament> colForme;
    @FXML private TableColumn<Medicament, BigDecimal> colPrix;
    @FXML private TableColumn<Medicament, Integer> colStock;
    @FXML private TableColumn<Medicament, Integer> colSeuilAlerte;
    @FXML private TableColumn<Medicament, String> colStatut;
    
    private ObservableList<Medicament> medicamentData = FXCollections.observableArrayList();
    
    
    /**
     * Initialisation après chargement FXML.
     */
    @FXML
    public void initialize() {
        // Configuration colonnes
        colId.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleLongProperty(cellData.getValue().getId()).asObject());
        
        colNom.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNom()));
        
        colDosage.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDosage()));
        
        colForme.setCellValueFactory(cellData -> 
            new SimpleObjectProperty<>(cellData.getValue().getForme()));
        
        colPrix.setCellValueFactory(cellData -> 
            new SimpleObjectProperty<>(cellData.getValue().getPrix()));
        
        colStock.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getStockActuel()).asObject());
        
        colSeuilAlerte.setCellValueFactory(cellData -> {
            Integer seuil = cellData.getValue().getSeuilAlerte();
            return new javafx.beans.property.SimpleIntegerProperty(seuil != null ? seuil : 0).asObject();
        });
        
        // Colonne Statut avec alerte visuelle
        colStatut.setCellValueFactory(cellData -> {
            Medicament medicament = cellData.getValue();
            if (medicament.isStockFaible()) {
                return new SimpleStringProperty("⚠️ ALERTE");
            } else {
                return new SimpleStringProperty("✅ OK");
            }
        });
        
        // Style cellule Statut (rouge si alerte)
        colStatut.setCellFactory(column -> new TableCell<Medicament, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.contains("ALERTE")) {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    }
                }
            }
        });
        
        medicamentTable.setItems(medicamentData);
        
        setupFormeFilter();
        chargerDonnees();
    }
    
    
    // ========== SETUP COMBOBOX ==========
    
    private void setupFormeFilter() {
        ObservableList<FormeMedicament> formes = FXCollections.observableArrayList(FormeMedicament.values());
        
        // Ajouter option "Toutes"
        formeFilter.setItems(formes);
        formeFilter.setPromptText("Toutes les formes");
        
        formeFilter.setConverter(new StringConverter<FormeMedicament>() {
            @Override
            public String toString(FormeMedicament forme) {
                return forme != null ? forme.getLibelle() : "Toutes";
            }
            
            @Override
            public FormeMedicament fromString(String string) {
                return null;
            }
        });
    }
    
    
    // ========== HANDLERS ==========
    
    @FXML
    private void handleNouveau() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/medicament-form.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();
            
            MedicamentFormController formController = loader.getController();
            formController.initForCreation();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Nouveau Médicament");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(medicamentTable.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);
            dialogStage.showAndWait();
            
            if (formController.isSavedSuccessfully()) {
                chargerDonnees();
                showInfo("Succès", "Médicament créé avec succès");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur", "Impossible d'ouvrir le formulaire : " + e.getMessage());
        }
    }
    
    @FXML
    private void handleModifier() {
        Medicament selected = medicamentTable.getSelectionModel().getSelectedItem();
        
        if (selected == null) {
            showWarning("Sélection requise", "Veuillez sélectionner un médicament à modifier");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/medicament-form.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();
            
            MedicamentFormController formController = loader.getController();
            formController.initForEdit(selected);
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Modifier Médicament");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(medicamentTable.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);
            dialogStage.showAndWait();
            
            if (formController.isSavedSuccessfully()) {
                chargerDonnees();
                showInfo("Succès", "Médicament modifié avec succès");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur", "Impossible d'ouvrir le formulaire : " + e.getMessage());
        }
    }
    
    @FXML
    private void handleSupprimer() {
        Medicament selected = medicamentTable.getSelectionModel().getSelectedItem();
        
        if (selected == null) {
            showWarning("Sélection requise", "Veuillez sélectionner un médicament à supprimer");
            return;
        }
        
        Optional<ButtonType> result = showConfirmation(
            "Confirmer la suppression",
            "Supprimer le médicament \"" + selected.getNom() + "\" ?\n\n" +
            "Cette action est irréversible."
        );
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                medicamentService.supprimerMedicament(selected.getId());
                chargerDonnees();
                showInfo("Succès", "Médicament supprimé avec succès");
            } catch (IllegalArgumentException e) {
                showError("Erreur", e.getMessage());
            } catch (Exception e) {
                showError("Erreur", "Impossible de supprimer : " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleAjusterStock() {
        Medicament selected = medicamentTable.getSelectionModel().getSelectedItem();
        
        if (selected == null) {
            showWarning("Sélection requise", "Veuillez sélectionner un médicament");
            return;
        }
        
        // Dialog ajustement stock
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Ajuster Stock - " + selected.getNom());
        dialog.setHeaderText("Stock actuel : " + selected.getStockActuel());
        
        ButtonType btnValider = new ButtonType("Valider", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnValider, ButtonType.CANCEL);
        
        // Formulaire
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20));
        
        ComboBox<TypeMouvement> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll(TypeMouvement.ENTREE, TypeMouvement.SORTIE);
        typeCombo.setConverter(new StringConverter<TypeMouvement>() {
            @Override
            public String toString(TypeMouvement type) {
                return type != null ? type.getLibelle() : "";
            }
            @Override
            public TypeMouvement fromString(String string) {
                return null;
            }
        });
        
        Spinner<Integer> quantiteSpinner = new Spinner<>(1, 9999, 1);
        quantiteSpinner.setEditable(true);
        
        TextField motifField = new TextField();
        motifField.setPromptText("Ex: Réapprovisionnement");
        
        grid.add(new Label("Type :"), 0, 0);
        grid.add(typeCombo, 1, 0);
        grid.add(new Label("Quantité :"), 0, 1);
        grid.add(quantiteSpinner, 1, 1);
        grid.add(new Label("Motif :"), 0, 2);
        grid.add(motifField, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        
        Optional<ButtonType> result = dialog.showAndWait();
        
        if (result.isPresent() && result.get() == btnValider) {
            TypeMouvement type = typeCombo.getValue();
            Integer quantite = quantiteSpinner.getValue();
            String motif = motifField.getText();
            
            if (type == null) {
                showWarning("Validation", "Veuillez sélectionner un type");
                return;
            }
            
            try {
                medicamentService.ajusterStock(selected.getId(), quantite, type, motif);
                chargerDonnees();
                showInfo("Succès", "Stock ajusté avec succès. Nouveau stock : " + 
                    medicamentService.obtenirMedicamentParId(selected.getId()).get().getStockActuel());
            } catch (IllegalArgumentException e) {
                showError("Erreur", e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleVoirHistorique() {
        Medicament selected = medicamentTable.getSelectionModel().getSelectedItem();
        
        if (selected == null) {
            showWarning("Sélection requise", "Veuillez sélectionner un médicament");
            return;
        }
        
        List<MouvementStock> mouvements = medicamentService.obtenirHistoriqueStock(selected.getId());
        
        if (mouvements.isEmpty()) {
            showInfo("Historique vide", "Aucun mouvement de stock pour ce médicament");
            return;
        }
        
        // Créer dialog avec TableView
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Historique Stock - " + selected.getNom());
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        
        TableView<MouvementStock> histoTable = new TableView<>();
        histoTable.setPrefWidth(700);
        histoTable.setPrefHeight(400);
        
        TableColumn<MouvementStock, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getType().getLibelle()));
        
        TableColumn<MouvementStock, Integer> colQte = new TableColumn<>("Quantité");
        colQte.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getQuantite()).asObject());
        
        TableColumn<MouvementStock, String> colMotif = new TableColumn<>("Motif");
        colMotif.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getMotif()));
        
        TableColumn<MouvementStock, Integer> colStockAvant = new TableColumn<>("Stock Avant");
        colStockAvant.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getStockAvant()).asObject());
        
        TableColumn<MouvementStock, Integer> colStockApres = new TableColumn<>("Stock Après");
        colStockApres.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getStockApres()).asObject());
        
        histoTable.getColumns().addAll(colType, colQte, colMotif, colStockAvant, colStockApres);
        histoTable.setItems(FXCollections.observableArrayList(mouvements));
        
        dialog.getDialogPane().setContent(histoTable);
        dialog.showAndWait();
    }
    
    @FXML
    private void handleFilterByForme() {
        FormeMedicament selectedForme = formeFilter.getValue();
        
        if (selectedForme == null) {
            chargerDonnees();
            return;
        }
        
        List<Medicament> medicaments = medicamentService.obtenirMedicamentsParForme(selectedForme);
        medicamentData.clear();
        medicamentData.addAll(medicaments);
    }
    
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText();
        
        if (searchText == null || searchText.trim().isEmpty()) {
            chargerDonnees();
            return;
        }
        
        List<Medicament> medicaments = medicamentService.rechercherMedicamentsParNom(searchText);
        medicamentData.clear();
        medicamentData.addAll(medicaments);
    }
    
    @FXML
    private void handleRefresh() {
        formeFilter.setValue(null);
        searchField.clear();
        chargerDonnees();
        showInfo("Actualisation", "Données rechargées");
    }
    
    
    // ========== UTILITAIRES ==========
    
    private void chargerDonnees() {
        List<Medicament> medicaments = medicamentService.obtenirTousMedicaments();
        medicamentData.clear();
        medicamentData.addAll(medicaments);
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
