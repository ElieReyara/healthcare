package com.healthcenter.service;

import com.healthcenter.domain.entities.CalendrierVaccinal;
import com.healthcenter.domain.entities.Patient;
import com.healthcenter.domain.entities.Vaccination;
import com.healthcenter.domain.enums.StatutVaccination;
import com.healthcenter.domain.enums.TypeVaccin;
import com.healthcenter.dto.VaccinationDTO;
import com.healthcenter.repository.CalendrierVaccinalRepository;
import com.healthcenter.repository.PatientRepository;
import com.healthcenter.repository.VaccinationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * TESTS UNITAIRES - VaccinationService.
 * 
 * Scénarios testés :
 * - Création avec validation
 * - Calcul automatique date rappel
 * - Détection rappels proches/en retard
 * - Vaccins manquants selon âge
 * - Statistiques couverture
 */
@ExtendWith(MockitoExtension.class)
class VaccinationServiceTest {
    
    @Mock
    private VaccinationRepository vaccinationRepository;
    
    @Mock
    private PatientRepository patientRepository;
    
    @Mock
    private CalendrierVaccinalRepository calendrierRepository;
    
    @InjectMocks
    private VaccinationService vaccinationService;
    
    private Patient patientTest;
    private CalendrierVaccinal calendrierBCG;
    
    
    @BeforeEach
    void setUp() {
        // Patient de 3 mois (90 jours)
        patientTest = new Patient();
        patientTest.setPrenom("Awa");
        patientTest.setNom("Diop");
        patientTest.setDateNaissance(LocalDate.now().minusDays(90));
        
        // Calendrier BCG (à la naissance, pas de rappel)
        calendrierBCG = new CalendrierVaccinal();
        calendrierBCG.setVaccin(TypeVaccin.BCG);
        calendrierBCG.setAgeRecommande(0);
        calendrierBCG.setNombreRappels(0);
        calendrierBCG.setObligatoire(true);
    }
    
    
    // ========== TEST CRÉATION ==========
    
    @Test
    @DisplayName("Création vaccination - SUCCESS")
    void testCreerVaccination_Success() {
        // Given
        VaccinationDTO dto = new VaccinationDTO();
        dto.setPatientId(1L);
        dto.setVaccin("BCG");
        dto.setDateAdministration(LocalDate.now());
        dto.setNumeroLot("LOT-001");
        
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patientTest));
        when(calendrierRepository.findByVaccin(TypeVaccin.BCG)).thenReturn(Optional.of(calendrierBCG));
        when(vaccinationRepository.save(any(Vaccination.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        Vaccination result = vaccinationService.creerVaccination(dto);
        
        // Then
        assertNotNull(result);
        assertEquals(TypeVaccin.BCG, result.getVaccin());
        assertEquals(StatutVaccination.ADMINISTRE, result.getStatut());
        verify(vaccinationRepository).save(any(Vaccination.class));
    }
    
    @Test
    @DisplayName("Création vaccination - Patient inexistant - Exception")
    void testCreerVaccination_PatientInexistant_ThrowsException() {
        // Given
        VaccinationDTO dto = new VaccinationDTO();
        dto.setPatientId(999L);
        dto.setVaccin("BCG");
        dto.setDateAdministration(LocalDate.now());
        
        when(patientRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            vaccinationService.creerVaccination(dto);
        });
        
        verify(vaccinationRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Création vaccination - Date future - Exception")
    void testCreerVaccination_DateFuture_ThrowsException() {
        // Given
        VaccinationDTO dto = new VaccinationDTO();
        dto.setPatientId(1L);
        dto.setVaccin("BCG");
        dto.setDateAdministration(LocalDate.now().plusDays(10)); // Future!
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            vaccinationService.creerVaccination(dto);
        });
    }
    
    
    // ========== TEST CALCUL RAPPEL AUTOMATIQUE ==========
    
    @Test
    @DisplayName("Création avec calendrier - Calcule rappel automatique")
    void testCreerVaccination_AvecCalendrier_CalculeRappelAutomatique() {
        // Given
        CalendrierVaccinal calendrierPenta = new CalendrierVaccinal();
        calendrierPenta.setVaccin(TypeVaccin.PENTA_1);
        calendrierPenta.setAgeRecommande(42);
        calendrierPenta.setNombreRappels(1);
        calendrierPenta.setDelaiRappel(28); // Rappel à 28 jours
        
        VaccinationDTO dto = new VaccinationDTO();
        dto.setPatientId(1L);
        dto.setVaccin("PENTA_1");
        dto.setDateAdministration(LocalDate.now());
        dto.setDateRappel(null); // Calcul auto!
        
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patientTest));
        when(calendrierRepository.findByVaccin(TypeVaccin.PENTA_1)).thenReturn(Optional.of(calendrierPenta));
        when(vaccinationRepository.save(any(Vaccination.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        Vaccination result = vaccinationService.creerVaccination(dto);
        
        // Then
        assertNotNull(result.getDateRappel());
        assertEquals(LocalDate.now().plusDays(28), result.getDateRappel());
        assertEquals(StatutVaccination.RAPPEL_PREVU, result.getStatut());
    }
    
    
    // ========== TEST RAPPELS PROCHES ==========
    
    @Test
    @DisplayName("Obtenir rappels prochains - Dans 7 jours")
    void testObtenirRappelsProchains() {
        // Given
        LocalDate dateRappelProche = LocalDate.now().plusDays(5);
        
        Vaccination vaccination = new Vaccination();
        vaccination.setId(1L);
        vaccination.setPatient(patientTest);
        vaccination.setVaccin(TypeVaccin.PENTA_1);
        vaccination.setDateAdministration(LocalDate.now().minusDays(23));
        vaccination.setDateRappel(dateRappelProche);
        vaccination.setStatut(StatutVaccination.RAPPEL_PREVU);
        
        when(vaccinationRepository.findRappelsProchains(any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(List.of(vaccination));
        
        // When
        List<Vaccination> rappels = vaccinationService.obtenirRappelsProchains(7);
        
        // Then
        assertEquals(1, rappels.size());
        assertEquals(TypeVaccin.PENTA_1, rappels.get(0).getVaccin());
    }
    
    
    // ========== TEST RAPPELS EN RETARD ==========
    
    @Test
    @DisplayName("Obtenir rappels en retard")
    void testObtenirRappelsEnRetard() {
        // Given
        LocalDate dateRappelPassee = LocalDate.now().minusDays(10);
        
        Vaccination vaccination = new Vaccination();
        vaccination.setId(1L);
        vaccination.setPatient(patientTest);
        vaccination.setVaccin(TypeVaccin.PENTA_1);
        vaccination.setDateAdministration(LocalDate.now().minusDays(40));
        vaccination.setDateRappel(dateRappelPassee);
        vaccination.setStatut(StatutVaccination.RAPPEL_EN_RETARD);
        
        when(vaccinationRepository.findRappelsEnRetard(any(LocalDate.class)))
            .thenReturn(List.of(vaccination));
        
        // When
        List<Vaccination> rappels = vaccinationService.obtenirRappelsEnRetard();
        
        // Then
        assertEquals(1, rappels.size());
        assertTrue(rappels.get(0).getDateRappel().isBefore(LocalDate.now()));
    }
    
    
    // ========== TEST CARNET VACCINAL ==========
    
    @Test
    @DisplayName("Obtenir carnet vaccinal - Groupé par vaccin")
    void testObtenirCarnetVaccinal() {
        // Given
        Vaccination bcg = new Vaccination();
        bcg.setVaccin(TypeVaccin.BCG);
        bcg.setDateAdministration(LocalDate.now().minusDays(90));
        
        Vaccination penta1 = new Vaccination();
        penta1.setVaccin(TypeVaccin.PENTA_1);
        penta1.setDateAdministration(LocalDate.now().minusDays(48));
        
        when(vaccinationRepository.findByPatientIdOrderByDateAdministrationDesc(1L))
            .thenReturn(List.of(penta1, bcg));
        
        // When
        Map<TypeVaccin, List<Vaccination>> carnet = vaccinationService.obtenirCarnetVaccinal(1L);
        
        // Then
        assertEquals(2, carnet.size());
        assertTrue(carnet.containsKey(TypeVaccin.BCG));
        assertTrue(carnet.containsKey(TypeVaccin.PENTA_1));
        assertEquals(1, carnet.get(TypeVaccin.BCG).size());
    }
    
    
    // ========== TEST VACCINS MANQUANTS ==========
    
    @Test
    @DisplayName("Vérifier vaccins manquants - Selon âge patient")
    void testVerifierVaccinationsManquantes() {
        // Given : Patient 90 jours, calendrier BCG (0 jours) + PENTA_1 (42 jours)
        CalendrierVaccinal calendrierPenta = new CalendrierVaccinal();
        calendrierPenta.setVaccin(TypeVaccin.PENTA_1);
        calendrierPenta.setAgeRecommande(42);
        calendrierPenta.setObligatoire(true);
        
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patientTest));
        when(calendrierRepository.findByObligatoireTrue())
            .thenReturn(List.of(calendrierBCG, calendrierPenta));
        when(vaccinationRepository.findByPatientId(1L))
            .thenReturn(Collections.emptyList()); // Aucun vaccin fait
        
        // When
        List<TypeVaccin> manquants = vaccinationService.verifierVaccinationsManquantes(1L);
        
        // Then
        assertEquals(2, manquants.size());
        assertTrue(manquants.contains(TypeVaccin.BCG));
        assertTrue(manquants.contains(TypeVaccin.PENTA_1));
    }
    
    
    // ========== TEST TAUX COUVERTURE ==========
    
    @Test
    @DisplayName("Calculer taux couverture - Pourcentage")
    void testCalculerTauxCouverture() {
        // Given : 75 patients différents vaccinés BCG sur 100 patients totaux
        List<Vaccination> vaccinations = new ArrayList<>();
        for (long i = 1; i <= 75; i++) {
            Patient p = new Patient();
            p.setPrenom("Patient" + i);
            p.setNom("Test");
            p.setDateNaissance(LocalDate.now().minusYears(1));
            // Utiliser un field reflection pour setter l'id
            try {
                java.lang.reflect.Field idField = Patient.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(p, i);
            } catch (Exception e) {
                // Ignore
            }
            
            Vaccination v = new Vaccination();
            v.setPatient(p);
            v.setVaccin(TypeVaccin.BCG);
            v.setDateAdministration(LocalDate.now());
            vaccinations.add(v);
        }
        
        when(vaccinationRepository.findByVaccin(TypeVaccin.BCG)).thenReturn(vaccinations);
        when(patientRepository.count()).thenReturn(100L);
        
        // When
        double taux = vaccinationService.calculerTauxCouverture(TypeVaccin.BCG);
        
        // Then
        assertEquals(75.0, taux, 0.01);
    }
    
    
    // ========== TEST MISE À JOUR STATUTS ==========
    
    @Test
    @DisplayName("Mettre à jour statuts - RAPPEL_EN_RETARD si date passée")
    void testMettreAJourStatuts_RappelEnRetard() {
        // Given
        Vaccination vaccination = new Vaccination();
        vaccination.setId(1L);
        vaccination.setDateAdministration(LocalDate.now().minusDays(40));
        vaccination.setDateRappel(LocalDate.now().minusDays(5)); // Date passée!
        vaccination.setStatut(StatutVaccination.RAPPEL_PREVU);
        
        when(vaccinationRepository.findAll()).thenReturn(List.of(vaccination));
        when(vaccinationRepository.save(any(Vaccination.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        vaccinationService.mettreAJourStatuts();
        
        // Then
        verify(vaccinationRepository).save(argThat(v -> 
            v.getStatut() == StatutVaccination.RAPPEL_EN_RETARD
        ));
    }
}
