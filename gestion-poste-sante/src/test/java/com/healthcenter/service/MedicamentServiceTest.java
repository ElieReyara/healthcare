package com.healthcenter.service;

import com.healthcenter.domain.entities.Medicament;
import com.healthcenter.domain.entities.MouvementStock;
import com.healthcenter.domain.enums.FormeMedicament;
import com.healthcenter.domain.enums.TypeMouvement;
import com.healthcenter.dto.MedicamentDTO;
import com.healthcenter.repository.MedicamentRepository;
import com.healthcenter.repository.MouvementStockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * TESTS UNITAIRES = MedicamentService.
 * 
 * Tests critiques pour la gestion des stocks et validations métier.
 */
@ExtendWith(MockitoExtension.class)
class MedicamentServiceTest {
    
    @Mock
    private MedicamentRepository medicamentRepository;
    
    @Mock
    private MouvementStockRepository mouvementStockRepository;
    
    @InjectMocks
    private MedicamentService medicamentService;
    
    private Medicament medicamentTest;
    private MedicamentDTO medicamentDtoTest;
    
    
    @BeforeEach
    void setUp() {
        medicamentTest = new Medicament();
        medicamentTest.setNom("Paracétamol");
        medicamentTest.setDosage("500mg");
        medicamentTest.setForme(FormeMedicament.COMPRIME);
        medicamentTest.setPrix(new BigDecimal("1500.00"));
        medicamentTest.setStockActuel(100);
        medicamentTest.setSeuilAlerte(20);
        
        medicamentDtoTest = new MedicamentDTO();
        medicamentDtoTest.setNom("Paracétamol");
        medicamentDtoTest.setDosage("500mg");
        medicamentDtoTest.setForme("COMPRIME");
        medicamentDtoTest.setPrix(new BigDecimal("1500.00"));
        medicamentDtoTest.setStockActuel(100);
        medicamentDtoTest.setSeuilAlerte(20);
    }
    
    
    // ========== TEST 1 : Création réussie ==========
    
    @Test
    void testCreerMedicament_Success() {
        // Arrange
        when(medicamentRepository.save(any(Medicament.class))).thenReturn(medicamentTest);
        
        // Act
        Medicament result = medicamentService.creerMedicament(medicamentDtoTest);
        
        // Assert
        assertNotNull(result);
        verify(medicamentRepository, times(1)).save(any(Medicament.class));
    }
    
    
    // ========== TEST 2 : Nom manquant ==========
    
    @Test
    void testCreerMedicament_NomManquant_ThrowsException() {
        // Arrange
        medicamentDtoTest.setNom(null);
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> medicamentService.creerMedicament(medicamentDtoTest)
        );
        
        assertTrue(exception.getMessage().contains("nom"));
        verify(medicamentRepository, never()).save(any());
    }
    
    
    // ========== TEST 3 : Stock négatif ==========
    
    @Test
    void testCreerMedicament_StockNegatif_ThrowsException() {
        // Arrange
        medicamentDtoTest.setStockActuel(-10);
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> medicamentService.creerMedicament(medicamentDtoTest)
        );
        
        assertTrue(exception.getMessage().contains("stock"));
        verify(medicamentRepository, never()).save(any());
    }
    
    
    // ========== TEST 4 : Ajustement ENTRÉE ==========
    
    @Test
    void testAjusterStock_Entree_Success() {
        // Arrange
        when(medicamentRepository.findById(1L)).thenReturn(Optional.of(medicamentTest));
        when(medicamentRepository.save(any(Medicament.class))).thenReturn(medicamentTest);
        
        ArgumentCaptor<MouvementStock> mouvementCaptor = ArgumentCaptor.forClass(MouvementStock.class);
        
        // Act
        medicamentService.ajusterStock(1L, 50, TypeMouvement.ENTREE, "Réapprovisionnement");
        
        // Assert
        verify(mouvementStockRepository, times(1)).save(mouvementCaptor.capture());
        MouvementStock mouvement = mouvementCaptor.getValue();
        
        assertEquals(TypeMouvement.ENTREE, mouvement.getType());
        assertEquals(50, mouvement.getQuantite());
        assertEquals(100, mouvement.getStockAvant());
        assertEquals(150, mouvement.getStockApres());
        assertEquals("Réapprovisionnement", mouvement.getMotif());
    }
    
    
    // ========== TEST 5 : Ajustement SORTIE ==========
    
    @Test
    void testAjusterStock_Sortie_Success() {
        // Arrange
        when(medicamentRepository.findById(1L)).thenReturn(Optional.of(medicamentTest));
        when(medicamentRepository.save(any(Medicament.class))).thenReturn(medicamentTest);
        
        ArgumentCaptor<MouvementStock> mouvementCaptor = ArgumentCaptor.forClass(MouvementStock.class);
        
        // Act
        medicamentService.ajusterStock(1L, 30, TypeMouvement.SORTIE, "Distribution");
        
        // Assert
        verify(mouvementStockRepository, times(1)).save(mouvementCaptor.capture());
        MouvementStock mouvement = mouvementCaptor.getValue();
        
        assertEquals(TypeMouvement.SORTIE, mouvement.getType());
        assertEquals(30, mouvement.getQuantite());
        assertEquals(100, mouvement.getStockAvant());
        assertEquals(70, mouvement.getStockApres());
    }
    
    
    // ========== TEST 6 : SORTIE avec stock insuffisant (CRITIQUE) ==========
    
    @Test
    void testAjusterStock_Sortie_StockInsuffisant_ThrowsException() {
        // Arrange
        when(medicamentRepository.findById(1L)).thenReturn(Optional.of(medicamentTest));
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> medicamentService.ajusterStock(1L, 200, TypeMouvement.SORTIE, "Distribution")
        );
        
        assertTrue(exception.getMessage().contains("insuffisant"));
        verify(mouvementStockRepository, never()).save(any());
        verify(medicamentRepository, never()).save(any());
    }
    
    
    // ========== TEST 7 : Médicaments en rupture ==========
    
    @Test
    void testObtenirMedicamentsEnRuptureStock() {
        // Arrange
        Medicament medicamentFaible = new Medicament();
        medicamentFaible.setNom("Aspirine");
        medicamentFaible.setStockActuel(5);
        medicamentFaible.setSeuilAlerte(10);
        
        when(medicamentRepository.findMedicamentsEnRuptureStock())
            .thenReturn(Arrays.asList(medicamentFaible));
        
        // Act
        List<Medicament> result = medicamentService.obtenirMedicamentsEnRuptureStock();
        
        // Assert
        assertEquals(1, result.size());
        assertEquals("Aspirine", result.get(0).getNom());
        assertTrue(result.get(0).isStockFaible());
    }
    
    
    // ========== TEST 8 : Suppression avec mouvements (CRITIQUE) ==========
    
    @Test
    void testSupprimerMedicament_AvecMouvements_ThrowsException() {
        // Arrange
        Medicament medicamentAvecId = new Medicament();
        medicamentAvecId.setNom("Test");
        medicamentAvecId.setDosage("100mg");
        medicamentAvecId.setForme(FormeMedicament.COMPRIME);
        medicamentAvecId.setPrix(new BigDecimal("1000"));
        medicamentAvecId.setStockActuel(50);
        
        when(medicamentRepository.findById(1L)).thenReturn(Optional.of(medicamentAvecId));
        
        MouvementStock mouvement = new MouvementStock();
        mouvement.setMedicament(medicamentAvecId);
        mouvement.setQuantite(10);
        
        when(mouvementStockRepository.findByMedicamentIdOrderByDateMouvementDesc(1L))
            .thenReturn(Arrays.asList(mouvement));
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> medicamentService.supprimerMedicament(1L)
        );
        
        assertTrue(exception.getMessage().contains("mouvement"));
        verify(medicamentRepository, never()).delete(any());
        verify(medicamentRepository, never()).deleteById(any());
    }
}
