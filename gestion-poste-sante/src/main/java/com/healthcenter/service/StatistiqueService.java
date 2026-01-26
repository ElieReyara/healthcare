package com.healthcenter.service;

import com.healthcenter.domain.entities.*;
import com.healthcenter.domain.enums.FonctionPersonnel;
import com.healthcenter.domain.enums.PeriodeStatistique;
import com.healthcenter.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service de gestion des statistiques du poste de santé.
 * Agrège les données de tous les modules pour générer des statistiques.
 */
@Service
public class StatistiqueService {
    
    @Autowired
    private PatientService patientService;
    
    @Autowired
    private ConsultationService consultationService;
    
    @Autowired
    private MedicamentService medicamentService;
    
    @Autowired
    private VaccinationService vaccinationService;
    
    @Autowired
    private PersonnelService personnelService;
    
    /**
     * Obtient les statistiques du dashboard principal.
     *
     * @return Les statistiques du dashboard
     */
    @Transactional(readOnly = true)
    public DashboardStats obtenirDashboardStats() {
        DashboardStats stats = new DashboardStats();
        
        LocalDate aujourd = LocalDate.now();
        LocalDate debutMois = aujourd.withDayOfMonth(1);
        LocalDate debutSemaine = PeriodeStatistique.SEMAINE.getDateDebut(aujourd);
        
        // Statistiques patients
        stats.setNbTotalPatients((long) patientService.obtenirTousLesPatients().size());
        stats.setNbPatientsMois((long) patientService.obtenirTousLesPatients().stream()
                .filter(p -> p.getDateNaissance() != null && 
                        !p.getDateNaissance().isBefore(debutMois))
                .count());
        
        // Statistiques consultations
        List<Consultation> consultations = consultationService.obtenirToutesLesConsultations();
        stats.setNbTotalConsultations((long) consultations.size());
        stats.setNbConsultationsMois((long) consultations.stream()
                .filter(c -> c.getDateConsultation() != null && 
                        !c.getDateConsultation().toLocalDate().isBefore(debutMois))
                .count());
        stats.setNbConsultationsSemaine((long) consultations.stream()
                .filter(c -> c.getDateConsultation() != null && 
                        !c.getDateConsultation().toLocalDate().isBefore(debutSemaine))
                .count());
        stats.setNbConsultationsAujourdhui((long) consultations.stream()
                .filter(c -> c.getDateConsultation() != null && 
                        c.getDateConsultation().toLocalDate().equals(aujourd))
                .count());
        
        // Statistiques vaccinations
        List<Vaccination> vaccinations = vaccinationService.obtenirToutesVaccinations();
        stats.setNbTotalVaccinations((long) vaccinations.size());
        stats.setNbVaccinationsMois((long) vaccinations.stream()
                .filter(v -> v.getDateAdministration() != null && 
                        !v.getDateAdministration().isBefore(debutMois))
                .count());
        
        // Statistiques médicaments
        List<Medicament> medicaments = medicamentService.obtenirTousMedicaments();
        stats.setNbMedicamentsStock((long) medicaments.stream()
                .filter(m -> m.getStockActuel() != null && m.getStockActuel() > 0)
                .count());
        stats.setNbMedicamentsRupture((long) medicaments.stream()
                .filter(m -> m.getStockActuel() != null && m.getSeuilAlerte() != null && m.getStockActuel() <= m.getSeuilAlerte())
                .count());
        
        // Statistiques personnel
        stats.setNbPersonnelActif((long) personnelService.obtenirPersonnelActif().size());
        
        return stats;
    }
    
    /**
     * Obtient les statistiques des patients pour une période donnée.
     *
     * @param debut Date de début
     * @param fin Date de fin
     * @return Les statistiques des patients
     */
    @Transactional(readOnly = true)
    public StatistiquesPatients obtenirStatistiquesPatients(LocalDate debut, LocalDate fin) {
        StatistiquesPatients stats = new StatistiquesPatients();
        
        List<Patient> patients = patientService.obtenirTousLesPatients();
        stats.setNbTotal((long) patients.size());
        
        // Répartition par sexe
        Map<String, Long> repartitionSexe = patients.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getSexe().name(),
                        Collectors.counting()
                ));
        stats.setRepartitionSexe(repartitionSexe);
        
        // Répartition par tranche d'âge
        Map<String, Long> repartitionAge = calculerTranchesAge(patients);
        stats.setRepartitionAge(repartitionAge);
        
        // Moyenne d'âge
        Double moyenneAge = calculerMoyenneAge(patients);
        stats.setMoyenneAge(moyenneAge);
        
        // Évolution des nouveaux patients (12 derniers mois)
        List<EvolutionData> evolution = calculerEvolutionNouveauxPatients(patients, 12);
        stats.setNouveauxPatientsMois(evolution);
        
        return stats;
    }
    
    /**
     * Obtient la répartition des patients par sexe.
     *
     * @return Liste de données de répartition
     */
    @Transactional(readOnly = true)
    public List<RepartitionData> obtenirRepartitionPatientsSexe() {
        List<Patient> patients = patientService.obtenirTousLesPatients();
        
        Map<String, Long> repartition = patients.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getSexe().name(),
                        Collectors.counting()
                ));
        
        long total = patients.size();
        
        return repartition.entrySet().stream()
                .map(e -> {
                    RepartitionData data = new RepartitionData(e.getKey(), e.getValue());
                    data.calculerPourcentage(total);
                    return data;
                })
                .sorted(Comparator.comparing(RepartitionData::getLabel))
                .collect(Collectors.toList());
    }
    
    /**
     * Obtient la répartition des patients par tranche d'âge.
     *
     * @return Liste de données de répartition
     */
    @Transactional(readOnly = true)
    public List<RepartitionData> obtenirRepartitionPatientsAge() {
        List<Patient> patients = patientService.obtenirTousLesPatients();
        Map<String, Long> repartition = calculerTranchesAge(patients);
        
        long total = patients.size();
        
        // Ordre des tranches
        List<String> ordresTranches = Arrays.asList("0-5 ans", "6-18 ans", "19-40 ans", "41-60 ans", "60+ ans");
        
        return ordresTranches.stream()
                .map(tranche -> {
                    Long valeur = repartition.getOrDefault(tranche, 0L);
                    RepartitionData data = new RepartitionData(tranche, valeur);
                    data.calculerPourcentage(total);
                    return data;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Obtient les statistiques des consultations pour une période donnée.
     *
     * @param debut Date de début
     * @param fin Date de fin
     * @return Les statistiques des consultations
     */
    @Transactional(readOnly = true)
    public StatistiquesConsultations obtenirStatistiquesConsultations(LocalDate debut, LocalDate fin) {
        StatistiquesConsultations stats = new StatistiquesConsultations();
        
        List<Consultation> toutesConsultations = consultationService.obtenirToutesLesConsultations();
        stats.setNbTotal((long) toutesConsultations.size());
        
        // Consultations de la période
        List<Consultation> consultationsPeriode = toutesConsultations.stream()
                .filter(c -> c.getDateConsultation() != null &&
                        !c.getDateConsultation().toLocalDate().isBefore(debut) &&
                        !c.getDateConsultation().toLocalDate().isAfter(fin))
                .collect(Collectors.toList());
        stats.setNbPeriode((long) consultationsPeriode.size());
        
        // Évolution temporelle
        String granularite = determinerGranularite(debut, fin);
        List<EvolutionData> evolution = obtenirEvolutionConsultations(debut, fin, granularite);
        stats.setEvolutionTemporelle(evolution);
        
        // Maladies fréquentes
        List<RepartitionData> maladies = obtenirMaladiesFrequentes(debut, fin, 10);
        stats.setMaladiesFrequentes(maladies);
        
        // Répartition par personnel
        List<RepartitionData> parPersonnel = calculerConsultationsParPersonnel(consultationsPeriode);
        stats.setConsultationsParPersonnel(parPersonnel);
        
        // Moyenne par jour
        long nbJours = ChronoUnit.DAYS.between(debut, fin) + 1;
        Double moyenne = nbJours > 0 ? (double) consultationsPeriode.size() / nbJours : 0.0;
        stats.setMoyenneParJour(moyenne);
        
        return stats;
    }
    
    /**
     * Obtient l'évolution des consultations dans le temps.
     *
     * @param debut Date de début
     * @param fin Date de fin
     * @param granularite "JOUR", "SEMAINE" ou "MOIS"
     * @return Liste de données d'évolution
     */
    @Transactional(readOnly = true)
    public List<EvolutionData> obtenirEvolutionConsultations(LocalDate debut, LocalDate fin, String granularite) {
        List<Consultation> consultations = consultationService.obtenirToutesLesConsultations().stream()
                .filter(c -> c.getDateConsultation() != null &&
                        !c.getDateConsultation().toLocalDate().isBefore(debut) &&
                        !c.getDateConsultation().toLocalDate().isAfter(fin))
                .collect(Collectors.toList());
        
        Map<String, List<Consultation>> groupes;
        
        if ("JOUR".equals(granularite)) {
            groupes = consultations.stream()
                    .collect(Collectors.groupingBy(
                            c -> c.getDateConsultation().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE)
                    ));
        } else if ("SEMAINE".equals(granularite)) {
            groupes = consultations.stream()
                    .collect(Collectors.groupingBy(
                            c -> {
                                YearMonth ym = YearMonth.from(c.getDateConsultation().toLocalDate());
                                int semaine = (c.getDateConsultation().getDayOfMonth() - 1) / 7 + 1;
                                return ym.getYear() + "-" + String.format("%02d", ym.getMonthValue()) + "-S" + semaine;
                            }
                    ));
        } else { // MOIS
            groupes = consultations.stream()
                    .collect(Collectors.groupingBy(
                            c -> c.getDateConsultation().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM"))
                    ));
        }
        
        return groupes.entrySet().stream()
                .map(e -> new EvolutionData(e.getKey(), e.getValue().size(), 
                        e.getValue().get(0).getDateConsultation().toLocalDate()))
                .sorted(Comparator.comparing(EvolutionData::getDate))
                .collect(Collectors.toList());
    }
    
    /**
     * Obtient les maladies les plus fréquentes.
     *
     * @param debut Date de début
     * @param fin Date de fin
     * @param limit Nombre maximum de résultats
     * @return Liste des maladies avec leur fréquence
     */
    @Transactional(readOnly = true)
    public List<RepartitionData> obtenirMaladiesFrequentes(LocalDate debut, LocalDate fin, int limit) {
        List<Consultation> consultations = consultationService.obtenirToutesLesConsultations().stream()
                .filter(c -> c.getDateConsultation() != null &&
                        !c.getDateConsultation().toLocalDate().isBefore(debut) &&
                        !c.getDateConsultation().toLocalDate().isAfter(fin) &&
                        c.getDiagnostic() != null && !c.getDiagnostic().isBlank())
                .collect(Collectors.toList());
        
        long total = consultations.size();
        
        Map<String, Long> maladies = consultations.stream()
                .collect(Collectors.groupingBy(
                        Consultation::getDiagnostic,
                        Collectors.counting()
                ));
        
        return maladies.entrySet().stream()
                .map(e -> {
                    RepartitionData data = new RepartitionData(e.getKey(), e.getValue());
                    data.calculerPourcentage(total);
                    return data;
                })
                .sorted(Comparator.comparing((RepartitionData d) -> d.getValeur().longValue()).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtient les statistiques des médicaments.
     *
     * @return Map avec les différentes statistiques
     */
    @Transactional(readOnly = true)
    public Map<String, Object> obtenirStatistiquesMedicaments() {
        Map<String, Object> stats = new HashMap<>();
        
        List<Medicament> medicaments = medicamentService.obtenirTousMedicaments();
        
        stats.put("nbTotal", (long) medicaments.size());
        
        long nbRupture = medicaments.stream()
                .filter(m -> m.getStockActuel() != null && m.getSeuilAlerte() != null && m.getStockActuel() <= m.getSeuilAlerte())
                .count();
        stats.put("nbRupture", nbRupture);
        
        double valeurStockTotal = medicaments.stream()
                .filter(m -> m.getPrix() != null && m.getStockActuel() != null)
                .mapToDouble(m -> m.getPrix().doubleValue() * m.getStockActuel())
                .sum();
        stats.put("valeurStockTotal", valeurStockTotal);
        
        // Top médicaments en stock
        List<RepartitionData> topStock = medicaments.stream()
                .filter(m -> m.getStockActuel() != null)
                .sorted(Comparator.comparing(Medicament::getStockActuel).reversed())
                .limit(10)
                .map(m -> new RepartitionData(m.getNom(), m.getStockActuel()))
                .collect(Collectors.toList());
        stats.put("topStock", topStock);
        
        // Alertes (rupture + proche rupture)
        List<Medicament> alertes = medicaments.stream()
                .filter(m -> m.getStockActuel() != null && m.getSeuilAlerte() != null && m.getStockActuel() <= m.getSeuilAlerte())
                .collect(Collectors.toList());
        stats.put("alertes", alertes);
        
        return stats;
    }
    
    /**
     * Obtient les statistiques des vaccinations pour une période donnée.
     *
     * @param debut Date de début
     * @param fin Date de fin
     * @return Map avec les différentes statistiques
     */
    @Transactional(readOnly = true)
    public Map<String, Object> obtenirStatistiquesVaccinations(LocalDate debut, LocalDate fin) {
        Map<String, Object> stats = new HashMap<>();
        
        List<Vaccination> toutesVaccinations = vaccinationService.obtenirToutesVaccinations();
        
        List<Vaccination> vaccinationsPeriode = toutesVaccinations.stream()
                .filter(v -> v.getDateAdministration() != null &&
                        !v.getDateAdministration().isBefore(debut) &&
                        !v.getDateAdministration().isAfter(fin))
                .collect(Collectors.toList());
        
        stats.put("nbTotal", (long) toutesVaccinations.size());
        stats.put("nbPeriode", (long) vaccinationsPeriode.size());
        
        // Couverture vaccinale
        List<RepartitionData> couverture = obtenirCouvertureVaccinale();
        stats.put("couvertureVaccinale", couverture);
        
        // Évolution
        String granularite = determinerGranularite(debut, fin);
        List<EvolutionData> evolution = calculerEvolutionVaccinations(vaccinationsPeriode, granularite);
        stats.put("evolution", evolution);
        
        return stats;
    }
    
    /**
     * Obtient la couverture vaccinale par type de vaccin.
     *
     * @return Liste des pourcentages de couverture
     */
    @Transactional(readOnly = true)
    public List<RepartitionData> obtenirCouvertureVaccinale() {
        List<Vaccination> vaccinations = vaccinationService.obtenirToutesVaccinations();
        long nbPatientsTotal = patientService.obtenirTousLesPatients().size();
        
        if (nbPatientsTotal == 0) {
            return new ArrayList<>();
        }
        
        Map<String, Long> parVaccin = vaccinations.stream()
                .collect(Collectors.groupingBy(
                        v -> v.getVaccin().name(),
                        Collectors.counting()
                ));
        
        return parVaccin.entrySet().stream()
                .map(e -> {
                    double pourcentage = (e.getValue().doubleValue() / nbPatientsTotal) * 100.0;
                    RepartitionData data = new RepartitionData(e.getKey(), pourcentage);
                    return data;
                })
                .sorted(Comparator.comparing((RepartitionData d) -> d.getValeur().doubleValue()).reversed())
                .collect(Collectors.toList());
    }
    
    /**
     * Obtient les statistiques du personnel pour une période donnée.
     *
     * @param debut Date de début
     * @param fin Date de fin
     * @return Map avec les différentes statistiques
     */
    @Transactional(readOnly = true)
    public Map<String, Object> obtenirStatistiquesPersonnel(LocalDate debut, LocalDate fin) {
        Map<String, Object> stats = new HashMap<>();
        
        List<Personnel> personnelActif = personnelService.obtenirPersonnelActif();
        stats.put("nbActif", (long) personnelActif.size());
        
        // Répartition par fonction
        List<RepartitionData> repartition = obtenirRepartitionPersonnelFonction();
        stats.put("repartitionFonction", repartition);
        
        // Top personnel actif
        List<Object[]> topPersonnel = personnelService.obtenirTopPersonnelActif(debut, fin, 10);
        stats.put("topPersonnel", topPersonnel);
        
        return stats;
    }
    
    /**
     * Obtient la répartition du personnel par fonction.
     *
     * @return Liste de données de répartition
     */
    @Transactional(readOnly = true)
    public List<RepartitionData> obtenirRepartitionPersonnelFonction() {
        Map<FonctionPersonnel, Long> repartition = personnelService.compterPersonnelParFonction();
        
        return repartition.entrySet().stream()
                .map(e -> new RepartitionData(e.getKey().getLibelle(), e.getValue()))
                .sorted(Comparator.comparing((RepartitionData d) -> d.getValeur().longValue()).reversed())
                .collect(Collectors.toList());
    }
    
    // ===== MÉTHODES PRIVÉES HELPERS =====
    
    /**
     * Calcule les tranches d'âge des patients.
     */
    private Map<String, Long> calculerTranchesAge(List<Patient> patients) {
        Map<String, Long> tranches = new HashMap<>();
        tranches.put("0-5 ans", 0L);
        tranches.put("6-18 ans", 0L);
        tranches.put("19-40 ans", 0L);
        tranches.put("41-60 ans", 0L);
        tranches.put("60+ ans", 0L);
        
        for (Patient patient : patients) {
            if (patient.getDateNaissance() != null) {
                int age = Period.between(patient.getDateNaissance(), LocalDate.now()).getYears();
                
                String tranche;
                if (age <= 5) {
                    tranche = "0-5 ans";
                } else if (age <= 18) {
                    tranche = "6-18 ans";
                } else if (age <= 40) {
                    tranche = "19-40 ans";
                } else if (age <= 60) {
                    tranche = "41-60 ans";
                } else {
                    tranche = "60+ ans";
                }
                
                tranches.put(tranche, tranches.get(tranche) + 1);
            }
        }
        
        return tranches;
    }
    
    /**
     * Calcule la moyenne d'âge des patients.
     */
    private Double calculerMoyenneAge(List<Patient> patients) {
        List<Integer> ages = patients.stream()
                .filter(p -> p.getDateNaissance() != null)
                .map(p -> Period.between(p.getDateNaissance(), LocalDate.now()).getYears())
                .collect(Collectors.toList());
        
        if (ages.isEmpty()) {
            return 0.0;
        }
        
        return ages.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
    }
    
    /**
     * Calcule l'évolution des nouveaux patients.
     */
    private List<EvolutionData> calculerEvolutionNouveauxPatients(List<Patient> patients, int nbMois) {
        LocalDate maintenant = LocalDate.now();
        Map<YearMonth, Long> parMois = new LinkedHashMap<>();
        
        for (int i = nbMois - 1; i >= 0; i--) {
            YearMonth mois = YearMonth.from(maintenant.minusMonths(i));
            parMois.put(mois, 0L);
        }
        
        patients.stream()
                .filter(p -> p.getDateNaissance() != null)
                .forEach(p -> {
                    YearMonth mois = YearMonth.from(p.getDateNaissance());
                    if (parMois.containsKey(mois)) {
                        parMois.put(mois, parMois.get(mois) + 1);
                    }
                });
        
        return parMois.entrySet().stream()
                .map(e -> new EvolutionData(
                        e.getKey().format(DateTimeFormatter.ofPattern("MMM yyyy")),
                        e.getValue(),
                        e.getKey().atDay(1)
                ))
                .collect(Collectors.toList());
    }
    
    /**
     * Calcule les consultations par personnel.
     */
    private List<RepartitionData> calculerConsultationsParPersonnel(List<Consultation> consultations) {
        Map<String, Long> parPersonnel = consultations.stream()
                .filter(c -> c.getPersonnel() != null)
                .collect(Collectors.groupingBy(
                        c -> c.getPersonnel().getNomComplet(),
                        Collectors.counting()
                ));
        
        return parPersonnel.entrySet().stream()
                .map(e -> new RepartitionData(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing((RepartitionData d) -> d.getValeur().longValue()).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }
    
    /**
     * Calcule l'évolution des vaccinations.
     */
    private List<EvolutionData> calculerEvolutionVaccinations(List<Vaccination> vaccinations, String granularite) {
        Map<String, List<Vaccination>> groupes;
        
        if ("MOIS".equals(granularite)) {
            groupes = vaccinations.stream()
                    .collect(Collectors.groupingBy(
                            v -> v.getDateAdministration().format(DateTimeFormatter.ofPattern("yyyy-MM"))
                    ));
        } else {
            groupes = vaccinations.stream()
                    .collect(Collectors.groupingBy(
                            v -> v.getDateAdministration().format(DateTimeFormatter.ISO_LOCAL_DATE)
                    ));
        }
        
        return groupes.entrySet().stream()
                .map(e -> new EvolutionData(e.getKey(), e.getValue().size(),
                        e.getValue().get(0).getDateAdministration()))
                .sorted(Comparator.comparing(EvolutionData::getDate))
                .collect(Collectors.toList());
    }
    
    /**
     * Détermine la granularité selon la période.
     */
    private String determinerGranularite(LocalDate debut, LocalDate fin) {
        long nbJours = ChronoUnit.DAYS.between(debut, fin);
        
        if (nbJours <= 30) {
            return "JOUR";
        } else if (nbJours <= 90) {
            return "SEMAINE";
        } else {
            return "MOIS";
        }
    }
}
