# 📚 API des Services - Référence Rapide

> **Utilité:** Référence EXACTE des méthodes des services pour éviter les erreurs de nommage  
> **Quand l'utiliser:** AVANT de générer du code qui appelle des services

---

## 👥 PatientService

```java
// Récupération
List<Patient> obtenirTousLesPatients()                    // ⚠️ Noter "TousLES" pas "Tout"
Optional<Patient> obtenirPatientParId(Long id)
List<Patient> rechercherPatients(String critere)

// Modifications
Patient ajouterPatient(Patient patient)
Patient modifierPatient(Patient patient)
void supprimerPatient(Long id)
```

---

## 🏥 ConsultationService

```java
// Récupération
List<Consultation> obtenirToutesLesConsultations()        // ⚠️ "ToutesLES" pas "Tout"
Optional<Consultation> obtenirConsultationParId(Long id)
List<Consultation> obtenirConsultationsParPatient(Long patientId)

// Modifications
Consultation ajouterConsultation(Consultation consultation)
Consultation modifierConsultation(Consultation consultation)
void supprimerConsultation(Long id)
```

---

## 💊 MedicamentService

```java
// Récupération
List<Medicament> obtenirTousMedicaments()                 // ⚠️ "Tous" pas "TousLes"
Optional<Medicament> obtenirMedicamentParId(Long id)
List<Medicament> obtenirMedicamentsEnRupture()           // Stock <= seuilAlerte

// Modifications
Medicament ajouterMedicament(Medicament medicament)
Medicament modifierMedicament(Medicament medicament)
void supprimerMedicament(Long id)
void updateStock(Long id, Integer nouvelleQuantite)      // ⚠️ Méthode spéciale stock
```

---

## 💉 VaccinationService

```java
// Récupération
List<Vaccination> obtenirToutesVaccinations()             // ⚠️ "Toutes" pas "ToutesLes"
Optional<Vaccination> obtenirVaccinationParId(Long id)
List<Vaccination> obtenirVaccinationsParPatient(Long patientId)
List<Vaccination> obtenirVaccinationsAVenir()            // Rappels à venir

// Modifications
Vaccination ajouterVaccination(Vaccination vaccination)
Vaccination modifierVaccination(Vaccination vaccination)
void supprimerVaccination(Long id)
```

---

## 👨‍⚕️ PersonnelService

```java
// Récupération
List<Personnel> obtenirToutPersonnel()                    // ⚠️ "Tout" pas "TousLes"
List<Personnel> obtenirPersonnelActif()                   // Seulement les actifs
Optional<Personnel> obtenirPersonnelParId(Long id)

// Statistiques - ⚠️ IMPORTANT: Signature avec 3 paramètres
List<PersonnelPerformanceDTO> obtenirTopPersonnelActif(LocalDate debut, LocalDate fin, int limite)

// Modifications
Personnel ajouterPersonnel(Personnel personnel)
Personnel modifierPersonnel(Personnel personnel)
void supprimerPersonnel(Long id)
```

---

## 📊 StatistiqueService

```java
// Dashboard
DashboardStats obtenirDashboardStats()

// Patients
List<RepartitionData> obtenirRepartitionPatientsSexe()
List<RepartitionData> obtenirRepartitionPatientsAge()
StatistiquesPatients obtenirStatistiquesPatients(LocalDate debut, LocalDate fin)

// Consultations
List<EvolutionData> obtenirEvolutionConsultations(LocalDate debut, LocalDate fin, String granularite)
List<RepartitionData> obtenirMaladiesFrequentes(LocalDate debut, LocalDate fin, int limite)
StatistiquesConsultations obtenirStatistiquesConsultations(LocalDate debut, LocalDate fin)

// Autres
StatistiquesVaccinations obtenirStatistiquesVaccinations(LocalDate debut, LocalDate fin)
StatistiquesMedicaments obtenirStatistiquesMedicaments()
StatistiquesPersonnel obtenirStatistiquesPersonnel(LocalDate debut, LocalDate fin)
```

---

## 📄 RapportService

```java
// Génération PDF
File genererRapportPDF(TypeRapport type, LocalDate debut, LocalDate fin, String cheminSortie)

// Génération Excel
File genererRapportExcel(TypeRapport type, LocalDate debut, LocalDate fin, String cheminSortie)

// Rapports mensuels
File genererRapportMensuel(int mois, int annee, FormatRapport format)

// Contenu
Map<String, Object> genererContenuRapportActiviteGlobale(LocalDate debut, LocalDate fin)
Map<String, Object> genererContenuRapportConsultations(LocalDate debut, LocalDate fin)
```

---

## ⚠️ PIÈGES COURANTS À ÉVITER

| ❌ ERREUR | ✅ CORRECT | Commentaire |
|----------|-----------|-------------|
| `obtenirToutPatients()` | `obtenirTousLesPatients()` | "TousLES" avec majuscule L |
| `obtenirToutConsultations()` | `obtenirToutesLesConsultations()` | "ToutesLES" féminin |
| `obtenirToutVaccinations()` | `obtenirToutesVaccinations()` | "Toutes" sans "Les" |
| `obtenirTousLesMedicaments()` | `obtenirTousMedicaments()` | "Tous" sans "Les" |
| `obtenirTopPersonnelActif(10)` | `obtenirTopPersonnelActif(debut, fin, 10)` | 3 params obligatoires |

---

## 📖 Comment Utiliser ce Document

1. **Avant de coder:** Consulte ce fichier pour les noms exacts
2. **En cas de doute:** Vérifie avec `grep_search` dans le service concerné
3. **Pour les nouveaux services:** Mets à jour ce fichier avec les nouvelles méthodes

**Dernière mise à jour:** 26 janvier 2026
