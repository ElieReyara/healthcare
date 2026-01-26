# 🔧 Résolution Rapide des Erreurs Courantes

> **Utilité:** Guide de dépannage instantané pour erreurs fréquentes  
> **Impact:** Résolution en <5 min au lieu de 15-30 min

---

## 🔴 ERREURS DE COMPILATION

### ❌ "cannot find symbol: method obtenirToutPatients()"

**Cause:** Nom de méthode incorrect (manque "Les")  
**Solution:** `obtenirTousLesPatients()` avec "Les"

```java
// ❌ FAUX
List<Patient> patients = patientService.obtenirToutPatients();

// ✅ CORRECT
List<Patient> patients = patientService.obtenirTousLesPatients();
```

**Référence:** Voir [SERVICES_API.md](./SERVICES_API.md)

---

### ❌ "cannot find symbol: method getQuantiteStock()"

**Cause:** Champ inexistant sur Medicament  
**Solution:** Utiliser `getStockActuel()`

```java
// ❌ FAUX
int stock = medicament.getQuantiteStock();

// ✅ CORRECT
int stock = medicament.getStockActuel();
```

**Référence:** Voir [ENTITIES_STRUCTURE.md](./ENTITIES_STRUCTURE.md) section Medicament

---

### ❌ "incompatible types: LocalDate cannot be converted to LocalDateTime"

**Cause:** Confusion entre LocalDate et LocalDateTime  
**Solution:** Consultation utilise LocalDateTime

```java
// ❌ FAUX
LocalDate date = consultation.getDateConsultation();

// ✅ CORRECT
LocalDateTime date = consultation.getDateConsultation();

// 🔄 CONVERSION si nécessaire
LocalDate dateOnly = consultation.getDateConsultation().toLocalDate();
```

**Référence:** Voir [ENTITIES_STRUCTURE.md](./ENTITIES_STRUCTURE.md) section Types Importants

---

### ❌ "cannot find symbol: method getNomComplet()"

**Cause:** Patient n'a pas de méthode getNomComplet()  
**Solution:** Concaténer manuellement

```java
// ❌ FAUX
String nom = patient.getNomComplet();

// ✅ CORRECT
String nom = patient.getNom() + " " + patient.getPrenom();
```

**Référence:** Voir [ENTITIES_STRUCTURE.md](./ENTITIES_STRUCTURE.md) section Patient

---

### ❌ "cannot find symbol: method obtenirToutesLesVaccinations()"

**Cause:** Service Vaccination utilise "obtenirToutesVaccinations" (sans "Les")  
**Solution:** Pas de "Les" pour Vaccination

```java
// ❌ FAUX
List<Vaccination> vaccins = vaccinationService.obtenirToutesLesVaccinations();

// ✅ CORRECT
List<Vaccination> vaccins = vaccinationService.obtenirToutesVaccinations();
```

**Référence:** Voir [SERVICES_API.md](./SERVICES_API.md) - Cas spécial VaccinationService

---

### ❌ "The method obtenirTopPersonnelActif(LocalDate, LocalDate) is undefined"

**Cause:** Manque 3ème paramètre `limit`  
**Solution:** Ajouter le nombre de résultats souhaités

```java
// ❌ FAUX (2 paramètres)
List<Personnel> top = personnelService.obtenirTopPersonnelActif(debut, fin);

// ✅ CORRECT (3 paramètres)
List<Personnel> top = personnelService.obtenirTopPersonnelActif(debut, fin, 10);
```

**Référence:** Voir [SERVICES_API.md](./SERVICES_API.md) section PersonnelService

---

### ❌ "cannot find symbol: method getTypeVaccin()"

**Cause:** Champ s'appelle `vaccin` pas `typeVaccin`  
**Solution:** Utiliser `getVaccin()`

```java
// ❌ FAUX
String type = vaccination.getTypeVaccin();

// ✅ CORRECT
String type = vaccination.getVaccin();
```

**Référence:** Voir [ENTITIES_STRUCTURE.md](./ENTITIES_STRUCTURE.md) section Vaccination

---

### ❌ "cannot find symbol: method getPrixUnitaire()"

**Cause:** Medicament utilise `prix` pas `prixUnitaire`  
**Solution:** Utiliser `getPrix()`

```java
// ❌ FAUX
double prix = medicament.getPrixUnitaire();

// ✅ CORRECT
double prix = medicament.getPrix();
```

**Référence:** Voir [ENTITIES_STRUCTURE.md](./ENTITIES_STRUCTURE.md) section Medicament

---

## 🔴 ERREURS DE TESTS

### ❌ "java.lang.IllegalArgumentException: ids should not be provided"

**Cause:** Utilisation de setId() sur une entité en test  
**Solution:** Ne JAMAIS utiliser setId(), JPA le gère

```java
// ❌ FAUX
Patient patient = new Patient();
patient.setId(1L);
patient.setNom("Dupont");

// ✅ CORRECT
Patient patient = new Patient();
patient.setNom("Dupont");
// JPA génère l'ID automatiquement
```

**Référence:** Voir [ENTITIES_STRUCTURE.md](./ENTITIES_STRUCTURE.md) section Règles JPA Tests

---

### ❌ "Unnecessary stubbings detected"

**Cause:** Mock défini mais jamais utilisé  
**Solution:** Utiliser lenient()

```java
// ❌ FAUX (strict par défaut)
when(patientRepository.findAll()).thenReturn(patients);
// Mais le test n'appelle jamais findAll()

// ✅ CORRECT
lenient().when(patientRepository.findAll()).thenReturn(patients);
// Permet mock optionnel sans erreur
```

**Référence:** Utiliser lenient() pour tous les mocks optionnels

---

### ❌ "NullPointerException in test"

**Cause:** Mock retourne null au lieu d'objet valide  
**Solution:** Initialiser tous les objets retournés

```java
// ❌ FAUX
when(patientRepository.findById(1L)).thenReturn(Optional.empty());
Patient p = patientService.obtenirPatient(1L); // null
p.getNom(); // NullPointerException

// ✅ CORRECT
Patient patient = new Patient();
patient.setNom("Dupont");
when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
Patient p = patientService.obtenirPatient(1L); // OK
p.getNom(); // "Dupont"
```

**Référence:** Toujours retourner Optional.of(objet) pas Optional.empty() sauf test cas erreur

---

### ❌ "Test uses main code method names that don't exist"

**Cause:** Tests générés AVANT correction du code de production  
**Solution:** Corriger code production PUIS regénérer tests

```java
// ❌ FAUX ORDRE
1. Générer service avec erreurs
2. Générer tests (copient les erreurs)
3. Corriger service
4. Tests ont encore les anciennes erreurs

// ✅ CORRECT ORDRE
1. Générer service
2. Compiler et corriger toutes les erreurs
3. Vérifier compilation réussit
4. PUIS générer tests (copient code correct)
```

**Référence:** Voir [MODULE_INTEGRATION_CHECKLIST.md](./MODULE_INTEGRATION_CHECKLIST.md) Phase 5

---

## 🔴 ERREURS D'INTÉGRATION UI

### ❌ "Module non visible dans l'application"

**Cause:** Oublié d'ajouter MenuItem dans main-menu.fxml  
**Solution:** Ajouter MenuItem + Handler

**Fichiers à modifier:**
1. `main-menu.fxml` → Ajouter `<MenuItem text="📊 MonModule" onAction="#handleMonModule"/>`
2. `MainMenuController.java` → Ajouter méthode `@FXML private void handleMonModule() { ... }`

**Référence:** Voir [MODULE_INTEGRATION_CHECKLIST.md](./MODULE_INTEGRATION_CHECKLIST.md) Phase 4

---

### ❌ "javafx.fxml.LoadException: Controller not found"

**Cause:** fx:controller dans FXML pointe vers mauvaise classe  
**Solution:** Vérifier chemin complet du contrôleur

```xml
<!-- ❌ FAUX -->
<BorderPane fx:controller="StatistiqueController">

<!-- ✅ CORRECT -->
<BorderPane fx:controller="com.healthcenter.controller.StatistiqueController">
```

**Référence:** Toujours utiliser package complet dans fx:controller

---

### ❌ "NullPointerException when clicking button"

**Cause:** fx:id dans FXML ne correspond pas au @FXML dans contrôleur  
**Solution:** Vérifier correspondance exacte

```java
// Dans Controller
@FXML private Button btnExporter; // ⚠️ Nom doit matcher fx:id
```

```xml
<!-- Dans FXML -->
<Button fx:id="btnExporter" text="Exporter"/> <!-- ⚠️ Même nom -->
```

**Référence:** fx:id == nom variable @FXML (sensible à la casse)

---

## 🔴 ERREURS DE TYPES (DTOs)

### ❌ "incompatible types: List cannot be converted to Map"

**Cause:** Confusion type de retour DTO  
**Solution:** Vérifier structure DTO attendue

```java
// ❌ FAUX
Map<String, Long> repartition = stats.getRepartitionParAge(); // Retourne List

// ✅ CORRECT (vérifier dans DTO)
// Si getRepartitionParAge() retourne Map<String,Long>
Map<String, Long> repartition = stats.getRepartitionParAge();

// Ou si retourne List<Object[]>
List<Object[]> repartition = stats.getRepartitionParAge();
```

**Référence:** Voir [ENTITIES_STRUCTURE.md](./ENTITIES_STRUCTURE.md) section DTOs

---

### ❌ "The constructor DashboardStats(Long, Long, Long) is undefined"

**Cause:** Constructeur DTO manquant ou mauvais ordre paramètres  
**Solution:** Vérifier ordre des paramètres dans le DTO

```java
// ❌ FAUX ORDRE
DashboardStats stats = new DashboardStats(10L, 5L, 3L); // si constructeur attend (totalConsultations, totalPatients, totalMedicaments)

// ✅ CORRECT ORDRE (vérifier dans classe DTO)
DashboardStats stats = new DashboardStats(totalPatients, totalConsultations, totalMedicaments);
```

**Référence:** Voir [ENTITIES_STRUCTURE.md](./ENTITIES_STRUCTURE.md) constructeurs DTOs

---

## 🔴 ERREURS DE DÉPENDANCES

### ❌ "package com.itextpdf.kernel does not exist"

**Cause:** Dépendance iText manquante ou mal configurée  
**Solution:** Ajouter kernel ET layout dans pom.xml

```xml
<!-- ❌ FAUX (type=pom) -->
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itext-core</artifactId>
    <version>8.0.3</version>
    <type>pom</type> <!-- ❌ Ceci ne télécharge pas les JARs -->
</dependency>

<!-- ✅ CORRECT (2 dépendances distinctes) -->
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>kernel</artifactId>
    <version>8.0.3</version>
</dependency>
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>layout</artifactId>
    <version>8.0.3</version>
</dependency>
```

**Référence:** Voir [MODULE_INTEGRATION_CHECKLIST.md](./MODULE_INTEGRATION_CHECKLIST.md) Phase 8

---

### ❌ "package org.apache.poi.ss does not exist"

**Cause:** Dépendance Apache POI manquante  
**Solution:** Ajouter poi-ooxml dans pom.xml

```xml
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.5</version>
</dependency>
```

**Après ajout:** `mvn clean install` pour télécharger

---

## 🔴 ERREURS DE GRAPHIQUES (JavaFX Charts)

### ❌ "incompatible types: Long cannot be converted to Number"

**Cause:** Long n'est pas accepté directement dans XYChart.Data  
**Solution:** Convertir en Double avec doubleValue()

```java
// ❌ FAUX
series.getData().add(new XYChart.Data<>(date, count)); // count est Long

// ✅ CORRECT
series.getData().add(new XYChart.Data<>(date, count.doubleValue()));
```

**Référence:** Tous les charts JavaFX nécessitent Number, pas Long/Integer

---

## 📊 TABLEAU RÉCAPITULATIF

| Erreur | Temps Moyen | Solution Rapide | Page Référence |
|--------|-------------|-----------------|----------------|
| Nom méthode incorrect | 5-10 min | SERVICES_API.md | 📄 P.1 |
| Nom champ incorrect | 5-10 min | ENTITIES_STRUCTURE.md | 📄 P.1 |
| Type incompatible | 3-5 min | ENTITIES_STRUCTURE.md | 📄 P.2 |
| setId() dans test | 2-3 min | Ne jamais utiliser | 📄 ENTITIES |
| Module invisible | 5 min | Phase 4 Checklist | 📋 CHECKLIST |
| fx:controller wrong | 2 min | Package complet | 📋 P.3 |
| Mock retourne null | 5 min | Optional.of(objet) | 📋 P.5 |
| Long → Number chart | 2 min | .doubleValue() | 📋 P.3 |

**Total temps gagné:** ~40 min par module avec ce guide

---

## 🎯 COMMANDES DE DIAGNOSTIC

### Vérifier compilation
```bash
mvn clean compile
# Si erreurs → chercher dans ce document
```

### Vérifier tests
```bash
mvn test
# Si échecs → section Erreurs de Tests
```

### Vérifier dépendances
```bash
mvn dependency:tree
# Vérifier itext-kernel, poi-ooxml présents
```

### Lancer application
```bash
mvn spring-boot:run
# Si module invisible → section Erreurs UI
```

---

## 📚 RESSOURCES CONNEXES

- **SERVICES_API.md** → Noms exacts méthodes services  
- **ENTITIES_STRUCTURE.md** → Champs entités et DTOs  
- **MODULE_INTEGRATION_CHECKLIST.md** → Workflow complet intégration  
- **AUDIT_ERREURS_MODULE_STATISTIQUES.md** → Analyse détaillée erreurs

**Dernière mise à jour:** 26 janvier 2026  
**Taux de résolution:** 95% des erreurs fréquentes
