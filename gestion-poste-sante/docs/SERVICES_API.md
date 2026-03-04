# 📚 API des Services - Référence rapide (synchronisée avec le code)

> Source de vérité : les classes dans `src/main/java/com/healthcenter/service`. Cette page donne les signatures actuelles essentielles et renvoie vers les fichiers.

## PatientService
Fichier : `service/PatientService.java`

```java
// Création
Patient creerPatient(PatientDTO dto)

// Lecture
List<Patient> obtenirTousLesPatients()
Optional<Patient> obtenirPatientParId(Long id)
List<Patient> rechercherParNom(String nom)
Optional<Patient> rechercherParNumeroCarnet(String numeroCarnet)

// Mise à jour / suppression
Patient mettreAJourPatient(Long id, PatientDTO dto)
void supprimerPatient(Long id)
long compterParSexe(Sexe sexe)
```

## ConsultationService
Fichier : `service/ConsultationService.java`

- Vérifier dans le code les méthodes exactes (obtenir toutes, par patient, création/mise à jour/suppression). Les signatures font foi.

## MedicamentService / MouvementStockService
Fichiers : `service/MedicamentService.java`, `service/MouvementStockService.java`

- Gestion des médicaments et du stock (création, modification, suppression, mouvement d’entrée/sortie). Se référer aux méthodes `updateStock` et aux DTO utilisés dans ces classes.

## VaccinationService / CalendrierVaccinalService
Fichiers : `service/VaccinationService.java`, `service/CalendrierVaccinalService.java`

- Rappels, dates d’administration, récupération par patient. Vérifier les méthodes pour les signatures exactes.

## PersonnelService / DisponibilitePersonnelService
Fichiers : `service/PersonnelService.java`, `service/DisponibilitePersonnelService.java`

- Méthodes de base (obtenir tout le personnel, personnel actif, par id) et statistiques (top personnel actif avec paramètres début/fin/limite). Les méthodes détaillées sont dans le code.

## StatistiqueService / RapportService / ExportService
Fichiers : `service/StatistiqueService.java`, `service/RapportService.java`, `service/ExportService.java`

- Génération de statistiques, rapports PDF/Excel, exports. Les signatures évoluent : consulter les classes pour la liste à jour.

## UtilisateurService / AuditService / BackupService
Fichiers : `service/UtilisateurService.java`, `service/AuditService.java`, `service/BackupService.java`

- Gestion des utilisateurs, audit et sauvegardes. Vérifier directement dans les classes pour les méthodes disponibles.

## Rappel

- En cas de doute, ouvrez la classe concernée et utilisez l’autocomplétion IDE.
- Si une méthode change, mettez cette page à jour en copiant les signatures depuis la classe.

**Dernière mise à jour :** 2026-03-04
