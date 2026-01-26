***

# CONTEXT.MD COMPLET (Version finale)

```markdown
# CONTEXT – Gestion Poste de Santé Communautaire

**Dernière mise à jour :** 2026-01-26 18:00  
**Version :** 2.0.0  
**Modules complétés :** Patient (100%), Consultations (100%), Médicaments + Stock (100%)  
**Module en cours :** -  
**Modules restants :** Vaccinations, Personnel, Statistiques, Rapports

---

## GuideLine
- Le symbole '##' indique une nouvelle section
- Le symbole '###' indique un sous-titre
- Le symbole '---' indique une séparation entre sections
- Les listes à puces commencent par '- ' ou '→'
- Les sous-points commencent par '* '
- Les extraits de code sont entourés de triples backticks (```)

---

## STACK TECHNIQUE

### Technologies principales
| Composant | Technologie | Version | Justification |
|-----------|-------------|---------|---------------|
| Langage | Java | 17 LTS | Support long terme jusqu'en 2029 |
| Framework Backend | Spring Boot | 3.2.1 | Standard industrie, desktop JavaFX |
| ORM | Hibernate (via Spring Data JPA) | 6.4.1 | Mapping objet-relationnel automatique |
| UI Desktop | JavaFX | 21 | Interface moderne, alternative Swing |
| Base de données | PostgreSQL | 15+ | Robuste, gratuit, scalable |
| Build Tool | Maven | 3.9+ | Gestion dépendances, standard Java |
| Tests | JUnit 5 + Mockito | 5.10+ | Tests unitaires + mocking |

---

## ARCHITECTURE GLOBALE

### Pattern architectural
**Clean Architecture (Layered)**

### Schéma des couches
```
┌─────────────────────────────────────────┐
│  COUCHE 4 : PRESENTATION (UI)           │
│  → Controllers JavaFX (@Component)      │
│  → Views FXML (fichiers XML)            │
│  → DTOs (objets transfert)              │
└──────────────┬──────────────────────────┘
               ↓ appelle (via DTO)
┌─────────────────────────────────────────┐
│  COUCHE 3 : APPLICATION (Services)      │
│  → PatientService (@Service)            │
│  → ConsultationService                  │
│  → Logique métier + Validation          │
└──────────────┬──────────────────────────┘
               ↓ appelle (via Entity)
┌─────────────────────────────────────────┐
│  COUCHE 2 : PERSISTENCE (Repositories)  │
│  → PatientRepository (JpaRepository)    │
│  → Accès données (CRUD auto-généré)     │
└──────────────┬──────────────────────────┘
               ↓ manipule
┌─────────────────────────────────────────┐
│  COUCHE 1 : DOMAIN (Entities)           │
│  → Patient.java (@Entity)               │
│  → Objets métier mappés SQL             │
└─────────────────────────────────────────┘
```

### Règle de dépendance
**Les dépendances pointent TOUJOURS vers le bas :**
- UI dépend de Service (jamais l'inverse)
- Service dépend de Repository
- Repository dépend de Entity
- Entity ne dépend de RIEN (POO pure)

---

## MODÈLE DE DONNÉES

### Schéma relationnel
```
Patient 1────N Consultation
Personnel 1──N Consultation
Consultation N─N Medicament (table jonction)
Maladie 1────N Consultation
Medicament 1─N MouvementStock
Patient 1────N Vaccination
```

### Entity Patient (TERMINÉ ✅)
**Fichier :** `src/main/java/com/healthcenter/domain/entities/Patient.java`

```java
@Entity
@Table(name = "patients")
public class Patient {
    @Id @GeneratedValue(strategy = IDENTITY)
    Long id;
    
    @Column(nullable = false, length = 100)
    String nom;
    
    @Column(nullable = false, length = 100)
    String prenom;
    
    @Column(name = "date_naissance")
    LocalDate dateNaissance;
    
    @Enumerated(EnumType.STRING)
    Sexe sexe;  // Enum : HOMME, FEMME, AUTRE
    
    String telephone;
    String adresse;
    
    @Column(unique = true, length = 50)
    String numeroCarnet;
    
    @OneToMany(mappedBy = "patient", cascade = ALL)
    List<Consultation> consultations;
    
    @OneToMany(mappedBy = "patient", cascade = ALL)
    List<Vaccination> vaccinations;
}
```

**Règles métier Patient :**
- Nom, prénom, dateNaissance, sexe : **OBLIGATOIRES**
- Date naissance : **< aujourd'hui**
- NumeroCarnet : **UNIQUE** si fourni

---

### Entity Consultation (À FAIRE 🔄)
**Fichier :** `src/main/java/com/healthcenter/domain/entities/Consultation.java`

```java
@Entity
@Table(name = "consultations")
public class Consultation {
    @Id @GeneratedValue
    Long id;
    
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    Patient patient;
    
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "personnel_id")
    Personnel personnel;
    
    @Column(nullable = false)
    LocalDateTime dateConsultation;
    
    @Column(length = 500)
    String symptomes;
    
    @Column(length = 500)
    String diagnostic;
    
    @Column(length = 1000)
    String prescription;
    
    @ManyToOne
    @JoinColumn(name = "maladie_id")
    Maladie maladie;
    
    @ManyToMany
    @JoinTable(
        name = "consultation_medicaments",
        joinColumns = @JoinColumn(name = "consultation_id"),
        inverseJoinColumns = @JoinColumn(name = "medicament_id")
    )
    List<Medicament> medicaments;
}
```

**Règles métier Consultation :**
- Patient : **OBLIGATOIRE**
- DateConsultation : **OBLIGATOIRE**, **<= aujourd'hui**
- Symptomes ou diagnostic : **au moins 1 obligatoire**

---

### Entity Medicament (À FAIRE ⏳)
**Fichier :** `src/main/java/com/healthcenter/domain/entities/Medicament.java`

```java
@Entity
@Table(name = "medicaments")
public class Medicament {
    @Id @GeneratedValue
    Long id;
    
    @Column(nullable = false, length = 200)
    String nom;
    
    String dosage;  // Ex: "500mg"
    
    @Enumerated(EnumType.STRING)
    FormeMedicament forme;  // COMPRIME, SIROP, INJECTION, POMMADE
    
    BigDecimal prix;
    
    Integer stockActuel;
    Integer seuilAlerte;  // Alerte si stock < seuil
    
    @OneToMany(mappedBy = "medicament")
    List<MouvementStock> mouvements;
}
```

**Règles métier Medicament :**
- Nom, forme : **OBLIGATOIRES**
- StockActuel : **>= 0**
- SeuilAlerte : **> 0** si défini

---

### Entity Personnel (À FAIRE ⏳)
```java
@Entity
@Table(name = "personnel")
public class Personnel {
    @Id @GeneratedValue
    Long id;
    
    String nom, prenom;
    
    @Enumerated(EnumType.STRING)
    FonctionPersonnel fonction;  // MEDECIN, INFIRMIER, SAGE_FEMME
    
    String specialisation;
    String telephone;
    
    @OneToMany(mappedBy = "personnel")
    List<Consultation> consultations;
}
```

---

### Entity Vaccination (À FAIRE ⏳)
```java
@Entity
@Table(name = "vaccinations")
public class Vaccination {
    @Id @GeneratedValue
    Long id;
    
    @ManyToOne
    @JoinColumn(name = "patient_id")
    Patient patient;
    
    @ManyToOne
    @JoinColumn(name = "personnel_id")
    Personnel personnel;
    
    String vaccin;
    LocalDate dateAdministration;
    LocalDate dateRappel;
}
```

---

### Entity Maladie (À FAIRE ⏳)
```java
@Entity
@Table(name = "maladies")
public class Maladie {
    @Id @GeneratedValue
    Long id;
    
    String nom;
    String codeICD10;
    String description;
    Integer prevalence;
}
```

---

### Entity MouvementStock (À FAIRE ⏳)
```java
@Entity
@Table(name = "mouvements_stock")
public class MouvementStock {
    @Id @GeneratedValue
    Long id;
    
    @ManyToOne
    @JoinColumn(name = "medicament_id")
    Medicament medicament;
    
    @Enumerated(EnumType.STRING)
    TypeMouvement type;  // ENTREE, SORTIE
    
    Integer quantite;
    LocalDateTime dateMouvement;
    String motif;
}
```

---

## API PATIENTSERVICE (RÉFÉRENCE)

### Méthodes disponibles
**Fichier :** `src/main/java/com/healthcenter/service/PatientService.java`

| Méthode | Transaction | Paramètres | Retour | Validation |
|---------|------------|-----------|--------|------------|
| `creerPatient` | @Transactional | PatientDTO | Patient | Champs obligatoires + unicité numeroCarnet |
| `obtenirTousLesPatients` | readOnly | - | List<Patient> | - |
| `obtenirPatientParId` | readOnly | Long id | Optional<Patient> | - |
| `rechercherParNom` | readOnly | String nom | List<Patient> | - |
| `rechercherParNumeroCarnet` | readOnly | String carnet | Optional<Patient> | - |
| `mettreAJourPatient` | @Transactional | Long id, PatientDTO | Patient | Validation + vérif existence |
| `supprimerPatient` | @Transactional | Long id | void | Cascade delete consultations |
| `compterParSexe` | readOnly | Sexe | long | - |
| `obtenirPatientsNesApres` | readOnly | LocalDate | List<Patient> | - |

**Utilise PatientService comme modèle pour tous les autres Services.**

---

## FEUILLE DE ROUTE PROJET

### Phase 1 : Fondations (TERMINÉE ✅)
**Durée :** 1 semaine  
**Status :** Complétée le 2026-01-25

#### Tâches accomplies
- [x] Setup environnement (Maven, PostgreSQL, JavaFX)
- [x] Configuration Spring Boot + Hibernate
- [x] Architecture Clean validée
- [x] Module Patient complet (CRUD + UI)
- [x] Tests unitaires PatientService
- [x] Documentation projet (README, CONTEXT, LOG)

---

### Phase 2 : Modules Métier (EN COURS 🔄)

#### Module Consultations (Priorité 1)

**Tâches :**
- [x] Entity Consultation + Maladie
- [x] ConsultationRepository
- [x] ConsultationDTO
- [x] ConsultationService (CRUD + validation)
- [x] ConsultationController + FXML liste
- [x] Formulaire création consultation
- [x] Relation N-N avec Medicaments
- [x] Tests ConsultationService

**Fonctionnalités :**
→ Créer consultation pour patient
→ Lister consultations par patient
→ Rechercher par date
→ Modifier/supprimer
→ Associer médicaments prescrits

---

#### Module Médicaments + Stock (Priorité 2)

**Tâches :**
- [ ] Entity Medicament + MouvementStock
- [ ] MedicamentRepository
- [ ] MedicamentService (CRUD + stock)
- [ ] MedicamentController + FXML
- [ ] Alertes stock faible
- [ ] Historique mouvements
- [ ] Déduction auto lors consultation

---

#### Module Personnel (Priorité 3)

**Tâches :**
- [ ] Entity Personnel
- [ ] PersonnelRepository
- [ ] PersonnelService
- [ ] PersonnelController + FXML

---

#### Module Vaccinations (Priorité 4)

**Tâches :**
- [ ] Entity Vaccination
- [ ] VaccinationRepository
- [ ] VaccinationService
- [ ] VaccinationController + FXML
- [ ] Système rappels vaccins

---

### Phase 3 : Statistiques & Reporting (À FAIRE ⏳)

**Tâches :**
- [ ] Dashboard statistiques
- [ ] Graphiques JavaFX
- [ ] Export PDF/Excel
- [ ] Rapport mensuel auto

---

### Phase 4 : Polish & Production (À FAIRE ⏳)

**Tâches :**
- [ ] Multi-utilisateurs (login)
- [ ] Backup auto DB
- [ ] Logs audit
- [ ] Packaging JAR
- [ ] Documentation finale

---

## CHANGELOG

### 2026-01-25
- ✅ **Module Patient** : CRUD complet + UI
- ✅ **Architecture** : Clean Architecture validée
- ✅ **Tests** : PatientService tests unitaires
- ✅ **Documentation** : README + CONTEXT + LOG
```

