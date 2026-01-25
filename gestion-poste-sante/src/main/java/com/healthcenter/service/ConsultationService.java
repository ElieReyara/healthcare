package com.healthcenter.service;

import com.healthcenter.domain.entities.Consultation;
import com.healthcenter.domain.entities.Patient;
import com.healthcenter.dto.ConsultationDTO;
import com.healthcenter.repository.ConsultationRepository;
import com.healthcenter.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * SERVICE LAYER = Logique métier pour Consultation.
 * 
 * Responsabilités :
 * - Valider les données consultation (business rules)
 * - Orchestrer ConsultationRepository + PatientRepository
 * - Convertir DTO ↔ Entity
 * - Gérer les transactions (@Transactional)
 * 
 * Règles métier Consultation :
 * - Patient : OBLIGATOIRE (doit exister en DB)
 * - DateConsultation : OBLIGATOIRE, <= maintenant
 * - Au moins symptomes OU diagnostic renseigné
 */
@Service
public class ConsultationService {
    
    /**
     * INJECTION DE DÉPENDANCES (Dependency Injection).
     * 
     * @Autowired : Spring injecte automatiquement les repositories.
     * Avantage : Pas de "new" → testable (on peut injecter des mocks).
     */
    @Autowired
    private ConsultationRepository consultationRepository;
    
    @Autowired
    private PatientRepository patientRepository;
    
    
    // ========== CREATE ==========
    
    /**
     * Créer une nouvelle consultation à partir d'un DTO.
     * 
     * @Transactional : Si erreur, rollback automatique (tout ou rien).
     * @param dto Données venant de l'UI
     * @return Consultation créée avec ID généré
     * @throws IllegalArgumentException si validation échoue
     */
    @Transactional
    public Consultation creerConsultation(ConsultationDTO dto) {
        // VALIDATION MÉTIER
        validerConsultationDTO(dto);
        
        // Vérifier que le patient existe
        Patient patient = patientRepository.findById(dto.getPatientId())
            .orElseThrow(() -> new IllegalArgumentException(
                "Patient avec ID " + dto.getPatientId() + " introuvable"
            ));
        
        // CONVERSION DTO → Entity
        Consultation consultation = mapToEntity(dto, patient);
        
        // SAUVEGARDE (JPA génère : INSERT INTO consultations ...)
        return consultationRepository.save(consultation);
    }
    
    
    // ========== READ ==========
    
    /**
     * Récupérer toutes les consultations.
     * @return Liste complète (peut être vide)
     */
    @Transactional(readOnly = true)
    public List<Consultation> obtenirToutesLesConsultations() {
        return consultationRepository.findAll();
    }
    
    /**
     * Rechercher une consultation par ID.
     * @param id Identifiant
     * @return Optional (vide si non trouvée)
     */
    @Transactional(readOnly = true)
    public Optional<Consultation> obtenirConsultationParId(Long id) {
        return consultationRepository.findById(id);
    }
    
    /**
     * Rechercher consultations d'un patient (triées par date décroissante).
     * Utile pour afficher historique médical (plus récentes en premier).
     * 
     * @param patientId ID du patient
     * @return Liste triée par date décroissante
     */
    @Transactional(readOnly = true)
    public List<Consultation> obtenirConsultationsParPatient(Long patientId) {
        return consultationRepository.findByPatientIdOrderByDateConsultationDesc(patientId);
    }
    
    /**
     * Rechercher consultations dans un intervalle de dates.
     * Utile pour rapports mensuels/annuels.
     * 
     * @param debut Date/heure début (inclusive)
     * @param fin Date/heure fin (inclusive)
     * @return Liste des consultations dans l'intervalle
     */
    @Transactional(readOnly = true)
    public List<Consultation> obtenirConsultationsEntreDates(LocalDateTime debut, LocalDateTime fin) {
        if (debut == null || fin == null) {
            throw new IllegalArgumentException("Les dates de début et fin sont obligatoires");
        }
        
        if (debut.isAfter(fin)) {
            throw new IllegalArgumentException("La date de début doit être antérieure à la date de fin");
        }
        
        return consultationRepository.findByDateConsultationBetween(debut, fin);
    }
    
    
    // ========== UPDATE ==========
    
    /**
     * Mettre à jour une consultation existante.
     * 
     * @param id ID de la consultation à modifier
     * @param dto Nouvelles données
     * @return Consultation mise à jour
     * @throws IllegalArgumentException si consultation inexistante
     */
    @Transactional
    public Consultation mettreAJourConsultation(Long id, ConsultationDTO dto) {
        // Vérifier existence consultation
        Consultation consultation = consultationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "Consultation avec ID " + id + " introuvable"
            ));
        
        // VALIDATION
        validerConsultationDTO(dto);
        
        // Vérifier que le patient existe (si changement de patient)
        Patient patient = patientRepository.findById(dto.getPatientId())
            .orElseThrow(() -> new IllegalArgumentException(
                "Patient avec ID " + dto.getPatientId() + " introuvable"
            ));
        
        // Mise à jour des champs
        consultation.setPatient(patient);
        consultation.setDateConsultation(dto.getDateConsultation());
        consultation.setSymptomes(dto.getSymptomes());
        consultation.setDiagnostic(dto.getDiagnostic());
        consultation.setPrescription(dto.getPrescription());
        
        // JPA détecte les modifications et génère : UPDATE consultations SET ...
        return consultationRepository.save(consultation);
    }
    
    
    // ========== DELETE ==========
    
    /**
     * Supprimer une consultation par ID.
     * 
     * @param id Identifiant
     * @throws IllegalArgumentException si consultation inexistante
     */
    @Transactional
    public void supprimerConsultation(Long id) {
        if (!consultationRepository.existsById(id)) {
            throw new IllegalArgumentException("Consultation avec ID " + id + " introuvable");
        }
        consultationRepository.deleteById(id);
    }
    
    
    // ========== STATISTIQUES (Business Logic) ==========
    
    /**
     * Compter consultations d'un patient.
     * @param patientId ID du patient
     * @return Nombre de consultations
     */
    @Transactional(readOnly = true)
    public long compterConsultationsParPatient(Long patientId) {
        return consultationRepository.countByPatientId(patientId);
    }
    
    /**
     * Obtenir consultations récentes (dernières 30 jours).
     * Utile pour dashboard/statistiques.
     * 
     * @return Liste consultations récentes
     */
    @Transactional(readOnly = true)
    public List<Consultation> obtenirConsultationsRecentes() {
        LocalDateTime dateDebut = LocalDateTime.now().minusDays(30);
        return consultationRepository.findConsultationsRecentes(dateDebut);
    }
    
    /**
     * Obtenir consultations avec diagnostic posé.
     * Utile pour statistiques médicales.
     * 
     * @return Liste consultations avec diagnostic
     */
    @Transactional(readOnly = true)
    public List<Consultation> obtenirConsultationsAvecDiagnostic() {
        return consultationRepository.findConsultationsAvecDiagnostic();
    }
    
    
    // ========== MÉTHODES PRIVÉES (Helpers) ==========
    
    /**
     * Validation des données métier.
     * @throws IllegalArgumentException si données invalides
     */
    private void validerConsultationDTO(ConsultationDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Les données de la consultation sont obligatoires");
        }
        
        // PatientId obligatoire
        if (dto.getPatientId() == null) {
            throw new IllegalArgumentException("Le patient est obligatoire");
        }
        
        // DateConsultation obligatoire
        if (dto.getDateConsultation() == null) {
            throw new IllegalArgumentException("La date de consultation est obligatoire");
        }
        
        // DateConsultation ne peut pas être dans le futur
        if (dto.getDateConsultation().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                "La date de consultation ne peut pas être dans le futur"
            );
        }
        
        // Au moins symptomes OU diagnostic renseigné
        boolean aSymptomes = dto.getSymptomes() != null && !dto.getSymptomes().trim().isEmpty();
        boolean aDiagnostic = dto.getDiagnostic() != null && !dto.getDiagnostic().trim().isEmpty();
        
        if (!aSymptomes && !aDiagnostic) {
            throw new IllegalArgumentException(
                "Au moins les symptômes ou le diagnostic doivent être renseignés"
            );
        }
    }
    
    /**
     * Convertir DTO → Entity.
     * 
     * @param dto DTO consultation
     * @param patient Entity Patient (déjà récupérée depuis DB)
     * @return Entity Consultation
     */
    private Consultation mapToEntity(ConsultationDTO dto, Patient patient) {
        Consultation consultation = new Consultation();
        consultation.setPatient(patient);
        consultation.setDateConsultation(dto.getDateConsultation());
        consultation.setSymptomes(dto.getSymptomes());
        consultation.setDiagnostic(dto.getDiagnostic());
        consultation.setPrescription(dto.getPrescription());
        return consultation;
    }
    
    /**
     * Convertir Entity → DTO (pour affichage UI sans exposer Entity complète).
     * 
     * @param consultation Entity Consultation
     * @return DTO ConsultationDTO
     */
    public ConsultationDTO mapToDTO(Consultation consultation) {
        return new ConsultationDTO(
            consultation.getPatient().getId(),
            consultation.getDateConsultation(),
            consultation.getSymptomes(),
            consultation.getDiagnostic(),
            consultation.getPrescription()
        );
    }
}
