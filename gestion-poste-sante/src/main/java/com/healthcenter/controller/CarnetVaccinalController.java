package com.healthcenter.controller;

import com.healthcenter.domain.entities.CalendrierVaccinal;
import com.healthcenter.domain.entities.Patient;
import com.healthcenter.domain.entities.Vaccination;
import com.healthcenter.domain.enums.StatutVaccination;
import com.healthcenter.domain.enums.TypeVaccin;
import com.healthcenter.service.CalendrierVaccinalService;
import com.healthcenter.service.VaccinationService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * CARNET VACCINAL CONTROLLER = Affichage du carnet de vaccination d'un patient.
 * 
 * Fonctionnalités :
 * - Affiche toutes les vaccinations (faites + manquantes)
 * - Coloration selon statut (✅ fait, ❌ manquant, 🔔 rappel)
 * - Détection vaccins manquants selon calendrier + âge patient
 * - Impression carnet (PDF)
 */
@Component
public class CarnetVaccinalController {
    
    @Autowired
    private VaccinationService vaccinationService;
    
    @Autowired
    private CalendrierVaccinalService calendrierService;
    
    // ========== COMPOSANTS FXML ==========
    
    @FXML private Label patientNomLabel;
    @FXML private Label patientAgeLabel;
    @FXML private TableView<VaccinationCarnetRow> carnetTable;
    @FXML private TableColumn<VaccinationCarnetRow, String> colVaccin;
    @FXML private TableColumn<VaccinationCarnetRow, String> colStatut;
    @FXML private TableColumn<VaccinationCarnetRow, LocalDate> colDateFait;
    @FXML private TableColumn<VaccinationCarnetRow, LocalDate> colDateRappel;
    
    private Patient patientActuel;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    
    /**
     * Initialisation après chargement FXML.
     */
    @FXML
    public void initialize() {
        // Configuration colonnes
        colVaccin.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVaccinLibelle()));
        
        // Colonne Statut avec coloration
        colStatut.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatut()));
        colStatut.setCellFactory(column -> new TableCell<VaccinationCarnetRow, String>() {
            @Override
            protected void updateItem(String statut, boolean empty) {
                super.updateItem(statut, empty);
                
                if (empty || statut == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(statut);
                    
                    // Coloration selon statut
                    if (statut.startsWith("✅")) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else if (statut.startsWith("❌")) {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    } else if (statut.startsWith("🔔")) {
                        setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                    } else if (statut.startsWith("⚠️")) {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
        
        // Colonne Date Fait
        colDateFait.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDateFait()));
        colDateFait.setCellFactory(column -> new TableCell<VaccinationCarnetRow, LocalDate>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setText(empty || date == null ? "" : date.format(DATE_FORMATTER));
            }
        });
        
        // Colonne Date Rappel
        colDateRappel.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDateRappel()));
        colDateRappel.setCellFactory(column -> new TableCell<VaccinationCarnetRow, LocalDate>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                
                if (empty || date == null) {
                    setText("");
                    setStyle("");
                } else {
                    setText(date.format(DATE_FORMATTER));
                    
                    // Coloration si rappel proche ou en retard
                    if (date.isBefore(LocalDate.now())) {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    } else if (date.isBefore(LocalDate.now().plusDays(7))) {
                        setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
    }
    
    
    // ========== INITIALISATION AVEC PATIENT ==========
    
    /**
     * Initialise le carnet pour un patient donné.
     */
    public void initWithPatient(Patient patient) {
        this.patientActuel = patient;
        
        // Afficher infos patient
        patientNomLabel.setText(patient.getPrenom() + " " + patient.getNom());
        
        // Calculer âge
        Period age = Period.between(patient.getDateNaissance(), LocalDate.now());
        patientAgeLabel.setText(age.getYears() + " ans, " + age.getMonths() + " mois");
        
        // Charger carnet vaccinal
        chargerCarnetVaccinal();
    }
    
    
    // ========== CHARGEMENT CARNET ==========
    
    private void chargerCarnetVaccinal() {
        List<VaccinationCarnetRow> rows = new ArrayList<>();
        
        // 1. Récupérer vaccinations effectuées
        Map<TypeVaccin, List<Vaccination>> carnet = vaccinationService.obtenirCarnetVaccinal(patientActuel.getId());
        
        // 2. Récupérer calendrier complet
        List<CalendrierVaccinal> calendriers = calendrierService.obtenirTousCalendriers();
        
        // 3. Pour chaque vaccin du calendrier, créer une ligne
        for (CalendrierVaccinal calendrier : calendriers) {
            TypeVaccin vaccin = calendrier.getVaccin();
            List<Vaccination> vaccinations = carnet.getOrDefault(vaccin, Collections.emptyList());
            
            if (vaccinations.isEmpty()) {
                // VACCIN NON ADMINISTRÉ
                if (isVaccinRequis(calendrier)) {
                    // Vaccin requis selon âge + obligatoire
                    rows.add(new VaccinationCarnetRow(
                        vaccin,
                        "❌ Non fait (obligatoire)",
                        null,
                        null
                    ));
                } else {
                    // Vaccin pas encore requis selon âge
                    rows.add(new VaccinationCarnetRow(
                        vaccin,
                        "⏳ Pas encore requis",
                        null,
                        null
                    ));
                }
            } else {
                // VACCIN ADMINISTRÉ (peut-être plusieurs doses)
                for (Vaccination vaccination : vaccinations) {
                    String statutText = mapStatutToText(vaccination.getStatut());
                    
                    rows.add(new VaccinationCarnetRow(
                        vaccin,
                        statutText,
                        vaccination.getDateAdministration(),
                        vaccination.getDateRappel()
                    ));
                }
            }
        }
        
        // 4. Afficher dans TableView
        carnetTable.setItems(FXCollections.observableArrayList(rows));
    }
    
    
    // ========== VÉRIFICATIONS ==========
    
    /**
     * Vérifie si un vaccin est requis selon l'âge du patient.
     */
    private boolean isVaccinRequis(CalendrierVaccinal calendrier) {
        if (!calendrier.getObligatoire()) {
            return false;
        }
        
        // Calculer âge du patient en jours
        long ageJours = java.time.temporal.ChronoUnit.DAYS.between(
            patientActuel.getDateNaissance(), 
            LocalDate.now()
        );
        
        // Si âge >= âge recommandé, le vaccin est requis
        return ageJours >= calendrier.getAgeRecommande();
    }
    
    /**
     * Convertit StatutVaccination en texte avec emoji.
     */
    private String mapStatutToText(StatutVaccination statut) {
        switch (statut) {
            case ADMINISTRE:
                return "✅ Fait";
            case RAPPEL_PREVU:
                return "🔔 Rappel prévu";
            case RAPPEL_EN_RETARD:
                return "⚠️ Rappel en retard";
            default:
                return statut.name();
        }
    }
    
    
    // ========== HANDLERS ==========
    
    @FXML
    private void handleImprimer() {
        // TODO : Implémenter génération PDF
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.INFORMATION
        );
        alert.setTitle("Impression");
        alert.setHeaderText("Fonctionnalité en développement");
        alert.setContentText("L'impression du carnet vaccinal sera disponible prochainement.");
        alert.showAndWait();
    }
    
    @FXML
    private void handleFermer() {
        Stage stage = (Stage) carnetTable.getScene().getWindow();
        stage.close();
    }
    
    
    // ========== INNER CLASS : ROW ==========
    
    /**
     * Ligne du carnet vaccinal.
     */
    public static class VaccinationCarnetRow {
        private final TypeVaccin vaccin;
        private final String statut;
        private final LocalDate dateFait;
        private final LocalDate dateRappel;
        
        public VaccinationCarnetRow(TypeVaccin vaccin, String statut, LocalDate dateFait, LocalDate dateRappel) {
            this.vaccin = vaccin;
            this.statut = statut;
            this.dateFait = dateFait;
            this.dateRappel = dateRappel;
        }
        
        public String getVaccinLibelle() {
            return vaccin.getLibelle();
        }
        
        public String getStatut() {
            return statut;
        }
        
        public LocalDate getDateFait() {
            return dateFait;
        }
        
        public LocalDate getDateRappel() {
            return dateRappel;
        }
    }
}
