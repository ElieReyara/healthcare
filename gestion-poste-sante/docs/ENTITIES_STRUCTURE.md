# 🏗️ Structure des Entités - Référence Rapide

> **Utilité:** Connaître les champs EXACTS des entités pour éviter les erreurs de nommage  
> **Quand l'utiliser:** AVANT d'utiliser une entité (lecture, écriture, test)

---

## 👤 Patient

```java
// Champs disponibles
private Long id;                    // ⚠️ Auto-généré JPA - PAS de setId()
private String nom;                 // String
private String prenom;              // String
private LocalDate dateNaissance;    // ⚠️ LocalDate pas LocalDateTime
private Sexe sexe;                  // Enum: HOMME, FEMME
private String telephone;           // String (optionnel)
private String adresse;             // String (optionnel)

// Méthodes utiles
String getNom()
String getPrenom()
// ⚠️ PAS de getNomComplet() - construire manuellement avec getNom() + " " + getPrenom()
// ⚠️ PAS de email - champ n'existe pas
// ⚠️ PAS de dateInscription - utiliser dateNaissance si besoin
```

---

## 🏥 Consultation

```java
// Champs disponibles
private Long id;                           // Auto-généré
private Patient patient;                   // Relation ManyToOne
private Personnel personnel;               // Relation ManyToOne (optionnel)
private LocalDateTime dateConsultation;    // ⚠️ LocalDateTime pas LocalDate
private String motif;                      // String
private String diagnostic;                 // String
private String traitement;                 // String (optionnel)
private String notes;                      // String (optionnel)

// Getters importants
LocalDateTime getDateConsultation()        // ⚠️ Retourne LocalDateTime
Patient getPatient()
String getDiagnostic()

// Pour convertir en LocalDate
consultation.getDateConsultation().toLocalDate()
```

---

## 💊 Medicament

```java
// Champs disponibles
private Long id;                    // Auto-généré
private String nom;                 // String
private String description;         // String (optionnel)
private Double prix;                // ⚠️ getPrix() pas getPrixUnitaire()
private Integer stockActuel;        // ⚠️ getStockActuel() pas getQuantiteStock()
private Integer seuilAlerte;        // Integer

// Getters critiques
Double getPrix()                    // ⚠️ PAS getPrixUnitaire()
Integer getStockActuel()            // ⚠️ PAS getQuantiteStock()
Integer getSeuilAlerte()

// Méthode calculée
boolean isEnRupture()               // stockActuel <= seuilAlerte
```

---

## 💉 Vaccination

```java
// Champs disponibles
private Long id;                         // Auto-généré
private Patient patient;                 // Relation ManyToOne
private Personnel personnel;             // Relation ManyToOne (optionnel)
private String vaccin;                   // ⚠️ getVaccin() pas getTypeVaccin()
private LocalDate dateAdministration;    // ⚠️ LocalDate
private LocalDate dateRappel;            // LocalDate (optionnel)
private String numeroLot;                // String (optionnel)

// Getters importants
String getVaccin()                       // ⚠️ PAS getTypeVaccin()
LocalDate getDateAdministration()
LocalDate getDateRappel()
```

---

## 👨‍⚕️ Personnel

```java
// Champs disponibles
private Long id;                    // Auto-généré
private String nom;                 // String
private String prenom;              // String
private Specialite specialite;      // Enum: MEDECIN_GENERALISTE, INFIRMIER, SAGE_FEMME, etc.
private String telephone;           // String (optionnel)
private String email;               // String (optionnel)
private boolean actif;              // boolean (true par défaut)

// Getters importants
String getNom()
String getPrenom()
Specialite getSpecialite()
boolean isActif()
```

---

## 📄 Rapport

```java
// Champs disponibles
private Long id;                    // Auto-généré
private TypeRapport type;           // Enum
private FormatRapport format;       // Enum: PDF, EXCEL
private LocalDate dateDebut;        // LocalDate
private LocalDate dateFin;          // LocalDate
private LocalDateTime dateGeneration; // LocalDateTime
private String cheminFichier;       // String
```

---

## 🎯 DTOs (Objets de Transfert)

### DashboardStats
```java
// Constructeur avec 11 paramètres
new DashboardStats(
    nbTotalPatients,         // Long
    nbPatientsMois,          // Long
    nbTotalConsultations,    // Long
    nbConsultationsMois,     // Long
    nbConsultationsSemaine,  // Long
    nbConsultationsAujourdhui, // Long
    nbTotalVaccinations,     // Long
    nbVaccinationsMois,      // Long
    nbMedicamentsStock,      // Long
    nbMedicamentsRupture,    // Long
    nbPersonnelActif         // Long
)

// OU constructeur vide + setters
DashboardStats stats = new DashboardStats();
stats.setNbTotalPatients(100L);
```

### StatistiquesPatients
```java
// Champs disponibles
private Long nbTotal;
private Map<String, Long> repartitionSexe;      // ⚠️ Map pas List
private Map<String, Long> repartitionAge;       // ⚠️ Map pas List
private Double moyenneAge;
private List<EvolutionData> nouveauxPatientsMois; // ⚠️ PAS de setNbNouveaux()

// Utilisation
StatistiquesPatients stats = new StatistiquesPatients();
stats.setNbTotal(100L);
stats.setRepartitionSexe(new HashMap<>());      // ⚠️ HashMap pas ArrayList
stats.setMoyenneAge(25.5);
```

### StatistiquesConsultations
```java
// Champs disponibles
private Long nbTotal;
private Long nbPeriode;
private Long nbParPatient;
private List<EvolutionData> evolutionTemporelle;
private List<RepartitionData> maladiesFrequentes; // ⚠️ List<RepartitionData>
private Double moyenneParJour;
private Double moyenneParPatient;
private Double dureeConsultationMoyenne;

// Utilisation
stats.setMaladiesFrequentes(new ArrayList<>()); // ⚠️ ArrayList<RepartitionData>
```

### RepartitionData
```java
// Structure simple pour graphiques
private String label;    // Ex: "HOMME", "Paludisme"
private Long valeur;     // Nombre

// Pour PieChart - ⚠️ Conversion nécessaire
new PieChart.Data(data.getLabel(), data.getValeur().doubleValue())
```

### EvolutionData
```java
// Structure pour graphiques temporels
private String periode;  // Ex: "2026-01", "Semaine 3"
private Long valeur;     // Nombre
```

---

## ⚠️ RÈGLES CRITIQUES POUR LES TESTS

```java
// ❌ INTERDIT en tests
patient.setId(1L);                  // JPA gère les IDs automatiquement

// ✅ CORRECT en tests
Patient patient = new Patient();
patient.setNom("Diallo");
patient.setPrenom("Amadou");
// Pas de setId() - laisser JPA faire

// Pour dates en tests
consultation.setDateConsultation(LocalDate.now().atStartOfDay()); // ⚠️ LocalDateTime
vaccination.setDateAdministration(LocalDate.now());               // ⚠️ LocalDate
```

---

## 🔄 Conversions Communes

```java
// LocalDate → LocalDateTime
LocalDate date = LocalDate.now();
LocalDateTime dateTime = date.atStartOfDay();

// LocalDateTime → LocalDate
LocalDateTime dateTime = LocalDateTime.now();
LocalDate date = dateTime.toLocalDate();

// Long → Double (pour graphiques)
Long valeur = 100L;
double valeurDouble = valeur.doubleValue();
```

---

## 📖 Comment Utiliser ce Document

1. **Avant d'utiliser une entité:** Vérifie les champs disponibles ici
2. **En cas de doute:** Utilise `read_file` sur l'entité pour voir le code
3. **Pour les tests:** Respecte les règles JPA (pas de setId)
4. **Pour les DTOs:** Vérifie le type (Map vs List) avant d'instancier

**Dernière mise à jour:** 26 janvier 2026
