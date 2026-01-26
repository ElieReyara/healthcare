package com.healthcenter.controller;

import com.healthcenter.domain.entities.Consultation;
import com.healthcenter.domain.entities.DisponibilitePersonnel;
import com.healthcenter.domain.entities.Personnel;
import com.healthcenter.service.DisponibilitePersonnelService;
import com.healthcenter.service.PersonnelService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * CONTROLLER JavaFX - Fiche Détails Personnel
 * 
 * Affiche informations complètes d'un personnel avec 3 onglets :
 * - Statistiques : Activité consultations
 * - Planning : Disponibilités par jour semaine
 * - Historique : Liste consultations réalisées
 * 
 * @author Health Center Team
 */
@Component
public class PersonnelDetailsController {
    
    @Autowired
    private PersonnelService personnelService;
    
    @Autowired
    private DisponibilitePersonnelService disponibiliteService;
    
    // ========== COMPOSANTS FXML - INFOS GÉNÉRALES ==========
    
    @FXML private Label nomCompletLabel;
    @FXML private Label fonctionLabel;
    @FXML private Label specialisationLabel;
    @FXML private Label contactLabel;
    @FXML private Label matriculeLabel;
    @FXML private Label dateEmbaucheLabel;
    
    // ========== COMPOSANTS FXML - ONGLET STATISTIQUES ==========
    
    @FXML private Label nbTotalConsultationsLabel;
    @FXML private Label nbConsultationsMoisLabel;
    @FXML private Label nbConsultationsSemaineLabel;
    
    // ========== COMPOSANTS FXML - ONGLET PLANNING ==========
    
    @FXML private GridPane planningGrid;
    
    // ========== COMPOSANTS FXML - ONGLET HISTORIQUE ==========
    
    @FXML private TableView<Consultation> consultationsTable;
    @FXML private TableColumn<Consultation, LocalDateTime> colDate;
    @FXML private TableColumn<Consultation, String> colPatient;
    @FXML private TableColumn<Consultation, String> colDiagnostic;
    
    private Personnel personnel;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    
    /**
     * Initialise la fiche avec un personnel.
     * 
     * @param personnel Personnel à afficher
     */
    public void initWithPersonnel(Personnel personnel) {
        this.personnel = personnel;
        
        afficherInfosGenerales();
        chargerStatistiques();
        chargerPlanning();
        chargerHistoriqueConsultations();
    }
    
    /**
     * Affiche les informations générales du personnel.
     */
    private void afficherInfosGenerales() {
        nomCompletLabel.setText(personnel.getNomComplet());
        fonctionLabel.setText(personnel.getFonctionLibelle());
        specialisationLabel.setText(
            personnel.getSpecialisation() != null ? personnel.getSpecialisation() : "Non spécifié"
        );
        
        // Contact
        StringBuilder contact = new StringBuilder();
        if (personnel.getTelephone() != null) {
            contact.append("📞 ").append(personnel.getTelephone());
        }
        if (personnel.getEmail() != null) {
            if (contact.length() > 0) contact.append(" | ");
            contact.append("📧 ").append(personnel.getEmail());
        }
        contactLabel.setText(contact.length() > 0 ? contact.toString() : "Non renseigné");
        
        matriculeLabel.setText(
            personnel.getNumeroMatricule() != null ? personnel.getNumeroMatricule() : "Non attribué"
        );
        dateEmbaucheLabel.setText(
            personnel.getDateEmbauche() != null ? personnel.getDateEmbauche().format(DATE_FORMATTER) : "Non renseignée"
        );
    }
    
    /**
     * Charge les statistiques d'activité.
     */
    private void chargerStatistiques() {
        try {
            // Total consultations
            long nbTotal = personnel.getConsultations().size();
            nbTotalConsultationsLabel.setText(String.valueOf(nbTotal));
            
            // Consultations ce mois
            LocalDate debutMois = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
            LocalDate finMois = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
            
            Map<String, Object> statsMois = personnelService.obtenirStatistiquesConsultations(
                personnel.getId(), debutMois, finMois
            );
            nbConsultationsMoisLabel.setText(String.valueOf(statsMois.get("nbConsultations")));
            
            // Consultations cette semaine
            LocalDate debutSemaine = LocalDate.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
            LocalDate finSemaine = LocalDate.now().with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));
            
            Map<String, Object> statsSemaine = personnelService.obtenirStatistiquesConsultations(
                personnel.getId(), debutSemaine, finSemaine
            );
            nbConsultationsSemaineLabel.setText(String.valueOf(statsSemaine.get("nbConsultations")));
            
        } catch (Exception e) {
            System.err.println("❌ Erreur chargement statistiques : " + e.getMessage());
            e.printStackTrace();
            
            nbTotalConsultationsLabel.setText("Erreur");
            nbConsultationsMoisLabel.setText("Erreur");
            nbConsultationsSemaineLabel.setText("Erreur");
        }
    }
    
    /**
     * Charge le planning des disponibilités.
     */
    private void chargerPlanning() {
        try {
            List<DisponibilitePersonnel> disponibilites = disponibiliteService.obtenirDisponibilitesParPersonnel(personnel.getId());
            
            // Grouper par jour
            Map<String, String> planningParJour = disponibilites.stream()
                .collect(Collectors.groupingBy(
                    dispo -> dispo.getJourSemaine().getLibelle(),
                    Collectors.mapping(
                        DisponibilitePersonnel::getCreneauFormate,
                        Collectors.joining(", ")
                    )
                ));
            
            // Afficher dans GridPane (supposé avoir 7 lignes pour les jours)
            String[] jours = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};
            
            for (int i = 0; i < jours.length; i++) {
                String jour = jours[i];
                String creneaux = planningParJour.getOrDefault(jour, "Non disponible");
                
                // Créer Labels dynamiquement
                Label jourLabel = new Label(jour + " :");
                jourLabel.setStyle("-fx-font-weight: bold;");
                
                Label creneauxLabel = new Label(creneaux);
                creneauxLabel.setStyle(creneaux.equals("Non disponible") ? "-fx-text-fill: gray;" : "-fx-text-fill: green;");
                
                planningGrid.add(jourLabel, 0, i);
                planningGrid.add(creneauxLabel, 1, i);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erreur chargement planning : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Charge l'historique des consultations.
     */
    private void chargerHistoriqueConsultations() {
        try {
            // Configurer colonnes
            colDate.setCellValueFactory(new PropertyValueFactory<>("dateConsultation"));
            colDate.setCellFactory(column -> new javafx.scene.control.TableCell<Consultation, LocalDateTime>() {
                @Override
                protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                    }
                }
            });
            
            colPatient.setCellValueFactory(cellData -> {
                if (cellData.getValue().getPatient() != null) {
                    return new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getPatient().getPrenom() + " " + 
                        cellData.getValue().getPatient().getNom()
                    );
                }
                return new javafx.beans.property.SimpleStringProperty("N/A");
            });
            
            colDiagnostic.setCellValueFactory(new PropertyValueFactory<>("diagnostic"));
            
            // Charger données
            List<Consultation> consultations = personnel.getConsultations();
            
            // Trier par date décroissante
            consultations.sort((c1, c2) -> c2.getDateConsultation().compareTo(c1.getDateConsultation()));
            
            ObservableList<Consultation> consultationData = FXCollections.observableArrayList(consultations);
            consultationsTable.setItems(consultationData);
            
        } catch (Exception e) {
            System.err.println("❌ Erreur chargement historique consultations : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Handler bouton Modifier Planning.
     */
    @FXML
    private void handleModifierPlanning() {
        // TODO : Ouvrir dialog modification planning
        // Pour l'instant, message info
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Fonctionnalité à venir");
        alert.setHeaderText("Modification planning");
        alert.setContentText("La modification du planning sera disponible dans une prochaine version.");
        alert.showAndWait();
    }
    
    /**
     * Handler bouton Fermer.
     */
    @FXML
    private void handleClose() {
        Stage stage = (Stage) nomCompletLabel.getScene().getWindow();
        stage.close();
    }
}
