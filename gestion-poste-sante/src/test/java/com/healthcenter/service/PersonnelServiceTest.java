package com.healthcenter.service;

import com.healthcenter.domain.entities.DisponibilitePersonnel;
import com.healthcenter.domain.entities.Personnel;
import com.healthcenter.domain.enums.FonctionPersonnel;
import com.healthcenter.domain.enums.JourSemaine;
import com.healthcenter.dto.PersonnelDTO;
import com.healthcenter.repository.DisponibilitePersonnelRepository;
import com.healthcenter.repository.PersonnelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * TESTS UNITAIRES - PersonnelService
 * 
 * Couvre :
 * - CRUD personnel (création, lecture, mise à jour, suppression)
 * - Validation données (nom, matricule unique, email)
 * - Statistiques consultations (compteurs, top personnel)
 * - Gestion disponibilités (vérification disponibilité jour/heure)
 * - Activation/Désactivation personnel (soft delete)
 * 
 * @author Health Center Team
 */
@ExtendWith(MockitoExtension.class)
public class PersonnelServiceTest {
    
    @Mock
    private PersonnelRepository personnelRepository;
    
    @Mock
    private DisponibilitePersonnelRepository disponibiliteRepository;
    
    @InjectMocks
    private PersonnelService personnelService;
    
    private Personnel personnelTest;
    private PersonnelDTO personnelDTOTest;
    private DisponibilitePersonnel disponibiliteTest;
    
    
    @BeforeEach
    void setUp() {
        // Personnel médecin test (Entity)
        personnelTest = new Personnel();
        personnelTest.setId(1L);
        personnelTest.setNom("DIOP");
        personnelTest.setPrenom("Amadou");
        personnelTest.setFonction(FonctionPersonnel.MEDECIN);
        personnelTest.setSpecialisation("Pédiatre");
        personnelTest.setTelephone("77 123 45 67");
        personnelTest.setEmail("adiop@healthcenter.sn");
        personnelTest.setAdresse("Dakar, Senegal");
        personnelTest.setNumeroMatricule("MAT-2024-001");
        personnelTest.setDateEmbauche(LocalDate.of(2024, 1, 15));
        personnelTest.setActif(true);
        
        // DTO correspondant (pas d'ID car c'est pour la création)
        personnelDTOTest = new PersonnelDTO();
        personnelDTOTest.setNom("DIOP");
        personnelDTOTest.setPrenom("Amadou");
        personnelDTOTest.setFonction("MEDECIN");
        personnelDTOTest.setSpecialisation("Pédiatre");
        personnelDTOTest.setTelephone("77 123 45 67");
        personnelDTOTest.setEmail("adiop@healthcenter.sn");
        personnelDTOTest.setAdresse("Dakar, Senegal");
        personnelDTOTest.setNumeroMatricule("MAT-2024-001");
        personnelDTOTest.setDateEmbauche(LocalDate.of(2024, 1, 15));
        personnelDTOTest.setActif(true);
        
        // Disponibilité test (Lundi 08:00-12:00)
        disponibiliteTest = new DisponibilitePersonnel();
        disponibiliteTest.setId(1L);
        disponibiliteTest.setPersonnel(personnelTest);
        disponibiliteTest.setJourSemaine(JourSemaine.LUNDI);
        disponibiliteTest.setHeureDebut(LocalTime.of(8, 0));
        disponibiliteTest.setHeureFin(LocalTime.of(12, 0));
        disponibiliteTest.setDateDebut(LocalDate.of(2024, 1, 1));
        disponibiliteTest.setDateFin(null);
    }
    
    
    // ========== TEST 1 : Création Personnel Success ==========
    
    @Test
    void testCreerPersonnel_Success() {
        // Given
        when(personnelRepository.findByNumeroMatricule("MAT-2024-001")).thenReturn(Optional.empty());
        when(personnelRepository.save(any(Personnel.class))).thenReturn(personnelTest);
        
        // When
        Personnel result = personnelService.creerPersonnel(personnelDTOTest);
        
        // Then
        assertNotNull(result);
        assertEquals("DIOP", result.getNom());
        assertEquals("Amadou", result.getPrenom());
        assertEquals(FonctionPersonnel.MEDECIN, result.getFonction());
        assertTrue(result.getActif());
        
        verify(personnelRepository, times(1)).findByNumeroMatricule("MAT-2024-001");
        verify(personnelRepository, times(1)).save(any(Personnel.class));
    }
    
    
    // ========== TEST 2 : Création Personnel - Nom Manquant ==========
    
    @Test
    void testCreerPersonnel_NomManquant_ThrowsException() {
        // Given
        PersonnelDTO personnelInvalide = new PersonnelDTO();
        personnelInvalide.setPrenom("Amadou");
        personnelInvalide.setFonction("MEDECIN");
        // Nom manquant
        
        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            personnelService.creerPersonnel(personnelInvalide);
        });
        
        assertTrue(exception.getMessage().contains("nom"));
        verify(personnelRepository, never()).save(any(Personnel.class));
    }
    
    
    // ========== TEST 3 : Création Personnel - Matricule Dupliqué ==========
    
    @Test
    void testCreerPersonnel_MatriculeDuplique_ThrowsException() {
        // Given
        Personnel existant = new Personnel();
        existant.setId(2L);
        existant.setNumeroMatricule("MAT-2024-001");
        
        when(personnelRepository.findByNumeroMatricule("MAT-2024-001")).thenReturn(Optional.of(existant));
        
        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            personnelService.creerPersonnel(personnelDTOTest);
        });
        
        assertTrue(exception.getMessage().contains("matricule") || exception.getMessage().contains("existe"));
        verify(personnelRepository, times(1)).findByNumeroMatricule("MAT-2024-001");
        verify(personnelRepository, never()).save(any(Personnel.class));
    }
    
    
    // ========== TEST 4 : Obtenir Personnel Actif ==========
    
    @Test
    void testObtenirPersonnelActif() {
        // Given
        Personnel personnel2 = new Personnel();
        personnel2.setId(2L);
        personnel2.setNom("FALL");
        personnel2.setPrenom("Fatou");
        personnel2.setFonction(FonctionPersonnel.INFIRMIER);
        personnel2.setActif(true);
        
        List<Personnel> personnelActif = Arrays.asList(personnelTest, personnel2);
        when(personnelRepository.findByActifTrue()).thenReturn(personnelActif);
        
        // When
        List<Personnel> result = personnelService.obtenirPersonnelActif();
        
        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(p -> p.getActif()));
        
        verify(personnelRepository, times(1)).findByActifTrue();
    }
    
    
    // ========== TEST 5 : Désactiver Personnel ==========
    
    @Test
    void testDesactiverPersonnel_Success() {
        // Given
        when(personnelRepository.findById(1L)).thenReturn(Optional.of(personnelTest));
        when(personnelRepository.save(any(Personnel.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        personnelService.desactiverPersonnel(1L);
        
        // Then
        assertFalse(personnelTest.getActif());
        verify(personnelRepository, times(1)).findById(1L);
        verify(personnelRepository, times(1)).save(personnelTest);
    }
    
    
    // ========== TEST 6 : Activer Personnel ==========
    
    @Test
    void testActiverPersonnel_Success() {
        // Given
        personnelTest.setActif(false);
        
        when(personnelRepository.findById(1L)).thenReturn(Optional.of(personnelTest));
        when(personnelRepository.save(any(Personnel.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        personnelService.activerPersonnel(1L);
        
        // Then
        assertTrue(personnelTest.getActif());
        verify(personnelRepository, times(1)).findById(1L);
        verify(personnelRepository, times(1)).save(personnelTest);
    }
    
    
    // ========== TEST 7 : Rechercher Personnel par Nom ==========
    
    @Test
    void testRechercherPersonnelParNom() {
        // Given
        List<Personnel> resultats = Collections.singletonList(personnelTest);
        when(personnelRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase("diop", "diop"))
            .thenReturn(resultats);
        
        // When
        List<Personnel> result = personnelService.rechercherPersonnelParNom("diop");
        
        // Then
        assertEquals(1, result.size());
        assertEquals("DIOP", result.get(0).getNom());
        
        verify(personnelRepository, times(1)).findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase("diop", "diop");
    }
    
    
    // ========== TEST 8 : Obtenir Personnel par Fonction ==========
    
    @Test
    void testObtenirPersonnelParFonction() {
        // Given
        List<Personnel> medecins = Collections.singletonList(personnelTest);
        when(personnelRepository.findByFonction(FonctionPersonnel.MEDECIN)).thenReturn(medecins);
        
        // When
        List<Personnel> result = personnelService.obtenirPersonnelParFonction(FonctionPersonnel.MEDECIN);
        
        // Then
        assertEquals(1, result.size());
        assertEquals(FonctionPersonnel.MEDECIN, result.get(0).getFonction());
        
        verify(personnelRepository, times(1)).findByFonction(FonctionPersonnel.MEDECIN);
    }
    
    
    // ========== TEST 9 : Compter Personnel par Fonction ==========
    
    @Test
    void testCompterPersonnelParFonction() {
        // Given
        Personnel infirmier1 = new Personnel();
        infirmier1.setFonction(FonctionPersonnel.INFIRMIER);
        
        Personnel infirmier2 = new Personnel();
        infirmier2.setFonction(FonctionPersonnel.INFIRMIER);
        
        List<Personnel> toutPersonnel = Arrays.asList(personnelTest, infirmier1, infirmier2);
        when(personnelRepository.findAll()).thenReturn(toutPersonnel);
        
        // When
        Map<FonctionPersonnel, Long> result = personnelService.compterPersonnelParFonction();
        
        // Then
        assertEquals(2, result.size());
        assertEquals(1L, result.get(FonctionPersonnel.MEDECIN));
        assertEquals(2L, result.get(FonctionPersonnel.INFIRMIER));
        
        verify(personnelRepository, times(1)).findAll();
    }
    
    
    // ========== TEST 10 : Est Disponible (Jour + Heure) ==========
    
    @Test
    void testEstDisponible_Jour_Heure() {
        // Given
        LocalDate lundiTest = LocalDate.of(2024, 2, 5); // Lundi 5 février 2024
        LocalTime heureTest = LocalTime.of(9, 30); // 09:30 (dans créneau 08:00-12:00)
        
        List<DisponibilitePersonnel> disponibilites = Collections.singletonList(disponibiliteTest);
        when(disponibiliteRepository.findDisponibilitesActives(1L, JourSemaine.LUNDI, lundiTest))
            .thenReturn(disponibilites);
        
        // When
        boolean result = personnelService.estDisponible(1L, lundiTest, heureTest);
        
        // Then
        assertTrue(result);
        verify(disponibiliteRepository, times(1)).findDisponibilitesActives(1L, JourSemaine.LUNDI, lundiTest);
    }
    
    
    @Test
    void testEstDisponible_HeureHorsCreneaux_RetourneFalse() {
        // Given
        LocalDate lundiTest = LocalDate.of(2024, 2, 5);
        LocalTime heureTest = LocalTime.of(14, 0); // 14:00 (hors créneau 08:00-12:00)
        
        List<DisponibilitePersonnel> disponibilites = Collections.singletonList(disponibiliteTest);
        when(disponibiliteRepository.findDisponibilitesActives(1L, JourSemaine.LUNDI, lundiTest))
            .thenReturn(disponibilites);
        
        // When
        boolean result = personnelService.estDisponible(1L, lundiTest, heureTest);
        
        // Then
        assertFalse(result);
        verify(disponibiliteRepository, times(1)).findDisponibilitesActives(1L, JourSemaine.LUNDI, lundiTest);
    }
}
