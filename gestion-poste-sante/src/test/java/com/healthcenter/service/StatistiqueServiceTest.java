package com.healthcenter.service;

import com.healthcenter.domain.entities.*;
import com.healthcenter.domain.enums.Sexe;
import com.healthcenter.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour StatistiqueService.
 */
@ExtendWith(MockitoExtension.class)
class StatistiqueServiceTest {
    
    @Mock
    private PatientService patientService;
    
    @Mock
    private ConsultationService consultationService;
    
    @Mock
    private MedicamentService medicamentService;
    
    @Mock
    private VaccinationService vaccinationService;
    
    @Mock
    private PersonnelService personnelService;
    
    @InjectMocks
    private StatistiqueService statistiqueService;
    
    private Patient patient1;
    private Patient patient2;
    private Consultation consultation1;
    private Consultation consultation2;
    
    @BeforeEach
    void setUp() {
        // Patients de test
        patient1 = new Patient();
        patient1.setNom("Diallo");
        patient1.setPrenom("Amadou");
        patient1.setSexe(Sexe.HOMME);
        patient1.setDateNaissance(LocalDate.now().minusYears(30));
        
        patient2 = new Patient();
        patient2.setNom("Sow");
        patient2.setPrenom("Fatou");
        patient2.setSexe(Sexe.FEMME);
        patient2.setDateNaissance(LocalDate.now().minusYears(25));
        
        // Consultations de test
        consultation1 = new Consultation();
        consultation1.setPatient(patient1);
        consultation1.setDateConsultation(LocalDate.now().minusDays(5).atStartOfDay());
        consultation1.setDiagnostic("Paludisme");
        
        consultation2 = new Consultation();
        consultation2.setPatient(patient2);
        consultation2.setDateConsultation(LocalDate.now().minusDays(2).atStartOfDay());
        consultation2.setDiagnostic("Grippe");
    }
    
    @Test
    void testObtenirDashboardStats() {
        // Arrange
        when(patientService.obtenirTousLesPatients()).thenReturn(Arrays.asList(patient1, patient2));
        when(consultationService.obtenirToutesLesConsultations()).thenReturn(Arrays.asList(consultation1, consultation2));
        when(vaccinationService.obtenirToutesVaccinations()).thenReturn(Arrays.asList());
        when(medicamentService.obtenirTousMedicaments()).thenReturn(Arrays.asList());
        when(personnelService.obtenirPersonnelActif()).thenReturn(Arrays.asList());
        
        // Act
        DashboardStats stats = statistiqueService.obtenirDashboardStats();
        
        // Assert
        assertNotNull(stats);
        assertEquals(2L, stats.getNbTotalPatients());
        assertEquals(2L, stats.getNbTotalConsultations());
        assertNotNull(stats.getDateGeneration());
        
        verify(patientService, atLeastOnce()).obtenirTousLesPatients();
        verify(consultationService).obtenirToutesLesConsultations();
    }
    
    @Test
    void testObtenirRepartitionPatientsSexe() {
        // Arrange
        when(patientService.obtenirTousLesPatients()).thenReturn(Arrays.asList(patient1, patient2));
        
        // Act
        List<RepartitionData> repartition = statistiqueService.obtenirRepartitionPatientsSexe();
        
        // Assert
        assertNotNull(repartition);
        assertEquals(2, repartition.size());
        
        // Vérifier que les deux sexes sont présents
        boolean hommePresent = repartition.stream().anyMatch(d -> d.getLabel().equals("HOMME"));
        boolean femmePresent = repartition.stream().anyMatch(d -> d.getLabel().equals("FEMME"));
        
        assertTrue(hommePresent);
        assertTrue(femmePresent);
        
        verify(patientService).obtenirTousLesPatients();
    }
    
    @Test
    void testObtenirRepartitionPatientsAge() {
        // Arrange
        when(patientService.obtenirTousLesPatients()).thenReturn(Arrays.asList(patient1, patient2));
        
        // Act
        List<RepartitionData> repartition = statistiqueService.obtenirRepartitionPatientsAge();
        
        // Assert
        assertNotNull(repartition);
        assertEquals(5, repartition.size()); // 5 tranches d'âge
        
        // Vérifier les tranches
        List<String> tranches = repartition.stream().map(RepartitionData::getLabel).toList();
        assertTrue(tranches.contains("0-5 ans"));
        assertTrue(tranches.contains("19-40 ans"));
        
        verify(patientService).obtenirTousLesPatients();
    }
    
    @Test
    void testObtenirEvolutionConsultations() {
        // Arrange
        LocalDate debut = LocalDate.now().minusDays(10);
        LocalDate fin = LocalDate.now();
        when(consultationService.obtenirToutesLesConsultations()).thenReturn(Arrays.asList(consultation1, consultation2));
        
        // Act
        List<EvolutionData> evolution = statistiqueService.obtenirEvolutionConsultations(debut, fin, "JOUR");
        
        // Assert
        assertNotNull(evolution);
        assertFalse(evolution.isEmpty());
        
        verify(consultationService).obtenirToutesLesConsultations();
    }
    
    @Test
    void testObtenirMaladiesFrequentes() {
        // Arrange
        LocalDate debut = LocalDate.now().minusMonths(1);
        LocalDate fin = LocalDate.now();
        when(consultationService.obtenirToutesLesConsultations()).thenReturn(Arrays.asList(consultation1, consultation2));
        
        // Act
        List<RepartitionData> maladies = statistiqueService.obtenirMaladiesFrequentes(debut, fin, 10);
        
        // Assert
        assertNotNull(maladies);
        assertEquals(2, maladies.size());
        
        // Vérifier les diagnostics
        List<String> diagnostics = maladies.stream().map(RepartitionData::getLabel).toList();
        assertTrue(diagnostics.contains("Paludisme"));
        assertTrue(diagnostics.contains("Grippe"));
        
        verify(consultationService).obtenirToutesLesConsultations();
    }
    
    @Test
    void testObtenirCouvertureVaccinale() {
        // Arrange
        when(vaccinationService.obtenirToutesVaccinations()).thenReturn(Arrays.asList());
        when(patientService.obtenirTousLesPatients()).thenReturn(Arrays.asList(patient1, patient2));
        
        // Act
        List<RepartitionData> couverture = statistiqueService.obtenirCouvertureVaccinale();
        
        // Assert
        assertNotNull(couverture);
        // Avec zéro vaccinations, la liste devrait être vide
        assertTrue(couverture.isEmpty());
        
        verify(vaccinationService).obtenirToutesVaccinations();
        verify(patientService).obtenirTousLesPatients();
    }
    
    @Test
    void testObtenirStatistiquesPatients() {
        // Arrange
        LocalDate debut = LocalDate.now().minusMonths(3);
        LocalDate fin = LocalDate.now();
        when(patientService.obtenirTousLesPatients()).thenReturn(Arrays.asList(patient1, patient2));
        
        // Act
        StatistiquesPatients stats = statistiqueService.obtenirStatistiquesPatients(debut, fin);
        
        // Assert
        assertNotNull(stats);
        assertEquals(2L, stats.getNbTotal());
        assertNotNull(stats.getRepartitionSexe());
        assertNotNull(stats.getRepartitionAge());
        assertNotNull(stats.getMoyenneAge());
        assertTrue(stats.getMoyenneAge() > 0);
        
        verify(patientService, atLeastOnce()).obtenirTousLesPatients();
    }
    
    @Test
    void testObtenirStatistiquesConsultations() {
        // Arrange
        LocalDate debut = LocalDate.now().minusMonths(1);
        LocalDate fin = LocalDate.now();
        when(consultationService.obtenirToutesLesConsultations()).thenReturn(Arrays.asList(consultation1, consultation2));
        
        // Act
        StatistiquesConsultations stats = statistiqueService.obtenirStatistiquesConsultations(debut, fin);
        
        // Assert
        assertNotNull(stats);
        assertEquals(2L, stats.getNbTotal());
        assertEquals(2L, stats.getNbPeriode());
        assertNotNull(stats.getEvolutionTemporelle());
        assertNotNull(stats.getMaladiesFrequentes());
        assertTrue(stats.getMoyenneParJour() >= 0);
        
        verify(consultationService, atLeastOnce()).obtenirToutesLesConsultations();
    }
}
