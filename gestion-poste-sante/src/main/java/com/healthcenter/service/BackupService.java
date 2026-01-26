package com.healthcenter.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de gestion des backups et restauration de la base de données
 */
@Service
public class BackupService {
    
    private static final Logger log = LoggerFactory.getLogger(BackupService.class);
    private static final String BACKUP_DIR = "backups";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmmss");
    
    @Autowired
    private DataSource dataSource;
    
    /**
     * Crée un backup de la base de données
     * @param cheminDestination Dossier de destination (null = dossier par défaut)
     * @return Le fichier de backup créé
     */
    @Transactional
    public File creerBackup(String cheminDestination) {
        try {
            // Créer dossier de backup si n'existe pas
            File backupDir = new File(cheminDestination != null ? cheminDestination : BACKUP_DIR);
            if (!backupDir.exists()) {
                backupDir.mkdirs();
            }
            
            // Nom du fichier avec timestamp
            String nomFichier = "backup-" + LocalDateTime.now().format(FORMATTER) + ".sql";
            File backupFile = new File(backupDir, nomFichier);
            
            // Utiliser H2 SCRIPT TO pour exporter SQL
            try (Connection conn = dataSource.getConnection();
                 Statement stmt = conn.createStatement()) {
                
                String sql = "SCRIPT TO '" + backupFile.getAbsolutePath().replace("\\", "/") + "'";
                stmt.execute(sql);
                
                log.info("Backup créé avec succès: {}", backupFile.getAbsolutePath());
            }
            
            return backupFile;
            
        } catch (Exception e) {
            log.error("Erreur lors de la création du backup", e);
            throw new RuntimeException("Erreur lors de la création du backup: " + e.getMessage(), e);
        }
    }
    
    /**
     * Restaure la base de données depuis un backup
     * @param cheminBackup Chemin du fichier de backup
     */
    @Transactional
    public void restaurerBackup(String cheminBackup) {
        try {
            File backupFile = new File(cheminBackup);
            if (!backupFile.exists()) {
                throw new IllegalArgumentException("Fichier de backup introuvable: " + cheminBackup);
            }
            
            // Utiliser H2 RUNSCRIPT FROM pour importer SQL
            try (Connection conn = dataSource.getConnection();
                 Statement stmt = conn.createStatement()) {
                
                String sql = "RUNSCRIPT FROM '" + backupFile.getAbsolutePath().replace("\\", "/") + "'";
                stmt.execute(sql);
                
                log.info("Backup restauré avec succès: {}", cheminBackup);
            }
            
        } catch (Exception e) {
            log.error("Erreur lors de la restauration du backup", e);
            throw new RuntimeException("Erreur lors de la restauration: " + e.getMessage(), e);
        }
    }
    
    /**
     * Liste tous les fichiers de backup disponibles
     * @param dossierBackups Dossier des backups (null = dossier par défaut)
     * @return Liste des fichiers de backup triés par date décroissante
     */
    public List<File> listerBackups(String dossierBackups) {
        File backupDir = new File(dossierBackups != null ? dossierBackups : BACKUP_DIR);
        
        if (!backupDir.exists() || !backupDir.isDirectory()) {
            return Collections.emptyList();
        }
        
        File[] fichiers = backupDir.listFiles((dir, name) -> name.endsWith(".sql"));
        
        if (fichiers == null || fichiers.length == 0) {
            return Collections.emptyList();
        }
        
        // Trier par date de modification décroissante
        List<File> listeBackups = Arrays.asList(fichiers);
        listeBackups.sort((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
        
        return listeBackups;
    }
    
    /**
     * Supprime un fichier de backup
     * @param backup Le fichier à supprimer
     */
    public void supprimerBackup(File backup) {
        if (backup.exists() && backup.delete()) {
            log.info("Backup supprimé: {}", backup.getName());
        } else {
            log.warn("Impossible de supprimer le backup: {}", backup.getName());
        }
    }
    
    /**
     * Backup automatique planifié
     * Exécuté tous les jours à 2h du matin
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void planifierBackupAutomatique() {
        try {
            log.info("Démarrage du backup automatique planifié");
            creerBackup(null);
            log.info("Backup automatique terminé avec succès");
        } catch (Exception e) {
            log.error("Erreur lors du backup automatique", e);
        }
    }
}
