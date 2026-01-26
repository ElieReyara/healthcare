package com.healthcenter.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour BackupService
 * Utilise @TempDir pour créer des fichiers temporaires
 */
@ExtendWith(MockitoExtension.class)
public class BackupServiceTest {
    
    @Mock
    private DataSource dataSource;
    
    @Mock
    private Connection connection;
    
    @Mock
    private Statement statement;
    
    @InjectMocks
    private BackupService backupService;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() throws Exception {
        lenient().when(dataSource.getConnection()).thenReturn(connection);
        lenient().when(connection.createStatement()).thenReturn(statement);
    }
    
    @Test
    void testCreerBackup_Success() throws Exception {
        // Given
        String cheminBackup = tempDir.toString();
        when(statement.execute(anyString())).thenReturn(true);
        
        // When
        File backupFile = backupService.creerBackup(cheminBackup);
        
        // Then
        assertNotNull(backupFile);
        assertTrue(backupFile.getName().startsWith("backup-"));
        assertTrue(backupFile.getName().endsWith(".sql"));
        verify(statement).execute(contains("SCRIPT TO"));
    }
    
    @Test
    void testListerBackups() throws Exception {
        // Given
        String cheminBackup = tempDir.toString();
        
        // Créer quelques fichiers de backup de test
        File backup1 = new File(tempDir.toFile(), "backup-2026-01-26-100000.sql");
        File backup2 = new File(tempDir.toFile(), "backup-2026-01-25-100000.sql");
        backup1.createNewFile();
        backup2.createNewFile();
        
        // When
        List<File> backups = backupService.listerBackups(cheminBackup);
        
        // Then
        assertEquals(2, backups.size());
        // Vérifie tri par date décroissante (backup1 plus récent)
        assertTrue(backups.get(0).lastModified() >= backups.get(1).lastModified());
    }
    
    @Test
    void testRestaurerBackup_Success() throws Exception {
        // Given
        File backupFile = new File(tempDir.toFile(), "backup-test.sql");
        backupFile.createNewFile();
        when(statement.execute(anyString())).thenReturn(true);
        
        // When
        backupService.restaurerBackup(backupFile.getAbsolutePath());
        
        // Then
        verify(statement).execute(contains("RUNSCRIPT FROM"));
    }
    
    @Test
    void testRestaurerBackup_FichierInexistant_ThrowsException() {
        // Given
        String cheminInexistant = tempDir.toString() + "/inexistant.sql";
        
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            backupService.restaurerBackup(cheminInexistant);
        });
    }
    
    @Test
    void testSupprimerBackup() throws Exception {
        // Given
        File backupFile = new File(tempDir.toFile(), "backup-test.sql");
        backupFile.createNewFile();
        assertTrue(backupFile.exists());
        
        // When
        backupService.supprimerBackup(backupFile);
        
        // Then
        assertFalse(backupFile.exists());
    }
}
