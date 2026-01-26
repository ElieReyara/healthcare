package com.healthcenter.controller;

import com.healthcenter.dto.*;
import com.healthcenter.service.StatistiqueService;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Contrôleur pour le module Statistiques.
 * Affiche le dashboard et les graphiques de statistiques.
 */
@Component
public class StatistiqueController {
    
    @Autowired
    private StatistiqueService statistiqueService;
    
    // Dashboard Stats
    @FXML private Label lblNbTotalPatients;
    @FXML private Label lblNbPatientsMois;
    @FXML private Label lblNbConsultationsMois;
    @FXML private Label lblNbVaccinationsMois;
    @FXML private Label lblNbMedicamentsStock;
    @FXML private Label lblNbMedicamentsRupture;
    
    // Charts
    @FXML private PieChart chartRepartitionSexe;
    @FXML private BarChart<String, Number> chartConsultationsMois;
    @FXML private BarChart<String, Number> chartVaccinationsMois;
    @FXML private PieChart chartMaladiesFrequentes;
    
    // Filtres de période
    @FXML private DatePicker dateDebut;
    @FXML private DatePicker dateFin;
    @FXML private ComboBox<String> comboPeriode;
    
    /**
     * Initialisation du contrôleur.
     */
    @FXML
    public void initialize() {
        // Initialiser les dates par défaut
        dateFin.setValue(LocalDate.now());
        dateDebut.setValue(LocalDate.now().minusMonths(1));
        
        // Initialiser le combo période
        if (comboPeriode != null) {
            comboPeriode.getItems().addAll("7 jours", "30 jours", "3 mois", "6 mois", "1 an");
            comboPeriode.setValue("30 jours");
        }
        
        // Charger les statistiques
        chargerDashboardStats();
        chargerGraphiques();
    }
    
    /**
     * Charge les statistiques du dashboard.
     */
    private void chargerDashboardStats() {
        try {
            DashboardStats stats = statistiqueService.obtenirDashboardStats();
            
            if (stats != null) {
                // Mettre à jour les labels
                if (lblNbTotalPatients != null) 
                    lblNbTotalPatients.setText(String.valueOf(stats.getNbTotalPatients()));
                if (lblNbPatientsMois != null) 
                    lblNbPatientsMois.setText(String.valueOf(stats.getNbPatientsMois()));
                if (lblNbConsultationsMois != null) 
                    lblNbConsultationsMois.setText(String.valueOf(stats.getNbConsultationsMois()));
                if (lblNbVaccinationsMois != null) 
                    lblNbVaccinationsMois.setText(String.valueOf(stats.getNbVaccinationsMois()));
                if (lblNbMedicamentsStock != null) 
                    lblNbMedicamentsStock.setText(String.valueOf(stats.getNbMedicamentsStock()));
                if (lblNbMedicamentsRupture != null) 
                    lblNbMedicamentsRupture.setText(String.valueOf(stats.getNbMedicamentsRupture()));
            }
        } catch (Exception e) {
            afficherErreur("Erreur lors du chargement des statistiques", e.getMessage());
        }
    }
    
    /**
     * Charge les graphiques.
     */
    private void chargerGraphiques() {
        LocalDate debut = dateDebut != null ? dateDebut.getValue() : LocalDate.now().minusMonths(1);
        LocalDate fin = dateFin != null ? dateFin.getValue() : LocalDate.now();
        
        try {
            // Graphique répartition par sexe
            if (chartRepartitionSexe != null) {
                List<RepartitionData> repartitionSexe = statistiqueService.obtenirRepartitionPatientsSexe();
                chartRepartitionSexe.getData().clear();
                for (RepartitionData data : repartitionSexe) {
                    PieChart.Data pieData = new PieChart.Data(data.getLabel(), data.getValeur().doubleValue());
                    chartRepartitionSexe.getData().add(pieData);
                }
            }
            
            // Graphique consultations par mois
            if (chartConsultationsMois != null) {
                List<EvolutionData> evolutionConsultations = 
                    statistiqueService.obtenirEvolutionConsultations(debut, fin, "MOIS");
                
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Consultations");
                
                for (EvolutionData data : evolutionConsultations) {
                    series.getData().add(new XYChart.Data<>(data.getPeriode(), data.getValeur()));
                }
                
                chartConsultationsMois.getData().clear();
                chartConsultationsMois.getData().add(series);
            }
            
            // Graphique maladies fréquentes
            if (chartMaladiesFrequentes != null) {
                List<RepartitionData> maladies = 
                    statistiqueService.obtenirMaladiesFrequentes(debut, fin, 10);
                
                chartMaladiesFrequentes.getData().clear();
                for (RepartitionData data : maladies) {
                    PieChart.Data pieData = new PieChart.Data(data.getLabel(), data.getValeur().doubleValue());
                    chartMaladiesFrequentes.getData().add(pieData);
                }
            }
            
        } catch (Exception e) {
            afficherErreur("Erreur lors du chargement des graphiques", e.getMessage());
        }
    }
    
    /**
     * Handler pour le bouton Rafraîchir.
     */
    @FXML
    private void handleRafraichir() {
        chargerDashboardStats();
        chargerGraphiques();
        afficherInfo("Statistiques mises à jour avec succès");
    }
    
    /**
     * Handler pour le changement de période.
     */
    @FXML
    private void handleChangementPeriode() {
        if (comboPeriode != null && comboPeriode.getValue() != null) {
            LocalDate fin = LocalDate.now();
            LocalDate debut = switch (comboPeriode.getValue()) {
                case "7 jours" -> fin.minusDays(7);
                case "30 jours" -> fin.minusMonths(1);
                case "3 mois" -> fin.minusMonths(3);
                case "6 mois" -> fin.minusMonths(6);
                case "1 an" -> fin.minusYears(1);
                default -> fin.minusMonths(1);
            };
            
            if (dateDebut != null) dateDebut.setValue(debut);
            if (dateFin != null) dateFin.setValue(fin);
            
            chargerGraphiques();
        }
    }
    
    /**
     * Affiche un message d'erreur.
     */
    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Affiche un message d'information.
     */
    private void afficherInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
