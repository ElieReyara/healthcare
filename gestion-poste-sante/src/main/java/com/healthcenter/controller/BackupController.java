package com.healthcenter.controller;

import com.healthcenter.service.BackupService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Contrôleur pour la gestion des backups
 */
@Component
public class BackupController {
    
    @FXML
    private TableView<File> backupsTable;
    
    @FXML
    private TableColumn<File, String> colNom;
    
    @FXML
    private TableColumn<File, String> colDate;
    
    @FXML
    private TableColumn<File, String> colTaille;
    
    @FXML
    private Button btnCreerBackup;
    
    @FXML
    private Button btnRestaurer;
    
    @FXML
    private Button btnSupprimer;
    
    @FXML
    private Label statusLabel;
    
    @FXML
    private ProgressBar progressBar;
    
    @Autowired
    private BackupService backupService;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DecimalFormat SIZE_FORMATTER = new DecimalFormat("#,##0.00");
    
    @FXML
    public void initialize() {
        configurerTable();
        chargerBackups();
        progressBar.setVisible(false);
        
        // Activer boutons seulement si sélection
        btnRestaurer.setDisable(true);
        btnSupprimer.setDisable(true);
        
        backupsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean selectionne = newVal != null;
            btnRestaurer.setDisable(!selectionne);
            btnSupprimer.setDisable(!selectionne);
        });
    }
    
    /**
     * Configure les colonnes de la table
     */
    private void configurerTable() {
        colNom.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        
        colDate.setCellValueFactory(data -> {
            long timestamp = data.getValue().lastModified();
            LocalDateTime date = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp), 
                ZoneId.systemDefault()
            );
            return new SimpleStringProperty(date.format(DATE_FORMATTER));
        });
        
        colTaille.setCellValueFactory(data -> {
            long bytes = data.getValue().length();
            double kb = bytes / 1024.0;
            double mb = kb / 1024.0;
            String taille = mb >= 1 ? SIZE_FORMATTER.format(mb) + " MB" : SIZE_FORMATTER.format(kb) + " KB";
            return new SimpleStringProperty(taille);
        });
    }
    
    /**
     * Charge la liste des backups
     */
    private void chargerBackups() {
        List<File> backups = backupService.listerBackups(null);
        ObservableList<File> items = FXCollections.observableArrayList(backups);
        backupsTable.setItems(items);
        
        statusLabel.setText(backups.size() + " backup(s) disponible(s)");
    }
    
    /**
     * Crée un nouveau backup
     */
    @FXML
    private void handleCreerBackup() {
        // Demander dossier de destination
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choisir dossier de backup");
        chooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        
        File dossier = chooser.showDialog(btnCreerBackup.getScene().getWindow());
        
        if (dossier != null) {
            progressBar.setVisible(true);
            btnCreerBackup.setDisable(true);
            statusLabel.setText("Création du backup en cours...");
            
            // Exécution asynchrone
            new Thread(() -> {
                try {
                    File backupFile = backupService.creerBackup(dossier.getAbsolutePath());
                    
                    Platform.runLater(() -> {
                        progressBar.setVisible(false);
                        btnCreerBackup.setDisable(false);
                        
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Backup créé");
                        alert.setHeaderText("Backup créé avec succès");
                        alert.setContentText("Fichier: " + backupFile.getName());
                        alert.showAndWait();
                        
                        chargerBackups();
                    });
                    
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        progressBar.setVisible(false);
                        btnCreerBackup.setDisable(false);
                        
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Erreur");
                        alert.setHeaderText("Erreur lors de la création du backup");
                        alert.setContentText(e.getMessage());
                        alert.showAndWait();
                    });
                }
            }).start();
        }
    }
    
    /**
     * Restaure un backup sélectionné
     */
    @FXML
    private void handleRestaurer() {
        File backupSelectionne = backupsTable.getSelectionModel().getSelectedItem();
        
        if (backupSelectionne == null) {
            return;
        }
        
        // Confirmation
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmer la restauration");
        confirmation.setHeaderText("Restaurer le backup ?");
        confirmation.setContentText("ATTENTION: Cette opération écrasera toutes les données actuelles.\n" +
            "Backup: " + backupSelectionne.getName());
        
        Optional<ButtonType> result = confirmation.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            progressBar.setVisible(true);
            btnRestaurer.setDisable(true);
            statusLabel.setText("Restauration en cours...");
            
            new Thread(() -> {
                try {
                    backupService.restaurerBackup(backupSelectionne.getAbsolutePath());
                    
                    Platform.runLater(() -> {
                        progressBar.setVisible(false);
                        btnRestaurer.setDisable(false);
                        
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Restauration réussie");
                        alert.setHeaderText("Backup restauré avec succès");
                        alert.setContentText("Veuillez redémarrer l'application pour appliquer les changements.");
                        alert.showAndWait();
                        
                        statusLabel.setText("Backup restauré - Redémarrage requis");
                    });
                    
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        progressBar.setVisible(false);
                        btnRestaurer.setDisable(false);
                        
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Erreur");
                        alert.setHeaderText("Erreur lors de la restauration");
                        alert.setContentText(e.getMessage());
                        alert.showAndWait();
                    });
                }
            }).start();
        }
    }
    
    /**
     * Supprime un backup
     */
    @FXML
    private void handleSupprimer() {
        File backupSelectionne = backupsTable.getSelectionModel().getSelectedItem();
        
        if (backupSelectionne == null) {
            return;
        }
        
        // Confirmation
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmer la suppression");
        confirmation.setHeaderText("Supprimer ce backup ?");
        confirmation.setContentText("Backup: " + backupSelectionne.getName());
        
        Optional<ButtonType> result = confirmation.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            backupService.supprimerBackup(backupSelectionne);
            chargerBackups();
        }
    }
}
