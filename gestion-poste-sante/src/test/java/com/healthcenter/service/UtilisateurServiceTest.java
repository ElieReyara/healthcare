package com.healthcenter.service;

import com.healthcenter.domain.entities.Utilisateur;
import com.healthcenter.domain.enums.RoleUtilisateur;
import com.healthcenter.repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour UtilisateurService
 */
@ExtendWith(MockitoExtension.class)
public class UtilisateurServiceTest {
    
    @Mock
    private UtilisateurRepository utilisateurRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UtilisateurService utilisateurService;
    
    private Utilisateur utilisateur;
    
    @BeforeEach
    void setUp() {
        utilisateur = new Utilisateur();
        utilisateur.setUsername("testuser");
        utilisateur.setPassword("hashedpassword");
        utilisateur.setNom("Dupont");
        utilisateur.setPrenom("Jean");
        utilisateur.setRole(RoleUtilisateur.MEDECIN);
        utilisateur.setActif(true);
    }
    
    @Test
    void testCreerUtilisateur_PasswordHashe() {
        // Given
        when(utilisateurRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("hashedpassword");
        when(utilisateurRepository.save(any(Utilisateur.class))).thenReturn(utilisateur);
        
        // When
        Utilisateur result = utilisateurService.creerUtilisateur("testuser", "password123", 
            "Dupont", "Jean", RoleUtilisateur.MEDECIN);
        
        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(passwordEncoder).encode("password123");
        verify(utilisateurRepository).save(any(Utilisateur.class));
    }
    
    @Test
    void testCreerUtilisateur_UsernameExistant_ThrowsException() {
        // Given
        when(utilisateurRepository.findByUsername("testuser")).thenReturn(Optional.of(utilisateur));
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            utilisateurService.creerUtilisateur("testuser", "password123", 
                "Dupont", "Jean", RoleUtilisateur.MEDECIN);
        });
    }
    
    @Test
    void testAuthentifier_Success() {
        // Given
        when(utilisateurRepository.findByUsername("testuser")).thenReturn(Optional.of(utilisateur));
        when(passwordEncoder.matches("password123", "hashedpassword")).thenReturn(true);
        when(utilisateurRepository.save(any(Utilisateur.class))).thenReturn(utilisateur);
        
        // When
        Optional<Utilisateur> result = utilisateurService.authentifier("testuser", "password123");
        
        // Then
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        assertNotNull(result.get().getDerniereConnexion());
    }
    
    @Test
    void testAuthentifier_MauvaisPassword_Echoue() {
        // Given
        when(utilisateurRepository.findByUsername("testuser")).thenReturn(Optional.of(utilisateur));
        when(passwordEncoder.matches("wrongpassword", "hashedpassword")).thenReturn(false);
        
        // When
        Optional<Utilisateur> result = utilisateurService.authentifier("testuser", "wrongpassword");
        
        // Then
        assertFalse(result.isPresent());
    }
    
    @Test
    void testAuthentifier_UtilisateurInactif_Echoue() {
        // Given
        utilisateur.setActif(false);
        when(utilisateurRepository.findByUsername("testuser")).thenReturn(Optional.of(utilisateur));
        
        // When
        Optional<Utilisateur> result = utilisateurService.authentifier("testuser", "password123");
        
        // Then
        assertFalse(result.isPresent());
    }
    
    @Test
    void testChangerMotDePasse_Success() {
        // Given
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(utilisateur));
        when(passwordEncoder.matches("oldpass", "hashedpassword")).thenReturn(true);
        when(passwordEncoder.encode("newpass")).thenReturn("newhashedpassword");
        when(utilisateurRepository.save(any(Utilisateur.class))).thenReturn(utilisateur);
        
        // When
        utilisateurService.changerMotDePasse(1L, "oldpass", "newpass");
        
        // Then
        verify(passwordEncoder).encode("newpass");
        verify(utilisateurRepository).save(utilisateur);
    }
    
    @Test
    void testChangerMotDePasse_MauvaisAncienPassword_ThrowsException() {
        // Given
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(utilisateur));
        when(passwordEncoder.matches("wrongoldpass", "hashedpassword")).thenReturn(false);
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            utilisateurService.changerMotDePasse(1L, "wrongoldpass", "newpass");
        });
    }
    
    @Test
    void testDesactiverUtilisateur() {
        // Given
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(utilisateur));
        when(utilisateurRepository.save(any(Utilisateur.class))).thenReturn(utilisateur);
        
        // When
        utilisateurService.desactiverUtilisateur(1L);
        
        // Then
        assertFalse(utilisateur.getActif());
        verify(utilisateurRepository).save(utilisateur);
    }
}
