package com.healthcenter.service;

import com.healthcenter.domain.entities.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Service d'export de données en format CSV.
 * Fournit des méthodes pour exporter différentes entités.
 */
@Service
public class ExportService {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String CSV_SEPARATOR = ";";
    
    /**
     * Exporte une liste de patients en CSV.
     *
     * @param patients La liste des patients
     * @param cheminSortie Le chemin du fichier de sortie
     * @return Le fichier créé
     */
    public File exporterPatientsCSV(List<Patient> patients, String cheminSortie) {
        try {
            File fichier = new File(cheminSortie);
            fichier.getParentFile().mkdirs();
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(fichier))) {
                // En-tête
                writer.println("ID" + CSV_SEPARATOR + 
                             "Nom" + CSV_SEPARATOR + 
                             "Prénom" + CSV_SEPARATOR + 
                             "Date Naissance" + CSV_SEPARATOR + 
                             "Sexe" + CSV_SEPARATOR + 
                             "Téléphone" + CSV_SEPARATOR + 
                             "Email" + CSV_SEPARATOR + 
                             "Adresse" + CSV_SEPARATOR + 
                             "Date Inscription");
                
                // Données
                for (Patient patient : patients) {
                    writer.println(
                        escape(patient.getId()) + CSV_SEPARATOR +
                        escape(patient.getNom()) + CSV_SEPARATOR +
                        escape(patient.getPrenom()) + CSV_SEPARATOR +
                        escape(patient.getDateNaissance() != null ? 
                              patient.getDateNaissance().format(DATE_FORMATTER) : "") + CSV_SEPARATOR +
                        escape(patient.getSexe() != null ? patient.getSexe().name() : "") + CSV_SEPARATOR +
                        escape(patient.getTelephone()) + CSV_SEPARATOR +
                        escape(patient.getAdresse()) + CSV_SEPARATOR +
                        escape(patient.getNumeroCarnet())
                    );
                }
            }
            
            return fichier;
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'export CSV des patients: " + e.getMessage(), e);
        }
    }
    
    /**
     * Exporte une liste de consultations en CSV.
     *
     * @param consultations La liste des consultations
     * @param cheminSortie Le chemin du fichier de sortie
     * @return Le fichier créé
     */
    public File exporterConsultationsCSV(List<Consultation> consultations, String cheminSortie) {
        try {
            File fichier = new File(cheminSortie);
            fichier.getParentFile().mkdirs();
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(fichier))) {
                // En-tête
                writer.println("ID" + CSV_SEPARATOR + 
                             "Date" + CSV_SEPARATOR + 
                             "Patient" + CSV_SEPARATOR + 
                             "Personnel" + CSV_SEPARATOR + 
                             "Motif" + CSV_SEPARATOR + 
                             "Diagnostic" + CSV_SEPARATOR + 
                             "Traitement");
                
                // Données
                for (Consultation consultation : consultations) {
                    writer.println(
                        escape(consultation.getId()) + CSV_SEPARATOR +
                        escape(consultation.getDateConsultation() != null ? 
                              consultation.getDateConsultation().format(DATETIME_FORMATTER) : "") + CSV_SEPARATOR +
                        escape(consultation.getPatient() != null ? 
                              consultation.getPatient().getNom() + " " + consultation.getPatient().getPrenom() : "") + CSV_SEPARATOR +
                        escape(consultation.getPersonnel() != null ? 
                              consultation.getPersonnel().getNomComplet() : "") + CSV_SEPARATOR +
                        escape(consultation.getSymptomes()) + CSV_SEPARATOR +
                        escape(consultation.getDiagnostic()) + CSV_SEPARATOR +
                        escape(consultation.getPrescription())
                    );
                }
            }
            
            return fichier;
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'export CSV des consultations: " + e.getMessage(), e);
        }
    }
    
    /**
     * Exporte une liste de vaccinations en CSV.
     *
     * @param vaccinations La liste des vaccinations
     * @param cheminSortie Le chemin du fichier de sortie
     * @return Le fichier créé
     */
    public File exporterVaccinationsCSV(List<Vaccination> vaccinations, String cheminSortie) {
        try {
            File fichier = new File(cheminSortie);
            fichier.getParentFile().mkdirs();
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(fichier))) {
                // En-tête
                writer.println("ID" + CSV_SEPARATOR + 
                             "Date Administration" + CSV_SEPARATOR + 
                             "Patient" + CSV_SEPARATOR + 
                             "Personnel" + CSV_SEPARATOR + 
                             "Type Vaccin" + CSV_SEPARATOR + 
                             "Lot" + CSV_SEPARATOR + 
                             "Date Rappel");
                
                // Données
                for (Vaccination vaccination : vaccinations) {
                    writer.println(
                        escape(vaccination.getId()) + CSV_SEPARATOR +
                        escape(vaccination.getDateAdministration() != null ? 
                              vaccination.getDateAdministration().format(DATE_FORMATTER) : "") + CSV_SEPARATOR +
                        escape(vaccination.getPatient() != null ? 
                              vaccination.getPatient().getNom() + " " + vaccination.getPatient().getPrenom() : "") + CSV_SEPARATOR +
                        escape(vaccination.getPersonnel() != null ? 
                              vaccination.getPersonnel().getNomComplet() : "") + CSV_SEPARATOR +
                        escape(vaccination.getVaccin() != null ? 
                              vaccination.getVaccin().name() : "") + CSV_SEPARATOR +
                        escape(vaccination.getNumeroLot()) + CSV_SEPARATOR +
                        escape(vaccination.getDateRappel() != null ? 
                              vaccination.getDateRappel().format(DATE_FORMATTER) : "")
                    );
                }
            }
            
            return fichier;
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'export CSV des vaccinations: " + e.getMessage(), e);
        }
    }
    
    /**
     * Exporte une liste de médicaments en CSV.
     *
     * @param medicaments La liste des médicaments
     * @param cheminSortie Le chemin du fichier de sortie
     * @return Le fichier créé
     */
    public File exporterMedicamentsCSV(List<Medicament> medicaments, String cheminSortie) {
        try {
            File fichier = new File(cheminSortie);
            fichier.getParentFile().mkdirs();
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(fichier))) {
                // En-tête
                writer.println("ID" + CSV_SEPARATOR + 
                             "Nom" + CSV_SEPARATOR + 
                             "Catégorie" + CSV_SEPARATOR + 
                             "Forme" + CSV_SEPARATOR + 
                             "Dosage" + CSV_SEPARATOR + 
                             "Quantité Stock" + CSV_SEPARATOR + 
                             "Seuil Alerte" + CSV_SEPARATOR + 
                             "Prix Unitaire" + CSV_SEPARATOR + 
                             "Date Péremption");
                
                // Données
                for (Medicament medicament : medicaments) {
                    writer.println(
                        escape(medicament.getId()) + CSV_SEPARATOR +
                        escape(medicament.getNom()) + CSV_SEPARATOR +
                        escape(medicament.getForme() != null ? 
                              medicament.getForme().name() : "") + CSV_SEPARATOR +
                        escape(medicament.getDosage()) + CSV_SEPARATOR +
                        escape(medicament.getStockActuel()) + CSV_SEPARATOR +
                        escape(medicament.getSeuilAlerte()) + CSV_SEPARATOR +
                        escape(medicament.getPrix())
                    );
                }
            }
            
            return fichier;
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'export CSV des médicaments: " + e.getMessage(), e);
        }
    }
    
    /**
     * Exporte des données multi-tables en CSV (une feuille par table).
     * Génère un fichier CSV par type de données.
     *
     * @param donnees Map avec clé = nom du type, valeur = liste d'objets
     * @param cheminDossier Dossier de sortie
     * @return Le dossier contenant les fichiers
     */
    public File exporterDonneesExcel(Map<String, List<?>> donnees, String cheminDossier) {
        try {
            File dossier = new File(cheminDossier);
            dossier.mkdirs();
            
            for (Map.Entry<String, List<?>> entry : donnees.entrySet()) {
                String nomFichier = entry.getKey() + ".csv";
                String cheminFichier = cheminDossier + "/" + nomFichier;
                
                List<?> liste = entry.getValue();
                if (liste != null && !liste.isEmpty()) {
                    Object premier = liste.get(0);
                    
                    if (premier instanceof Patient) {
                        exporterPatientsCSV((List<Patient>) liste, cheminFichier);
                    } else if (premier instanceof Consultation) {
                        exporterConsultationsCSV((List<Consultation>) liste, cheminFichier);
                    } else if (premier instanceof Vaccination) {
                        exporterVaccinationsCSV((List<Vaccination>) liste, cheminFichier);
                    } else if (premier instanceof Medicament) {
                        exporterMedicamentsCSV((List<Medicament>) liste, cheminFichier);
                    }
                }
            }
            
            return dossier;
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'export multi-tables: " + e.getMessage(), e);
        }
    }
    
    /**
     * Échappe les valeurs pour CSV (gère les guillemets et séparateurs).
     */
    private String escape(Object valeur) {
        if (valeur == null) {
            return "";
        }
        
        String str = valeur.toString();
        
        // Si la chaîne contient le séparateur ou des guillemets, l'entourer de guillemets
        if (str.contains(CSV_SEPARATOR) || str.contains("\"") || str.contains("\n")) {
            str = "\"" + str.replace("\"", "\"\"") + "\"";
        }
        
        return str;
    }
}
