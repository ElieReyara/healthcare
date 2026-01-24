package com.healthcenter.service;

import com.healthcenter.domain.entities.Patient;
import com.healthcenter.domain.enums.Sexe;
import com.healthcenter.dto.PatientDTO;
import com.healthcenter.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * SERVICE LAYER = Logique métier.
 * 
 * Responsabilités :
 * - Valider les données (business rules)
 * - Orchestrer plusieurs repositories si nécessaire
 * - Convertir DTO ↔ Entity
 * - Gérer les transactions (@Transactional)
 */
@Service  // ← Spring détecte et crée une instance unique (Singleton Pattern)
public class PatientService {
    
    /**
     * INJECTION DE DÉPENDANCES (Dependency Injection - Design Pattern).
     * 
     * @Autowired : Spring injecte automatiquement le PatientRepository.
     * Avantage : Pas de "new PatientRepository()" → testable (on peut injecter un mock).
     */
    @Autowired
    private PatientRepository patientRepository;
    
    
    // ========== CREATE ==========
    
    /**
     * Créer un nouveau patient à partir d'un DTO.
     * 
     * @Transactional : Si erreur, rollback automatique (tout ou rien).
     * @param dto Données venant de l'UI
     * @return Patient créé avec ID généré
     * @throws IllegalArgumentException si validation échoue
     */
    @Transactional
    public Patient creerPatient(PatientDTO dto) {
        // VALIDATION MÉTIER (business rules)
        validerPatientDTO(dto);
        
        // Vérifier unicité du numéro carnet
        if (dto.getNumeroCarnet() != null && 
            patientRepository.findByNumeroCarnet(dto.getNumeroCarnet()).isPresent()) {
            throw new IllegalArgumentException(
                "Le numéro de carnet '" + dto.getNumeroCarnet() + "' existe déjà"
            );
        }
        
        // CONVERSION DTO → Entity
        Patient patient = mapToEntity(dto);
        
        // SAUVEGARDE (JPA génère : INSERT INTO patients ...)
        return patientRepository.save(patient);
    }
    
    
    // ========== READ ==========
    
    /**
     * Récupérer tous les patients.
     * @return Liste complète (peut être vide)
     */
    @Transactional(readOnly = true)  // readOnly = optimisation performance
    public List<Patient> obtenirTousLesPatients() {
        return patientRepository.findAll();
    }
    
    /**
     * Rechercher un patient par ID.
     * @param id Identifiant
     * @return Optional (vide si non trouvé)
     */
    @Transactional(readOnly = true)
    public Optional<Patient> obtenirPatientParId(Long id) {
        return patientRepository.findById(id);
    }
    
    /**
     * Rechercher patients par nom (insensible à la casse).
     * @param nom Nom partiel ou complet
     * @return Liste des patients correspondants
     */
    @Transactional(readOnly = true)
    public List<Patient> rechercherParNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            return List.of();  // Liste vide si nom invalide
        }
        return patientRepository.findByNomContainingIgnoreCase(nom);
    }
    
    /**
     * Rechercher par numéro de carnet (unique).
     */
    @Transactional(readOnly = true)
    public Optional<Patient> rechercherParNumeroCarnet(String numeroCarnet) {
        return patientRepository.findByNumeroCarnet(numeroCarnet);
    }
    
    
    // ========== UPDATE ==========
    
    /**
     * Mettre à jour un patient existant.
     * 
     * @param id ID du patient à modifier
     * @param dto Nouvelles données
     * @return Patient mis à jour
     * @throws IllegalArgumentException si patient inexistant
     */
    @Transactional
    public Patient mettreAJourPatient(Long id, PatientDTO dto) {
        // Vérifier existence
        Patient patient = patientRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "Patient avec ID " + id + " introuvable"
            ));
        
        // Validation
        validerPatientDTO(dto);
        
        // Vérifier unicité carnet (sauf si même patient)
        if (dto.getNumeroCarnet() != null) {
            Optional<Patient> existant = patientRepository.findByNumeroCarnet(dto.getNumeroCarnet());
            if (existant.isPresent() && !existant.get().getId().equals(id)) {
                throw new IllegalArgumentException("Ce numéro de carnet est déjà utilisé");
            }
        }
        
        // Mise à jour des champs
        patient.setNom(dto.getNom());
        patient.setPrenom(dto.getPrenom());
        patient.setDateNaissance(dto.getDateNaissance());
        patient.setSexe(dto.getSexe());
        patient.setTelephone(dto.getTelephone());
        patient.setAdresse(dto.getAdresse());
        patient.setNumeroCarnet(dto.getNumeroCarnet());
        
        // JPA détecte les modifications et génère : UPDATE patients SET ...
        return patientRepository.save(patient);
    }
    
    
    // ========== DELETE ==========
    
    /**
     * Supprimer un patient par ID.
     * ATTENTION : Supprime aussi ses consultations (cascade).
     * 
     * @param id Identifiant
     * @throws IllegalArgumentException si patient inexistant
     */
    @Transactional
    public void supprimerPatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new IllegalArgumentException("Patient avec ID " + id + " introuvable");
        }
        patientRepository.deleteById(id);
    }
    
    
    // ========== STATISTIQUES (Business Logic) ==========
    
    /**
     * Compter patients par sexe.
     * @param sexe HOMME, FEMME, AUTRE
     * @return Nombre de patients
     */
    @Transactional(readOnly = true)
    public long compterParSexe(Sexe sexe) {
        return patientRepository.countBySexe(sexe);
    }
    
    /**
     * Obtenir patients nés après une certaine date.
     */
    @Transactional(readOnly = true)
    public List<Patient> obtenirPatientsNesApres(LocalDate date) {
        return patientRepository.findPatientsNesApres(date);
    }
    
    
    // ========== MÉTHODES PRIVÉES (Helpers) ==========
    
    /**
     * Validation des données métier.
     * @throws IllegalArgumentException si données invalides
     */
    private void validerPatientDTO(PatientDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Les données du patient sont obligatoires");
        }
        
        if (dto.getNom() == null || dto.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom est obligatoire");
        }
        
        if (dto.getPrenom() == null || dto.getPrenom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le prénom est obligatoire");
        }
        
        if (dto.getDateNaissance() == null) {
            throw new IllegalArgumentException("La date de naissance est obligatoire");
        }
        
        // Vérifier que date naissance n'est pas dans le futur
        if (dto.getDateNaissance().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La date de naissance ne peut pas être future");
        }
        
        if (dto.getSexe() == null) {
            throw new IllegalArgumentException("Le sexe est obligatoire");
        }
    }
    
    /**
     * Convertir DTO → Entity.
     * Mapping manuel (alternatives : ModelMapper, MapStruct pour projets larges).
     */
    private Patient mapToEntity(PatientDTO dto) {
        Patient patient = new Patient();
        patient.setNom(dto.getNom());
        patient.setPrenom(dto.getPrenom());
        patient.setDateNaissance(dto.getDateNaissance());
        patient.setSexe(dto.getSexe());
        patient.setTelephone(dto.getTelephone());
        patient.setAdresse(dto.getAdresse());
        patient.setNumeroCarnet(dto.getNumeroCarnet());
        return patient;
    }
    
    /**
     * Convertir Entity → DTO (pour affichage UI sans exposer Entity complète).
     */
    public PatientDTO mapToDTO(Patient patient) {
        PatientDTO dto = new PatientDTO();
        dto.setNom(patient.getNom());
        dto.setPrenom(patient.getPrenom());
        dto.setDateNaissance(patient.getDateNaissance());
        dto.setSexe(patient.getSexe());
        dto.setTelephone(patient.getTelephone());
        dto.setAdresse(patient.getAdresse());
        dto.setNumeroCarnet(patient.getNumeroCarnet());
        return dto;
    }
}
