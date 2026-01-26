package com.healthcenter.service;

import com.healthcenter.domain.entities.Rapport;
import com.healthcenter.domain.enums.FormatRapport;
import com.healthcenter.domain.enums.TypeRapport;
import com.healthcenter.dto.RepartitionData;
import com.healthcenter.dto.StatistiquesConsultations;
import com.healthcenter.dto.StatistiquesPatients;
import com.healthcenter.repository.RapportRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Service de génération de rapports PDF et Excel.
 * Utilise iText pour PDF et Apache POI pour Excel.
 */
@Service
public class RapportService {
    
    @Autowired
    private StatistiqueService statistiqueService;
    
    @Autowired
    private RapportRepository rapportRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    /**
     * Génère un rapport PDF.
     *
     * @param type Le type de rapport
     * @param debut Date de début
     * @param fin Date de fin
     * @param cheminSortie Chemin du fichier de sortie
     * @return Le fichier généré
     */
    @Transactional
    public File genererRapportPDF(TypeRapport type, LocalDate debut, LocalDate fin, String cheminSortie) {
        try {
            // Collecter les données
            Map<String, Object> donnees = genererContenuRapport(type, debut, fin);
            
            // Créer le PDF
            File fichier = creerDocumentPDF(type.getLibelle(), donnees, cheminSortie);
            
            // Sauvegarder l'historique
            sauvegarderHistorique(type, debut, fin, fichier, FormatRapport.PDF);
            
            return fichier;
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du rapport PDF: " + e.getMessage(), e);
        }
    }
    
    /**
     * Génère un rapport Excel.
     *
     * @param type Le type de rapport
     * @param debut Date de début
     * @param fin Date de fin
     * @param cheminSortie Chemin du fichier de sortie
     * @return Le fichier généré
     */
    @Transactional
    public File genererRapportExcel(TypeRapport type, LocalDate debut, LocalDate fin, String cheminSortie) {
        try {
            // Collecter les données
            Map<String, Object> donnees = genererContenuRapport(type, debut, fin);
            
            // Créer l'Excel
            File fichier = creerDocumentExcel(type.getLibelle(), donnees, cheminSortie);
            
            // Sauvegarder l'historique
            sauvegarderHistorique(type, debut, fin, fichier, FormatRapport.EXCEL);
            
            return fichier;
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du rapport Excel: " + e.getMessage(), e);
        }
    }
    
    /**
     * Génère un rapport mensuel automatique.
     *
     * @param mois Le mois (1-12)
     * @param annee L'année
     * @param format Le format du rapport
     * @return Le fichier généré
     */
    @Transactional
    public File genererRapportMensuel(int mois, int annee, FormatRapport format) {
        LocalDate debut = LocalDate.of(annee, mois, 1);
        LocalDate fin = debut.withDayOfMonth(debut.lengthOfMonth());
        
        String nomFichier = String.format("rapport_mensuel_%04d-%02d.%s", 
                annee, mois, format.getExtension());
        String cheminSortie = "rapports/" + nomFichier;
        
        if (format == FormatRapport.PDF) {
            return genererRapportPDF(TypeRapport.MENSUEL, debut, fin, cheminSortie);
        } else {
            return genererRapportExcel(TypeRapport.MENSUEL, debut, fin, cheminSortie);
        }
    }
    
    /**
     * Génère un rapport annuel.
     *
     * @param annee L'année
     * @param format Le format du rapport
     * @return Le fichier généré
     */
    @Transactional
    public File genererRapportAnnuel(int annee, FormatRapport format) {
        LocalDate debut = LocalDate.of(annee, 1, 1);
        LocalDate fin = LocalDate.of(annee, 12, 31);
        
        String nomFichier = String.format("rapport_annuel_%04d.%s", annee, format.getExtension());
        String cheminSortie = "rapports/" + nomFichier;
        
        if (format == FormatRapport.PDF) {
            return genererRapportPDF(TypeRapport.ANNUEL, debut, fin, cheminSortie);
        } else {
            return genererRapportExcel(TypeRapport.ANNUEL, debut, fin, cheminSortie);
        }
    }
    
    /**
     * Obtient l'historique des rapports générés.
     *
     * @return Liste des 20 derniers rapports
     */
    @Transactional(readOnly = true)
    public List<Rapport> obtenirHistoriqueRapports() {
        return rapportRepository.findTop10ByOrderByDateGenerationDesc();
    }
    
    // ===== GÉNÉRATION CONTENU PAR TYPE =====
    
    /**
     * Génère le contenu du rapport selon son type.
     */
    private Map<String, Object> genererContenuRapport(TypeRapport type, LocalDate debut, LocalDate fin) {
        return switch (type) {
            case ACTIVITE_GLOBALE, MENSUEL, ANNUEL -> genererContenuRapportActiviteGlobale(debut, fin);
            case CONSULTATIONS -> genererContenuRapportConsultations(debut, fin);
            case VACCINATIONS -> genererContenuRapportVaccinations(debut, fin);
            case MEDICAMENTS_STOCK -> genererContenuRapportMedicaments(debut, fin);
            case PERSONNEL_ACTIVITE -> genererContenuRapportPersonnel(debut, fin);
            case MALADIES_FREQUENTES -> genererContenuRapportMaladiesFrequentes(debut, fin);
        };
    }
    
    /**
     * Génère le contenu pour le rapport d'activité globale.
     */
    public Map<String, Object> genererContenuRapportActiviteGlobale(LocalDate debut, LocalDate fin) {
        Map<String, Object> donnees = new java.util.HashMap<>();
        
        donnees.put("periode", debut.format(DATE_FORMATTER) + " - " + fin.format(DATE_FORMATTER));
        donnees.put("dashboard", statistiqueService.obtenirDashboardStats());
        donnees.put("statsPatients", statistiqueService.obtenirStatistiquesPatients(debut, fin));
        donnees.put("statsConsultations", statistiqueService.obtenirStatistiquesConsultations(debut, fin));
        donnees.put("statsVaccinations", statistiqueService.obtenirStatistiquesVaccinations(debut, fin));
        donnees.put("statsMedicaments", statistiqueService.obtenirStatistiquesMedicaments());
        donnees.put("statsPersonnel", statistiqueService.obtenirStatistiquesPersonnel(debut, fin));
        
        return donnees;
    }
    
    /**
     * Génère le contenu pour le rapport consultations.
     */
    public Map<String, Object> genererContenuRapportConsultations(LocalDate debut, LocalDate fin) {
        Map<String, Object> donnees = new java.util.HashMap<>();
        
        donnees.put("periode", debut.format(DATE_FORMATTER) + " - " + fin.format(DATE_FORMATTER));
        donnees.put("statsConsultations", statistiqueService.obtenirStatistiquesConsultations(debut, fin));
        donnees.put("maladiesFrequentes", statistiqueService.obtenirMaladiesFrequentes(debut, fin, 20));
        
        return donnees;
    }
    
    /**
     * Génère le contenu pour le rapport vaccinations.
     */
    public Map<String, Object> genererContenuRapportVaccinations(LocalDate debut, LocalDate fin) {
        Map<String, Object> donnees = new java.util.HashMap<>();
        
        donnees.put("periode", debut.format(DATE_FORMATTER) + " - " + fin.format(DATE_FORMATTER));
        donnees.put("statsVaccinations", statistiqueService.obtenirStatistiquesVaccinations(debut, fin));
        donnees.put("couvertureVaccinale", statistiqueService.obtenirCouvertureVaccinale());
        
        return donnees;
    }
    
    /**
     * Génère le contenu pour le rapport médicaments.
     */
    public Map<String, Object> genererContenuRapportMedicaments(LocalDate debut, LocalDate fin) {
        Map<String, Object> donnees = new java.util.HashMap<>();
        
        donnees.put("periode", debut.format(DATE_FORMATTER) + " - " + fin.format(DATE_FORMATTER));
        donnees.put("statsMedicaments", statistiqueService.obtenirStatistiquesMedicaments());
        
        return donnees;
    }
    
    /**
     * Génère le contenu pour le rapport personnel.
     */
    public Map<String, Object> genererContenuRapportPersonnel(LocalDate debut, LocalDate fin) {
        Map<String, Object> donnees = new java.util.HashMap<>();
        
        donnees.put("periode", debut.format(DATE_FORMATTER) + " - " + fin.format(DATE_FORMATTER));
        donnees.put("statsPersonnel", statistiqueService.obtenirStatistiquesPersonnel(debut, fin));
        donnees.put("repartitionFonction", statistiqueService.obtenirRepartitionPersonnelFonction());
        
        return donnees;
    }
    
    /**
     * Génère le contenu pour le rapport maladies fréquentes.
     */
    public Map<String, Object> genererContenuRapportMaladiesFrequentes(LocalDate debut, LocalDate fin) {
        Map<String, Object> donnees = new java.util.HashMap<>();
        
        donnees.put("periode", debut.format(DATE_FORMATTER) + " - " + fin.format(DATE_FORMATTER));
        donnees.put("maladiesFrequentes", statistiqueService.obtenirMaladiesFrequentes(debut, fin, 30));
        
        return donnees;
    }
    
    // ===== GÉNÉRATION PDF (iText) =====
    
    /**
     * Crée un document PDF avec iText.
     */
    private File creerDocumentPDF(String titre, Map<String, Object> donnees, String cheminSortie) throws Exception {
        File fichier = new File(cheminSortie);
        fichier.getParentFile().mkdirs();
        
        PdfWriter writer = new PdfWriter(new FileOutputStream(fichier));
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);
        
        // En-tête
        document.add(new Paragraph("POSTE DE SANTÉ - RAPPORT")
                .setFontSize(20)
                .setBold());
        document.add(new Paragraph(titre)
                .setFontSize(16)
                .setBold());
        document.add(new Paragraph("Période: " + donnees.get("periode"))
                .setFontSize(12));
        document.add(new Paragraph("Date de génération: " + LocalDate.now().format(DATE_FORMATTER))
                .setFontSize(10));
        document.add(new Paragraph("\n"));
        
        // Contenu selon le type de rapport
        if (donnees.containsKey("dashboard")) {
            ajouterSectionDashboardPDF(document, donnees);
        }
        
        if (donnees.containsKey("statsPatients")) {
            ajouterSectionPatientsPDF(document, donnees);
        }
        
        if (donnees.containsKey("statsConsultations")) {
            ajouterSectionConsultationsPDF(document, donnees);
        }
        
        if (donnees.containsKey("maladiesFrequentes")) {
            ajouterSectionMaladiesPDF(document, donnees);
        }
        
        document.close();
        
        return fichier;
    }
    
    /**
     * Ajoute la section dashboard au PDF.
     */
    private void ajouterSectionDashboardPDF(Document document, Map<String, Object> donnees) {
        document.add(new Paragraph("INDICATEURS CLÉS")
                .setFontSize(14)
                .setBold());
        
        // Créer un tableau pour les KPIs
        Table table = new Table(2);
        table.addCell("Indicateur");
        table.addCell("Valeur");
        
        // Ajouter les données (version simplifiée)
        table.addCell("Patients inscrits");
        table.addCell("À compléter"); // Récupérer depuis donnees
        
        document.add(table);
        document.add(new Paragraph("\n"));
    }
    
    /**
     * Ajoute la section patients au PDF.
     */
    private void ajouterSectionPatientsPDF(Document document, Map<String, Object> donnees) {
        document.add(new Paragraph("STATISTIQUES PATIENTS")
                .setFontSize(14)
                .setBold());
        
        StatistiquesPatients stats = (StatistiquesPatients) donnees.get("statsPatients");
        
        document.add(new Paragraph("Nombre total: " + stats.getNbTotal()));
        document.add(new Paragraph("Âge moyen: " + String.format("%.1f ans", stats.getMoyenneAge())));
        document.add(new Paragraph("\n"));
    }
    
    /**
     * Ajoute la section consultations au PDF.
     */
    private void ajouterSectionConsultationsPDF(Document document, Map<String, Object> donnees) {
        document.add(new Paragraph("STATISTIQUES CONSULTATIONS")
                .setFontSize(14)
                .setBold());
        
        StatistiquesConsultations stats = (StatistiquesConsultations) donnees.get("statsConsultations");
        
        document.add(new Paragraph("Nombre total: " + stats.getNbTotal()));
        document.add(new Paragraph("Nombre période: " + stats.getNbPeriode()));
        document.add(new Paragraph("Moyenne par jour: " + String.format("%.1f", stats.getMoyenneParJour())));
        document.add(new Paragraph("\n"));
    }
    
    /**
     * Ajoute la section maladies fréquentes au PDF.
     */
    @SuppressWarnings("unchecked")
    private void ajouterSectionMaladiesPDF(Document document, Map<String, Object> donnees) {
        document.add(new Paragraph("MALADIES FRÉQUENTES")
                .setFontSize(14)
                .setBold());
        
        List<RepartitionData> maladies = (List<RepartitionData>) donnees.get("maladiesFrequentes");
        
        Table table = new Table(3);
        table.addCell("Rang");
        table.addCell("Diagnostic");
        table.addCell("Nombre");
        
        int rang = 1;
        for (RepartitionData maladie : maladies) {
            table.addCell(String.valueOf(rang++));
            table.addCell(maladie.getLabel());
            table.addCell(maladie.getValeur().toString());
        }
        
        document.add(table);
        document.add(new Paragraph("\n"));
    }
    
    // ===== GÉNÉRATION EXCEL (Apache POI) =====
    
    /**
     * Crée un document Excel avec Apache POI.
     */
    private File creerDocumentExcel(String titre, Map<String, Object> donnees, String cheminSortie) throws Exception {
        File fichier = new File(cheminSortie);
        fichier.getParentFile().mkdirs();
        
        Workbook workbook = new XSSFWorkbook();
        
        // Feuille Synthèse
        Sheet synthese = workbook.createSheet("Synthèse");
        ajouterSyntheseExcel(synthese, titre, donnees);
        
        // Feuilles selon le type de rapport
        if (donnees.containsKey("statsPatients")) {
            Sheet patients = workbook.createSheet("Patients");
            ajouterSectionPatientsExcel(patients, donnees);
        }
        
        if (donnees.containsKey("statsConsultations")) {
            Sheet consultations = workbook.createSheet("Consultations");
            ajouterSectionConsultationsExcel(consultations, donnees);
        }
        
        if (donnees.containsKey("maladiesFrequentes")) {
            Sheet maladies = workbook.createSheet("Maladies");
            ajouterSectionMaladiesExcel(maladies, donnees);
        }
        
        // Écrire le fichier
        try (FileOutputStream outputStream = new FileOutputStream(fichier)) {
            workbook.write(outputStream);
        }
        
        workbook.close();
        
        return fichier;
    }
    
    /**
     * Ajoute la feuille synthèse.
     */
    private void ajouterSyntheseExcel(Sheet sheet, String titre, Map<String, Object> donnees) {
        Row row0 = sheet.createRow(0);
        row0.createCell(0).setCellValue("RAPPORT: " + titre);
        
        Row row1 = sheet.createRow(1);
        row1.createCell(0).setCellValue("Période: " + donnees.get("periode"));
        
        Row row2 = sheet.createRow(2);
        row2.createCell(0).setCellValue("Généré le: " + LocalDate.now().format(DATE_FORMATTER));
    }
    
    /**
     * Ajoute la section patients à Excel.
     */
    private void ajouterSectionPatientsExcel(Sheet sheet, Map<String, Object> donnees) {
        StatistiquesPatients stats = (StatistiquesPatients) donnees.get("statsPatients");
        
        Row row0 = sheet.createRow(0);
        row0.createCell(0).setCellValue("STATISTIQUES PATIENTS");
        
        int rowNum = 2;
        Row row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue("Nombre total");
        row.createCell(1).setCellValue(stats.getNbTotal());
        
        row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue("Âge moyen");
        row.createCell(1).setCellValue(stats.getMoyenneAge());
    }
    
    /**
     * Ajoute la section consultations à Excel.
     */
    private void ajouterSectionConsultationsExcel(Sheet sheet, Map<String, Object> donnees) {
        StatistiquesConsultations stats = (StatistiquesConsultations) donnees.get("statsConsultations");
        
        Row row0 = sheet.createRow(0);
        row0.createCell(0).setCellValue("STATISTIQUES CONSULTATIONS");
        
        int rowNum = 2;
        Row row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue("Nombre total");
        row.createCell(1).setCellValue(stats.getNbTotal());
        
        row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue("Nombre période");
        row.createCell(1).setCellValue(stats.getNbPeriode());
        
        row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue("Moyenne/jour");
        row.createCell(1).setCellValue(stats.getMoyenneParJour());
    }
    
    /**
     * Ajoute la section maladies à Excel.
     */
    @SuppressWarnings("unchecked")
    private void ajouterSectionMaladiesExcel(Sheet sheet, Map<String, Object> donnees) {
        Row row0 = sheet.createRow(0);
        row0.createCell(0).setCellValue("MALADIES FRÉQUENTES");
        
        Row headerRow = sheet.createRow(2);
        headerRow.createCell(0).setCellValue("Rang");
        headerRow.createCell(1).setCellValue("Diagnostic");
        headerRow.createCell(2).setCellValue("Nombre");
        
        List<RepartitionData> maladies = (List<RepartitionData>) donnees.get("maladiesFrequentes");
        
        int rowNum = 3;
        int rang = 1;
        for (RepartitionData maladie : maladies) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rang++);
            row.createCell(1).setCellValue(maladie.getLabel());
            row.createCell(2).setCellValue(maladie.getValeur().longValue());
        }
    }
    
    // ===== HISTORIQUE =====
    
    /**
     * Sauvegarde l'historique du rapport généré.
     */
    private void sauvegarderHistorique(TypeRapport type, LocalDate debut, LocalDate fin, 
                                      File fichier, FormatRapport format) {
        Rapport rapport = new Rapport(type, debut, fin, format);
        rapport.setNomFichier(fichier.getAbsolutePath());
        rapport.setTailleFichier(fichier.length());
        
        rapportRepository.save(rapport);
    }
}
