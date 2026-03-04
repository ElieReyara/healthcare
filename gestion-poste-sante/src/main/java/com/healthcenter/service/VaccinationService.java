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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

/**
 * SERVICE Vaccination = Logique métier pour gestion vaccinations.
 * 
 * Gère :
 * - CRUD vaccinations
 * - Calcul automatique dates rappels selon calendrier
 * - Suivi rappels (proches, en retard)
 * - Carnet vaccinal par patient
 * - Détection vaccins manquants
 * - Statistiques couverture vaccinale
 */
@Service
public class VaccinationService {
    
    @Autowired
    private VaccinationRepository vaccinationRepository;
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private CalendrierVaccinalRepository calendrierRepository;
    
    
    // ========== CRUD ==========
    
    /**
     * Crée une nouvelle vaccination.
     * Calcule automatiquement la date de rappel si calendrier disponible.
     * 
     * @param dto Données vaccination
     * @return Vaccination créée
     * @throws IllegalArgumentException si validation échoue
     */
    @Transactional
    public Vaccination creerVaccination(VaccinationDTO dto) {
        valider(dto);
        
        // Récupérer patient
        Patient patient = patientRepository.findById(dto.getPatientId())
            .orElseThrow(() -> new IllegalArgumentException(
                "Patient avec ID " + dto.getPatientId() + " introuvable"
            ));
        
        // Mapper DTO → Entity
        Vaccination vaccination = mapToEntity(dto);
        vaccination.setPatient(patient);
        
        // Calcul auto date rappel si calendrier existe
        if (dto.getDateRappel() == null) {
            LocalDate dateRappelCalculee = calculerDateRappelAutomatique(
                TypeVaccin.valueOf(dto.getVaccin()), 
                dto.getDateAdministration()
            );
            vaccination.setDateRappel(dateRappelCalculee);
        }
        
        // Définir statut initial
        vaccination.setStatut(vaccination.calculerStatut());
        
        return vaccinationRepository.save(vaccination);
    }
    
    /**
     * Obtient toutes les vaccinations.
     * 
     * @return Liste vaccinations
     */
    @Transactional(readOnly = true)
    public List<Vaccination> obtenirToutesVaccinations() {
        return vaccinationRepository.findAllWithRelations();
    }
    
    /**
     * Obtient une vaccination par ID.
     * 
     * @param id ID vaccination
     * @return Optional Vaccination
     */
    @Transactional(readOnly = true)
    public Optional<Vaccination> obtenirVaccinationParId(Long id) {
        return vaccinationRepository.findById(id);
    }
    
    /**
     * Obtient toutes les vaccinations d'un patient.
     * Triées par date décroissante (plus récentes en premier).
     * 
     * @param patientId ID patient
     * @return Liste vaccinations patient
     */
    @Transactional(readOnly = true)
    public List<Vaccination> obtenirVaccinationsParPatient(Long patientId) {
        return vaccinationRepository.findByPatientIdWithRelations(patientId);
    }
    
    /**
     * Obtient toutes les vaccinations pour un type de vaccin.
     * 
     * @param vaccin Type vaccin
     * @return Liste vaccinations
     */
    @Transactional(readOnly = true)
    public List<Vaccination> obtenirVaccinationsParVaccin(TypeVaccin vaccin) {
        return vaccinationRepository.findByVaccinWithRelations(vaccin);
    }
    
    /**
     * Met à jour une vaccination.
     * Recalcule la date de rappel si le vaccin a changé.
     * 
     * @param id ID vaccination
     * @param dto Nouvelles données
     * @return Vaccination mise à jour
     */
    @Transactional
    public Vaccination mettreAJourVaccination(Long id, VaccinationDTO dto) {
        Vaccination vaccination = vaccinationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "Vaccination avec ID " + id + " introuvable"
            ));
        
        valider(dto);
        
        // Mise à jour champs
        vaccination.setVaccin(TypeVaccin.valueOf(dto.getVaccin()));
        vaccination.setDateAdministration(dto.getDateAdministration());
        vaccination.setNumeroLot(dto.getNumeroLot());
        vaccination.setObservations(dto.getObservations());
        
        // Recalcul rappel si changé ou non fourni
        if (dto.getDateRappel() != null) {
            vaccination.setDateRappel(dto.getDateRappel());
        } else {
            LocalDate dateRappelCalculee = calculerDateRappelAutomatique(
                vaccination.getVaccin(), 
                vaccination.getDateAdministration()
            );
            vaccination.setDateRappel(dateRappelCalculee);
        }
        
        // Mise à jour statut
        vaccination.setStatut(vaccination.calculerStatut());
        
        return vaccinationRepository.save(vaccination);
    }
    
    /**
     * Supprime une vaccination.
     * 
     * @param id ID vaccination
     */
    @Transactional
    public void supprimerVaccination(Long id) {
        if (!vaccinationRepository.existsById(id)) {
            throw new IllegalArgumentException("Vaccination avec ID " + id + " introuvable");
        }
        vaccinationRepository.deleteById(id);
    }
    
    
    // ========== RAPPELS ==========
    
    /**
     * Obtient les rappels proches (dans les N prochains jours).
     * 
     * @param joursAvance Nombre de jours d'avance (défaut 7)
     * @return Liste vaccinations avec rappels proches
     */
    @Transactional(readOnly = true)
    public List<Vaccination> obtenirRappelsProchains(Integer joursAvance) {
        if (joursAvance == null) {
            joursAvance = 7;
        }
        
        LocalDate aujourdhui = LocalDate.now();
        LocalDate dateLimite = aujourdhui.plusDays(joursAvance);
        
        return vaccinationRepository.findRappelsProchains(aujourdhui, dateLimite);
    }
    
    /**
     * Obtient les rappels en retard.
     * 
     * @return Liste vaccinations avec rappels en retard
     */
    @Transactional(readOnly = true)
    public List<Vaccination> obtenirRappelsEnRetard() {
        return vaccinationRepository.findRappelsEnRetard(LocalDate.now());
    }
    
    /**
     * Met à jour les statuts de toutes les vaccinations.
     * Passe RAPPEL_PREVU → RAPPEL_EN_RETARD si date rappel dépassée.
     */
    @Transactional
    public void mettreAJourStatuts() {
        List<Vaccination> vaccinations = vaccinationRepository.findAll();
        
        for (Vaccination vaccination : vaccinations) {
            StatutVaccination nouveauStatut = vaccination.calculerStatut();
            if (!nouveauStatut.equals(vaccination.getStatut())) {
                vaccination.setStatut(nouveauStatut);
                vaccinationRepository.save(vaccination);
            }
        }
    }
    
    
    // ========== CARNET VACCINAL ==========
    
    /**
     * Obtient le carnet vaccinal complet d'un patient.
     * Vaccinations groupées par type de vaccin.
     * 
     * @param patientId ID patient
     * @return Map TypeVaccin → Liste Vaccinations
     */
    @Transactional(readOnly = true)
    public Map<TypeVaccin, List<Vaccination>> obtenirCarnetVaccinal(Long patientId) {
        List<Vaccination> vaccinations = vaccinationRepository.findByPatientIdOrderByDateAdministrationDesc(patientId);
        
        return vaccinations.stream()
            .collect(Collectors.groupingBy(Vaccination::getVaccin));
    }
    
    /**
     * Vérifie les vaccinations manquantes pour un patient.
     * Compare vaccinations faites vs calendrier obligatoire selon âge.
     * 
     * @param patientId ID patient
     * @return Liste types vaccins manquants
     */
    @Transactional(readOnly = true)
    public List<TypeVaccin> verifierVaccinationsManquantes(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Patient avec ID " + patientId + " introuvable"
            ));
        
        // Âge patient en jours
        long ageJours = java.time.temporal.ChronoUnit.DAYS.between(
            patient.getDateNaissance(), 
            LocalDate.now()
        );
        
        // Vaccinations déjà faites
        List<Vaccination> vaccinationsFaites = vaccinationRepository.findByPatientId(patientId);
        Set<TypeVaccin> vaccinsRecus = vaccinationsFaites.stream()
            .map(Vaccination::getVaccin)
            .collect(Collectors.toSet());
        
        // Vaccins obligatoires selon âge
        List<CalendrierVaccinal> calendriers = calendrierRepository.findByObligatoireTrue();
        
        return calendriers.stream()
            .filter(c -> c.getAgeRecommande() <= (int)ageJours) // Patient assez âgé
            .map(CalendrierVaccinal::getVaccin)
            .filter(v -> !vaccinsRecus.contains(v)) // Pas encore reçu
            .collect(Collectors.toList());
    }
    
    
    // ========== STATISTIQUES ==========
    
    /**
     * Calcule le taux de couverture pour un vaccin donné.
     * Formule : (Patients vaccinés / Total patients) * 100
     * 
     * @param vaccin Type vaccin
     * @return Taux couverture en %
     */
    @Transactional(readOnly = true)
    public Double calculerTauxCouverture(TypeVaccin vaccin) {
        long totalPatients = patientRepository.count();
        if (totalPatients == 0) {
            return 0.0;
        }
        
        List<Vaccination> vaccinationsType = vaccinationRepository.findByVaccin(vaccin);
        Set<Long> patientsVaccines = vaccinationsType.stream()
            .map(v -> v.getPatient().getId())
            .collect(Collectors.toSet());
        
        return (patientsVaccines.size() * 100.0) / totalPatients;
    }
    
    
    // ========== HELPERS PRIVÉS ==========
    
    /**
     * Valide un DTO Vaccination.
     * 
     * @param dto DTO à valider
     * @throws IllegalArgumentException si invalide
     */
    private void valider(VaccinationDTO dto) {
        if (dto.getPatientId() == null) {
            throw new IllegalArgumentException("Le patient est obligatoire");
        }
        
        if (dto.getVaccin() == null || dto.getVaccin().isEmpty()) {
            throw new IllegalArgumentException("Le vaccin est obligatoire");
        }
        
        if (dto.getDateAdministration() == null) {
            throw new IllegalArgumentException("La date d'administration est obligatoire");
        }
        
        if (dto.getDateAdministration().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                "La date d'administration ne peut pas être dans le futur"
            );
        }
    }
    
    /**
     * Mappe un DTO vers une Entity.
     * 
     * @param dto DTO source
     * @return Entity Vaccination
     */
    private Vaccination mapToEntity(VaccinationDTO dto) {
        Vaccination vaccination = new Vaccination();
        vaccination.setVaccin(TypeVaccin.valueOf(dto.getVaccin()));
        vaccination.setDateAdministration(dto.getDateAdministration());
        vaccination.setDateRappel(dto.getDateRappel());
        vaccination.setNumeroLot(dto.getNumeroLot());
        vaccination.setObservations(dto.getObservations());
        return vaccination;
    }
    
    /**
     * Calcule la date de rappel automatiquement selon le calendrier.
     * 
     * @param vaccin Type vaccin
     * @param dateAdmin Date administration
     * @return Date rappel calculée ou null si pas de rappels
     */
    private LocalDate calculerDateRappelAutomatique(TypeVaccin vaccin, LocalDate dateAdmin) {
        Optional<CalendrierVaccinal> calendrierOpt = calendrierRepository.findByVaccin(vaccin);
        
        if (calendrierOpt.isEmpty()) {
            return null;
        }
        
        return calendrierOpt.get().calculerDateRappel(dateAdmin);
    }
}
