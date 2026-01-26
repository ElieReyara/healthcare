package com.healthcenter.controller;

import com.healthcenter.domain.enums.PeriodeStatistique;
import com.healthcenter.dto.*;
import com.healthcenter.service.StatistiqueService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur des statistiques détaillées.
 * Permet de visualiser les statistiques par catégorie avec filtres.
 */
@Controller
public class StatistiquesDetailsController {
    
    @Autowired
    private StatistiqueService statistiqueService;
    
    @FXML private ComboBox<String> categorieCombo;
    @FXML private ComboBox<PeriodeStatistique> periodeCombo;
    @FXML private DatePicker dateDebutPicker;
    @FXML private DatePicker dateFinPicker;
    @FXML private Button btnAppliquer;
    
    @FXML private TabPane detailsTabPane;
    @FXML private Tab syntheseTab;
    @FXML private Tab graphiquesTab;
    @FXML private Tab donneesTab;
    
    @FXML private VBox syntheseContainer;
    @FXML private VBox graphiquesContainer;
    @FXML private TableView<?> donneesTable;
    
    /**
     * Initialisation du contrôleur.
     */
    @FXML
    public void initialize() {
        // Initialiser les catégories
        categorieCombo.setItems(FXCollections.observableArrayList(
                "Patients", "Consultations", "Vaccinations", "Médicaments", "Personnel"
        ));
        categorieCombo.getSelectionModel().selectFirst();
        
        // Initialiser les périodes
        periodeCombo.setItems(FXCollections.observableArrayList(PeriodeStatistique.values()));
        periodeCombo.getSelectionModel().select(PeriodeStatistique.MOIS);
        
        // Masquer les DatePickers initialement
        dateDebutPicker.setVisible(false);
        dateFinPicker.setVisible(false);
        dateDebutPicker.setManaged(false);
        dateFinPicker.setManaged(false);
        
        // Listener pour la période
        periodeCombo.setOnAction(e -> handlePeriodeSelected());
        
        // Charger les données initiales
        handleAppliquer();
    }
    
    /**
     * Gère le clic sur la catégorie.
     */
    @FXML
    private void handleCategorieSelected() {
        // La catégorie change, prêt pour appliquer
    }
    
    /**
     * Gère la sélection de la période.
     */
    private void handlePeriodeSelected() {
        PeriodeStatistique periode = periodeCombo.getValue();
        
        if (periode == PeriodeStatistique.PERSONNALISE) {
            dateDebutPicker.setVisible(true);
            dateFinPicker.setVisible(true);
            dateDebutPicker.setManaged(true);
            dateFinPicker.setManaged(true);
            
            if (dateDebutPicker.getValue() == null) {
                dateDebutPicker.setValue(LocalDate.now().minusMonths(1));
            }
            if (dateFinPicker.getValue() == null) {
                dateFinPicker.setValue(LocalDate.now());
            }
        } else {
            dateDebutPicker.setVisible(false);
            dateFinPicker.setVisible(false);
            dateDebutPicker.setManaged(false);
            dateFinPicker.setManaged(false);
        }
    }
    
    /**
     * Gère le clic sur Appliquer.
     */
    @FXML
    private void handleAppliquer() {
        try {
            String categorie = categorieCombo.getValue();
            LocalDate[] dates = calculerDatesPeriode();
            LocalDate debut = dates[0];
            LocalDate fin = dates[1];
            
            switch (categorie) {
                case "Patients" -> chargerStatistiquesPatients(debut, fin);
                case "Consultations" -> chargerStatistiquesConsultations(debut, fin);
                case "Vaccinations" -> chargerStatistiquesVaccinations(debut, fin);
                case "Médicaments" -> chargerStatistiquesMedicaments();
                case "Personnel" -> chargerStatistiquesPersonnel(debut, fin);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            afficherErreur("Erreur lors du chargement des statistiques: " + e.getMessage());
        }
    }
    
    /**
     * Charge les statistiques des patients.
     */
    private void chargerStatistiquesPatients(LocalDate debut, LocalDate fin) {
        StatistiquesPatients stats = statistiqueService.obtenirStatistiquesPatients(debut, fin);
        
        // Onglet Synthèse
        syntheseContainer.getChildren().clear();
        syntheseContainer.getChildren().add(new Label("STATISTIQUES PATIENTS"));
        syntheseContainer.getChildren().add(new Label("Nombre total: " + stats.getNbTotal()));
        syntheseContainer.getChildren().add(new Label("Âge moyen: " + String.format("%.1f ans", stats.getMoyenneAge())));
        
        // Répartition sexe
        syntheseContainer.getChildren().add(new Label("\nRépartition par sexe:"));
        for (Map.Entry<String, Long> entry : stats.getRepartitionSexe().entrySet()) {
            syntheseContainer.getChildren().add(new Label("  " + entry.getKey() + ": " + entry.getValue()));
        }
        
        // Répartition âge
        syntheseContainer.getChildren().add(new Label("\nRépartition par âge:"));
        for (Map.Entry<String, Long> entry : stats.getRepartitionAge().entrySet()) {
            syntheseContainer.getChildren().add(new Label("  " + entry.getKey() + ": " + entry.getValue()));
        }
    }
    
    /**
     * Charge les statistiques des consultations.
     */
    private void chargerStatistiquesConsultations(LocalDate debut, LocalDate fin) {
        StatistiquesConsultations stats = statistiqueService.obtenirStatistiquesConsultations(debut, fin);
        
        // Onglet Synthèse
        syntheseContainer.getChildren().clear();
        syntheseContainer.getChildren().add(new Label("STATISTIQUES CONSULTATIONS"));
        syntheseContainer.getChildren().add(new Label("Nombre total: " + stats.getNbTotal()));
        syntheseContainer.getChildren().add(new Label("Nombre période: " + stats.getNbPeriode()));
        syntheseContainer.getChildren().add(new Label("Moyenne par jour: " + String.format("%.1f", stats.getMoyenneParJour())));
        
        // Maladies fréquentes
        syntheseContainer.getChildren().add(new Label("\nTop 5 maladies:"));
        List<RepartitionData> maladies = stats.getMaladiesFrequentes();
        for (int i = 0; i < Math.min(5, maladies.size()); i++) {
            RepartitionData maladie = maladies.get(i);
            syntheseContainer.getChildren().add(new Label("  " + (i+1) + ". " + maladie.getLabel() + ": " + maladie.getValeur()));
        }
    }
    
    /**
     * Charge les statistiques des vaccinations.
     */
    private void chargerStatistiquesVaccinations(LocalDate debut, LocalDate fin) {
        Map<String, Object> stats = statistiqueService.obtenirStatistiquesVaccinations(debut, fin);
        
        // Onglet Synthèse
        syntheseContainer.getChildren().clear();
        syntheseContainer.getChildren().add(new Label("STATISTIQUES VACCINATIONS"));
        syntheseContainer.getChildren().add(new Label("Nombre total: " + stats.get("nbTotal")));
        syntheseContainer.getChildren().add(new Label("Nombre période: " + stats.get("nbPeriode")));
        
        // Couverture vaccinale
        @SuppressWarnings("unchecked")
        List<RepartitionData> couverture = (List<RepartitionData>) stats.get("couvertureVaccinale");
        
        syntheseContainer.getChildren().add(new Label("\nCouverture vaccinale:"));
        for (RepartitionData data : couverture) {
            syntheseContainer.getChildren().add(new Label("  " + data.getLabel() + ": " + 
                    String.format("%.1f%%", data.getValeur().doubleValue())));
        }
    }
    
    /**
     * Charge les statistiques des médicaments.
     */
    private void chargerStatistiquesMedicaments() {
        Map<String, Object> stats = statistiqueService.obtenirStatistiquesMedicaments();
        
        // Onglet Synthèse
        syntheseContainer.getChildren().clear();
        syntheseContainer.getChildren().add(new Label("STATISTIQUES MÉDICAMENTS"));
        syntheseContainer.getChildren().add(new Label("Nombre total: " + stats.get("nbTotal")));
        syntheseContainer.getChildren().add(new Label("Nombre en rupture: " + stats.get("nbRupture")));
        syntheseContainer.getChildren().add(new Label("Valeur stock total: " + 
                String.format("%.2f FCFA", stats.get("valeurStockTotal"))));
    }
    
    /**
     * Charge les statistiques du personnel.
     */
    private void chargerStatistiquesPersonnel(LocalDate debut, LocalDate fin) {
        Map<String, Object> stats = statistiqueService.obtenirStatistiquesPersonnel(debut, fin);
        
        // Onglet Synthèse
        syntheseContainer.getChildren().clear();
        syntheseContainer.getChildren().add(new Label("STATISTIQUES PERSONNEL"));
        syntheseContainer.getChildren().add(new Label("Nombre actif: " + stats.get("nbActif")));
        
        // Répartition par fonction
        @SuppressWarnings("unchecked")
        List<RepartitionData> repartition = (List<RepartitionData>) stats.get("repartitionFonction");
        
        syntheseContainer.getChildren().add(new Label("\nRépartition par fonction:"));
        for (RepartitionData data : repartition) {
            syntheseContainer.getChildren().add(new Label("  " + data.getLabel() + ": " + data.getValeur()));
        }
    }
    
    /**
     * Calcule les dates selon la période sélectionnée.
     */
    private LocalDate[] calculerDatesPeriode() {
        PeriodeStatistique periode = periodeCombo.getValue();
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
