package com.healthcenter.controller;

import com.healthcenter.domain.enums.FormatRapport;
import com.healthcenter.domain.enums.PeriodeStatistique;
import com.healthcenter.domain.enums.TypeRapport;
import com.healthcenter.service.RapportService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.awt.Desktop;
import java.io.File;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;

/**
 * Contrôleur du générateur de rapports.
 * Permet de sélectionner le type, la période et le format du rapport.
 */
@Controller
public class RapportController {
    
    @Autowired
    private RapportService rapportService;
    
    @FXML private ComboBox<TypeRapport> typeRapportCombo;
    @FXML private ComboBox<PeriodeStatistique> periodeCombo;
    @FXML private DatePicker dateDebutPicker;
    @FXML private DatePicker dateFinPicker;
    @FXML private ComboBox<FormatRapport> formatCombo;
    @FXML private TextArea apercuTextArea;
    @FXML private ProgressBar progressBar;
    @FXML private Label statusLabel;
    @FXML private Button btnGenerer;
    @FXML private Button btnAnnuler;
    
    private boolean generationEnCours = false;
    
    /**
     * Initialisation du contrôleur.
     */
    @FXML
    public void initialize() {
        // Initialiser les ComboBox
        typeRapportCombo.setItems(FXCollections.observableArrayList(TypeRapport.values()));
        typeRapportCombo.getSelectionModel().selectFirst();
        
        periodeCombo.setItems(FXCollections.observableArrayList(PeriodeStatistique.values()));
        periodeCombo.getSelectionModel().select(PeriodeStatistique.MOIS);
        
        formatCombo.setItems(FXCollections.observableArrayList(FormatRapport.values()));
        formatCombo.getSelectionModel().selectFirst();
        
        // Masquer les DatePickers initialement
        dateDebutPicker.setVisible(false);
        dateFinPicker.setVisible(false);
        dateDebutPicker.setManaged(false);
        dateFinPicker.setManaged(false);
        
        // Masquer la barre de progression
        progressBar.setVisible(false);
        progressBar.setManaged(false);
        
        // Configurer les listeners
        periodeCombo.setOnAction(e -> handlePeriodeSelected());
        typeRapportCombo.setOnAction(e -> handleTypeRapportSelected());
    }
    
    /**
     * Gère la sélection du type de rapport.
     */
    @FXML
    private void handleTypeRapportSelected() {
        TypeRapport type = typeRapportCombo.getValue();
        if (type != null) {
            statusLabel.setText("Type sélectionné: " + type.getLibelle());
        }
    }
    
    /**
     * Gère la sélection de la période.
     */
    @FXML
    private void handlePeriodeSelected() {
        PeriodeStatistique periode = periodeCombo.getValue();
        
        if (periode == PeriodeStatistique.PERSONNALISE) {
            // Afficher les DatePickers
            dateDebutPicker.setVisible(true);
            dateFinPicker.setVisible(true);
            dateDebutPicker.setManaged(true);
            dateFinPicker.setManaged(true);
            
            // Initialiser avec les dates par défaut
            if (dateDebutPicker.getValue() == null) {
                dateDebutPicker.setValue(LocalDate.now().minusMonths(1));
            }
            if (dateFinPicker.getValue() == null) {
                dateFinPicker.setValue(LocalDate.now());
            }
        } else {
            // Masquer les DatePickers
            dateDebutPicker.setVisible(false);
            dateFinPicker.setVisible(false);
            dateDebutPicker.setManaged(false);
            dateFinPicker.setManaged(false);
        }
    }
    
    /**
     * Gère le clic sur Afficher aperçu.
     */
    @FXML
    private void handleAfficherApercu() {
        try {
            LocalDate[] dates = calculerDatesPeriode(periodeCombo.getValue());
            LocalDate debut = dates[0];
            LocalDate fin = dates[1];
            
            TypeRapport type = typeRapportCombo.getValue();
            
            StringBuilder apercu = new StringBuilder();
            apercu.append("APERÇU DU RAPPORT\n");
            apercu.append("=================\n\n");
            apercu.append("Type: ").append(type.getLibelle()).append("\n");
            apercu.append("Période: ").append(debut).append(" au ").append(fin).append("\n");
            apercu.append("Format: ").append(formatCombo.getValue().getLibelle()).append("\n\n");
            apercu.append("Le rapport contiendra:\n");
            
            switch (type) {
                case ACTIVITE_GLOBALE, MENSUEL, ANNUEL -> {
                    apercu.append("- Statistiques patients\n");
                    apercu.append("- Statistiques consultations\n");
                    apercu.append("- Statistiques vaccinations\n");
                    apercu.append("- État des stocks médicaments\n");
                    apercu.append("- Activité du personnel\n");
                }
                case CONSULTATIONS -> {
                    apercu.append("- Nombre de consultations\n");
                    apercu.append("- Évolution temporelle\n");
                    apercu.append("- Maladies fréquentes\n");
                    apercu.append("- Répartition par personnel\n");
                }
                case VACCINATIONS -> {
                    apercu.append("- Nombre de vaccinations\n");
                    apercu.append("- Couverture vaccinale\n");
                    apercu.append("- Évolution vaccinations\n");
                }
                case MEDICAMENTS_STOCK -> {
                    apercu.append("- Inventaire complet\n");
                    apercu.append("- Alertes rupture de stock\n");
                    apercu.append("- Valeur totale du stock\n");
                }
                case PERSONNEL_ACTIVITE -> {
                    apercu.append("- Personnel actif\n");
                    apercu.append("- Répartition par fonction\n");
                    apercu.append("- Top personnel actif\n");
                }
                case MALADIES_FREQUENTES -> {
                    apercu.append("- Top 30 maladies fréquentes\n");
                    apercu.append("- Statistiques par maladie\n");
                }
            }
            
            apercuTextArea.setText(apercu.toString());
            
        } catch (Exception e) {
            afficherErreur("Erreur lors de la génération de l'aperçu: " + e.getMessage());
        }
    }
    
    /**
     * Gère le clic sur Générer.
     */
    @FXML
    private void handleGenerer() {
        if (generationEnCours) {
            return;
        }
        
        try {
            // Validation
            if (typeRapportCombo.getValue() == null) {
                afficherErreur("Veuillez sélectionner un type de rapport");
                return;
            }
            
            if (formatCombo.getValue() == null) {
                afficherErreur("Veuillez sélectionner un format");
                return;
            }
            
            LocalDate[] dates = calculerDatesPeriode(periodeCombo.getValue());
            LocalDate debut = dates[0];
            LocalDate fin = dates[1];
            
            if (debut.isAfter(fin)) {
                afficherErreur("La date de début doit être antérieure à la date de fin");
                return;
            }
            
            // FileChooser pour choisir l'emplacement
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le rapport");
            
            FormatRapport format = formatCombo.getValue();
            String extension = format.getExtension();
            fileChooser.setInitialFileName("rapport_" + typeRapportCombo.getValue().name().toLowerCase() + "." + extension);
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(format.getLibelle(), "*." + extension)
            );
            
            File fichier = fileChooser.showSaveDialog(btnGenerer.getScene().getWindow());
            
            if (fichier != null) {
                genererRapportAsync(typeRapportCombo.getValue(), debut, fin, fichier.getAbsolutePath(), format);
            }
            
        } catch (Exception e) {
            afficherErreur("Erreur lors de la génération du rapport: " + e.getMessage());
        }
    }
    
    /**
     * Génère le rapport de manière asynchrone.
     */
    private void genererRapportAsync(TypeRapport type, LocalDate debut, LocalDate fin, 
                                    String cheminSortie, FormatRapport format) {
        generationEnCours = true;
        progressBar.setVisible(true);
        progressBar.setManaged(true);
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        statusLabel.setText("Génération en cours...");
        btnGenerer.setDisable(true);
        
        new Thread(() -> {
            try {
                File fichierGenere;
                
                if (format == FormatRapport.PDF) {
                    fichierGenere = rapportService.genererRapportPDF(type, debut, fin, cheminSortie);
                } else {
                    fichierGenere = rapportService.genererRapportExcel(type, debut, fin, cheminSortie);
                }
                
                Platform.runLater(() -> {
                    generationEnCours = false;
                    progressBar.setVisible(false);
                    progressBar.setManaged(false);
                    btnGenerer.setDisable(false);
                    statusLabel.setText("Rapport généré avec succès!");
                    
                    // Demander si on veut ouvrir le fichier
                    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmation.setTitle("Rapport généré");
                    confirmation.setHeaderText("Le rapport a été généré avec succès");
                    confirmation.setContentText("Voulez-vous ouvrir le fichier?");
                    
                    confirmation.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            ouvrirFichier(fichierGenere);
                        }
                    });
                });
                
            } catch (Exception e) {
                Platform.runLater(() -> {
                    generationEnCours = false;
                    progressBar.setVisible(false);
                    progressBar.setManaged(false);
                    btnGenerer.setDisable(false);
                    statusLabel.setText("Erreur lors de la génération");
                    afficherErreur("Erreur: " + e.getMessage());
                });
            }
        }).start();
    }
    
    /**
     * Ouvre le fichier avec l'application par défaut.
     */
    private void ouvrirFichier(File fichier) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(fichier);
            }
        } catch (Exception e) {
            afficherErreur("Impossible d'ouvrir le fichier: " + e.getMessage());
        }
    }
    
    /**
     * Gère le clic sur Annuler.
     */
    @FXML
    private void handleAnnuler() {
        Stage stage = (Stage) btnAnnuler.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Calcule les dates de début et de fin selon la période sélectionnée.
     */
    private LocalDate[] calculerDatesPeriode(PeriodeStatistique periode) {
        LocalDate reference = LocalDate.now();
        
        if (periode == PeriodeStatistique.PERSONNALISE) {
            LocalDate debut = dateDebutPicker.getValue();
            LocalDate fin = dateFinPicker.getValue();
            
            if (debut == null) debut = reference.minusMonths(1);
            if (fin == null) fin = reference;
            
            return new LocalDate[]{debut, fin};
        } else {
            return new LocalDate[]{
                    periode.getDateDebut(reference),
                    periode.getDateFin(reference)
            };
        }
    }
    
    /**
     * Affiche un message d'erreur.
     */
    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
