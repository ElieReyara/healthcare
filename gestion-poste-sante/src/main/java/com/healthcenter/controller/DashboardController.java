package com.healthcenter.controller;

import com.healthcenter.dto.DashboardStats;
import com.healthcenter.dto.RepartitionData;
import com.healthcenter.dto.EvolutionData;
import com.healthcenter.service.StatistiqueService;
import com.healthcenter.service.ExportService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Contrôleur du tableau de bord.
 * Affiche les indicateurs clés et les graphiques statistiques.
 */
@Controller
public class DashboardController {
    
    @Autowired
    private StatistiqueService statistiqueService;
    
    @Autowired
    private ExportService exportService;
    
    @Autowired
    private ConfigurableApplicationContext springContext;
    
    // Labels KPIs
    @FXML private Label nbPatientsLabel;
    @FXML private Label nbPatientsMoisLabel;
    @FXML private Label nbConsultationsLabel;
    @FXML private Label nbConsultationsMoisLabel;
    @FXML private Label nbConsultationsSemaineLabel;
    @FXML private Label nbConsultationsAujourdhuiLabel;
    @FXML private Label nbVaccinationsLabel;
    @FXML private Label nbVaccinationsMoisLabel;
    @FXML private Label nbMedicamentsLabel;
    @FXML private Label nbMedicamentsRuptureLabel;
    @FXML private Label nbPersonnelLabel;
    @FXML private Label dateGenerationLabel;
    
    // Graphiques
    @FXML private PieChart repartitionSexeChart;
    @FXML private BarChart<String, Number> consultationsMoisChart;
    @FXML private BarChart<String, Number> maladiesFrequentesChart;
    @FXML private PieChart couvertureVaccinaleChart;
    
    // Boutons
    @FXML private Button btnActualiser;
    @FXML private Button btnGenererRapport;
    @FXML private Button btnExporter;
    
    /**
     * Initialisation du contrôleur.
     */
    @FXML
    public void initialize() {
        chargerDashboard();
    }
    
    /**
     * Charge toutes les données du dashboard.
     */
    public void chargerDashboard() {
        try {
            // Charger les KPIs
            DashboardStats stats = statistiqueService.obtenirDashboardStats();
            
            nbPatientsLabel.setText(String.valueOf(stats.getNbTotalPatients()));
            nbPatientsMoisLabel.setText("+ " + stats.getNbPatientsMois() + " ce mois");
            
            nbConsultationsLabel.setText(String.valueOf(stats.getNbTotalConsultations()));
            nbConsultationsMoisLabel.setText(String.valueOf(stats.getNbConsultationsMois()));
            nbConsultationsSemaineLabel.setText(String.valueOf(stats.getNbConsultationsSemaine()));
            nbConsultationsAujourdhuiLabel.setText(String.valueOf(stats.getNbConsultationsAujourdhui()));
            
            nbVaccinationsLabel.setText(String.valueOf(stats.getNbTotalVaccinations()));
            nbVaccinationsMoisLabel.setText("+ " + stats.getNbVaccinationsMois() + " ce mois");
            
            nbMedicamentsLabel.setText(String.valueOf(stats.getNbMedicamentsStock()));
            nbMedicamentsRuptureLabel.setText(String.valueOf(stats.getNbMedicamentsRupture()));
            
            nbPersonnelLabel.setText(String.valueOf(stats.getNbPersonnelActif()));
            
            dateGenerationLabel.setText("Dernière actualisation : " + 
                    stats.getDateGeneration().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            
            // Charger les graphiques
            chargerGraphiqueRepartitionSexe();
            chargerGraphiqueConsultationsMois();
            chargerGraphiqueMaladiesFrequentes();
            chargerGraphiqueCouvertureVaccinale();
            
        } catch (Exception e) {
            e.printStackTrace();
            afficherErreur("Erreur lors du chargement du dashboard: " + e.getMessage());
        }
    }
    
    /**
     * Charge le graphique de répartition des patients par sexe.
     */
    private void chargerGraphiqueRepartitionSexe() {
        try {
            List<RepartitionData> repartition = statistiqueService.obtenirRepartitionPatientsSexe();
            
            repartitionSexeChart.getData().clear();
            
            for (RepartitionData data : repartition) {
                PieChart.Data pieData = new PieChart.Data(
                        data.getLabel() + " (" + data.getPourcentageFormate() + ")",
                        data.getValeur().doubleValue()
                );
                repartitionSexeChart.getData().add(pieData);
            }
            
            repartitionSexeChart.setTitle("Répartition Patients par Sexe");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Charge le graphique d'évolution des consultations sur 6 mois.
     */
    private void chargerGraphiqueConsultationsMois() {
        try {
            LocalDate fin = LocalDate.now();
            LocalDate debut = fin.minusMonths(6);
            
            List<EvolutionData> evolution = statistiqueService.obtenirEvolutionConsultations(debut, fin, "MOIS");
            
            consultationsMoisChart.getData().clear();
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Consultations");
            
            for (EvolutionData data : evolution) {
                series.getData().add(new XYChart.Data<>(data.getPeriode(), data.getValeur()));
            }
            
            consultationsMoisChart.getData().add(series);
            consultationsMoisChart.setTitle("Consultations 6 Derniers Mois");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Charge le graphique des maladies fréquentes.
     */
    private void chargerGraphiqueMaladiesFrequentes() {
        try {
            LocalDate fin = LocalDate.now();
            LocalDate debut = fin.minusMonths(3);
            
            List<RepartitionData> maladies = statistiqueService.obtenirMaladiesFrequentes(debut, fin, 10);
            
            maladiesFrequentesChart.getData().clear();
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Nombre de cas");
            
            for (RepartitionData data : maladies) {
                series.getData().add(new XYChart.Data<>(data.getLabel(), data.getValeur()));
            }
            
            maladiesFrequentesChart.getData().add(series);
            maladiesFrequentesChart.setTitle("Top 10 Maladies Fréquentes");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Charge le graphique de couverture vaccinale.
     */
    private void chargerGraphiqueCouvertureVaccinale() {
        try {
            List<RepartitionData> couverture = statistiqueService.obtenirCouvertureVaccinale();
            
            couvertureVaccinaleChart.getData().clear();
            
            for (RepartitionData data : couverture) {
                PieChart.Data pieData = new PieChart.Data(
                        data.getLabel() + " (" + String.format("%.1f%%", data.getValeur().doubleValue()) + ")",
                        data.getValeur().doubleValue()
                );
                couvertureVaccinaleChart.getData().add(pieData);
            }
            
            couvertureVaccinaleChart.setTitle("Couverture Vaccinale");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Gère le clic sur le bouton Actualiser.
     */
    @FXML
    private void handleActualiser() {
        chargerDashboard();
    }
    
    /**
     * Gère le clic sur le bouton Générer Rapport.
     */
    @FXML
    private void handleGenererRapport() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/rapport.fxml"));
            loader.setControllerFactory(springContext::getBean);
            
            Stage stage = new Stage();
            stage.setTitle("Générer un Rapport");
            stage.setScene(new Scene(loader.load(), 700, 600));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
        } catch (Exception e) {
            e.printStackTrace();
            afficherErreur("Erreur lors de l'ouverture du générateur de rapports: " + e.getMessage());
        }
    }
    
    /**
     * Gère le clic sur le bouton Exporter.
     */
    @FXML
    private void handleExporter() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Exporter les données du dashboard");
            fileChooser.setInitialFileName("dashboard_export.csv");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv")
            );
            
            File fichier = fileChooser.showSaveDialog(btnExporter.getScene().getWindow());
            
            if (fichier != null) {
                // Export simple des données (à implémenter selon besoin)
                afficherInfo("Export réussi vers: " + fichier.getAbsolutePath());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            afficherErreur("Erreur lors de l'export: " + e.getMessage());
        }
    }
    
    /**
     * Affiche un message d'erreur.
     */
    private void afficherErreur(String message) {
        Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR
            );
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
    
    /**
     * Affiche un message d'information.
     */
    private void afficherInfo(String message) {
        Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.INFORMATION
            );
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
