# INSTRUCTIONS COPILOT - Gestion Poste Santé


--- 
## GuideLine
- Le symbole '##' indique une nouvelle section.
- Les listes à puces commencent par '- ' ou '→'.
- Les sous-points d'une listes à puces commencent par '* '.
- Les extraits de code sont entourés de triples backticks (```)
- Les instructions sont en français.
- Le symbole '###' indique un sous-titre.
- Le symbole '---' indique une séparation entre sections.


---


## AVANT DE GÉNÉRER DU CODE


Tu DOIS lire ces fichiers obligatoirement :
- `CONTEXT.md` (racine du projet) → Modèle de données + APIs existantes + Feuille de route
- `logs/LOG.md` (dossier logs) → Historique modifications récentes
- Consulte le Code existant similaire et garde un resume en contexte pour etre sur de produire du code coherent 


---


## STRUCTURE PROJET (Où créer les fichiers)


### Arborescence complète
```
gestion-poste-sante/
├── src/main/java/com/healthcenter/
│   ├── domain/
│   │   ├── entities/           → Créer Entity ici (Patient.java)
│   │   └── enums/             → Créer Enum ici (Sexe.java)
│   ├── repository/            → Créer Repository ici (PatientRepository.java)
│   ├── service/               → Créer Service ici (PatientService.java)
│   ├── controller/            → Créer Controller ici (PatientController.java)
│   ├── dto/                   → Créer DTO ici (PatientDTO.java)
│   ├── config/                → Config Spring/Hibernate
│   ├── exception/             → Exceptions custom
│   └── util/                  → Classes utilitaires
│
├── src/main/resources/
│   ├── fxml/                  → Créer FXML ici (patient-list.fxml)
│   └── application.properties → Config DB
│
├── src/test/java/com/healthcenter/
│   ├── service/               → Tests Service (PatientServiceTest.java)
│   └── repository/            → Tests Repository
│
├── logs/                      → LOG.md ici (détails modifications)
├── CONTEXT.md                 → Documentation projet (racine)
├── README.md                  → Doc utilisateur (racine)
└── pom.xml                    → Config Maven
```

### Règles création fichiers
→ **Toujours préciser le chemin complet** dans tes réponses
→ **Respecter la structure** sauf si contrainte technique impose alternative
→ **Si déviation nécessaire** : documenter la raison dans LOG.md


---


## RÈGLES ARCHITECTURE 


### Flow 
Controller (JavaFX)
→ appelle Service (avec DTO)
→ appelle Repository (avec Entity)
→ Database



### Interdictions
- ❌ PAS de logique métier dans Controller JavaFX
- ❌ PAS d'accès direct Repository depuis Controller
- ❌ PAS de Entity dans Controller (utilise DTO)
- ❌ PAS de SQL brut (uniquement JPA)
- ❌ PAS de code dupliqué
  * Si même logique 2× → créer méthode privée helper
- ❌ PAS de magic numbers/strings
  * Créer constantes : `public static final String DB_NAME = "healthcenter_db";`
- ❌ PAS de catch vide :
  ```java
  // ❌ INTERDIT
  catch (Exception e) { }


  // ✅ CORRECT
  catch (Exception e) {
      // Log l'erreur ou throw une exception custom
      throw new RuntimeException("Message clair", e);
  }
  ```
### Pratiques obligatoires
- Logique → Service Layer obligatoirement
- Mapping Entity ↔ DTO dans Service Layer
- Utilise annotations Spring : @Service, @Repository, @Transactional
- Controllers = gestion événements UI uniquement
- Créer l'Entity avec annotations JPA complètes
- Créer le Repository (interface JpaRepository)
- Créer le DTO (objet transfert sans annotations JPA)
- Créer le Service avec validation métier
- Créer le Controller + FXML
- Créer les tests (JUnit Service minimum)
- Pour chaque Entity : 
 * @Id + @GeneratedValue(strategy = IDENTITY)
 * @Entity + @Table(name = "nom_table")
 * Constructeur vide obligatoire pour JPA
 * Relations : @OneToMany, @ManyToOne avec mappedBy correct
 * Pas de @Data Lombok (explicite getters/setters pour apprendre)


- Pour chaque Service :
  * Annotation @Service
  * Injection @Autowired private XRepository repository;
  * Méthodes @Transactional pour modifications DB
  * Méthodes @Transactional(readOnly = true) pour lectures
  * Validation métier AVANT appel repository
  * Lever IllegalArgumentException avec message clair si erreur


- Pour chaque Controller JavaFX :
  * Annotation @Component (pas @Controller Spring MVC)
  * @FXML pour chaque composant UI
  * initialize() pour setup initial
  * Méthodes handlers privées (private void handleXxx())
  * Pas de logique métier (appel service uniquement)


- Documentation Code :
  * Commentaires Javadoc (/** */) pour classes publiques
  * Inline comments pour logique complexe uniquement
  * README.md mis à jour après chaque module


---
## CREATTION DE TESTS UNITAIRES JUNIT POUR SERVICE
**Chemin :** `src/main/java/com/healthcenter/service/NomServiceTest.java`

```java
@SpringBootTest
public class NomServiceTest {
    @Autowired
    private NomService service;
    
    @Test
    public void testCreer_Succes() {
        NomDTO dto = new NomDTO();
        dto.setChamp("valeur");
        
        Nom resultat = service.creer(dto);
        
        assertNotNull(resultat.getId());
        assertEquals("valeur", resultat.getChamp());
    }
    
    @Test
    public void testCreer_Echec_ChampNull() {
        NomDTO dto = new NomDTO();
        dto.setChamp(null);
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.creer(dto);
        });
        
        assertEquals("Message clair", exception.getMessage());
    }
}
```
---

## TEMPLATE SERVICE (Copier pour chaque nouveau Service)


**Chemin :** `src/main/java/com/healthcenter/service/NomService.java`

```java
@Service
public class NomService {
    @Autowired
    private NomRepository repository;
    
    @Transactional
    public Nom creer(NomDTO dto) {
        // 1. Validation
        if (dto.getChamp() == null) {
            throw new IllegalArgumentException("Message clair");
        }
        
        // 2. Mapping DTO → Entity
        Nom entity = new Nom();
        entity.setChamp(dto.getChamp());
        
        // 3. Sauvegarde
        return repository.save(entity);
    }
    
    @Transactional(readOnly = true)
    public List<Nom> obtenirTous() {
        return repository.findAll();
    }
}
```


---
## TEMPLATE ENTITY (Copier pour chaque nouvelle Entity)


**Chemin :** `src/main/java/com/healthcenter/domain/entities/NomEntity.java`

```java
@Entity
@Table(name = "nom_table")
public class NomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String champObligatoire;
    
    // Relations
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private ParentEntity parent;
    
    // Constructeurs
    public NomEntity() {}
    
    // Getters/Setters
}
```
---
## TEMPLATE REPOSITORY


**Chemin :** `src/main/java/com/healthcenter/repository/NomRepository.java`

```java
@Repository
public interface NomRepository extends JpaRepository<NomEntity, Long> {
    // Requêtes custom (JPA génère SQL auto)
    List<NomEntity> findByChampContainingIgnoreCase(String champ);
    
    Optional<NomEntity> findByChampUnique(String valeur);
}
```

---
## TEMPLATE DTO


**Chemin :** `src/main/java/com/healthcenter/dto/NomDTO.java`

```java
public class NomDTO {
    private String champ1;
    private String champ2;
    
    // Constructeurs
    public NomDTO() {}
    
    // Getters/Setters
}
```

---
## TEMPLATE Controller JavaFX


**Chemin :** `src/main/java/com/healthcenter/controller/NomController.java`

```java
@Component
public class NomController {
    @Autowired
    private NomService service;
    
    @FXML private TableView<Nom> table;
    
    @FXML
    public void initialize() {
        // Setup colonnes
        // Charger données
    }
    
    @FXML
    private void handleAction() {
        // Appel service uniquement
    }
}
```

---
## TEMPLATE FXML


**Chemin Liste :** `src/main/resources/fxml/nom-list.fxml`  
**Chemin Formulaire :** `src/main/resources/fxml/nom-form.fxml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>

<BorderPane xmlns:fx="http://javafx.com/fxml/1"
            fx:controller="com.healthcenter.controller.NomController">
    <!-- Structure UI ici -->
</BorderPane>
```

---
## Création nouveau module (checklist)


- Entity : @Entity, @Table, relations JPA → `src/main/java/com/healthcenter/domain/entities/`
- Repository : interface XRepository extends JpaRepository<X, Long> → `src/main/java/com/healthcenter/repository/`
- DTO : POJO sans annotations JPA → `src/main/java/com/healthcenter/dto/`
- Service : @Service, validation métier, @Transactional → `src/main/java/com/healthcenter/service/`
- Controller : @Component, @FXML, handlers privés → `src/main/java/com/healthcenter/controller/`
- FXML : fichier UI ({entity}-list.fxml, {entity}-form.fxml) → `src/main/resources/fxml/`
- Tests : XServiceTest.java (JUnit) → `src/test/java/com/healthcenter/service/`


---


## LOGGING OBLIGATOIRE


### Fichier principal
**Chemin :** `logs/LOG.md`

### Règles gestion logs
→ **Mise à jour obligatoire** après chaque fichier créé/modifié  
→ **Limite :** Si `LOG.md` dépasse **1000 lignes**, créer `logs/LOG-2.md`  
→ **Format numérotation :** `LOG.md`, `LOG-2.md`, `LOG-3.md`, etc.  
→ **Contenu :** Concis mais complet (pas de détails non pertinents)

### Template entrée log
```markdown
### [YYYY-MM-DD HH:mm] - [TYPE] [Nom fichier]
**Action :** [Description courte]
**Chemin :** [Chemin complet]
**Détails :**
- [Point technique 1]
- [Point technique 2]
**Status :** [✅ Compilé | ⚠️ Corrigé | 🔄 En cours]
***
```

### Types actions valides
- **CREATE** : Nouveau fichier
- **UPDATE** : Modification existant
- **REFACTOR** : Refactoring code
- **FIX** : Correction bug
- **TEST** : Tests ajoutés/modifiés
- **DELETE** : Suppression fichier
- **DOC** : Documentation ajoutée/modifiée
- **Autre** : A preciser clairement

### Exemple entrée log
```markdown
### 2026-01-25 21:30 - CREATE Consultation.java
**Action :** Création Entity Consultation avec relation Patient
**Chemin :** src/main/java/com/healthcenter/domain/entities/Consultation.java
**Détails :**
- Champs : patient, dateConsultation, symptomes, diagnostic, prescription
- Relation @ManyToOne vers Patient (@JoinColumn patient_id)
- Constructeur vide JPA
**Status :** ✅ Compilé
***
```

### Gestion déviations
**Si tu sors du template/structure standard :**
```markdown
**⚠️ DÉVIATION TEMPLATE**
**Raison :** [Pourquoi standard inadapté]
**Solution :** [Ce qui a été fait]
**Justification :** [Référence best practice ou contrainte technique]
```


---


## CONVENTIONS NOMMAGE


- Entity : Patient.java (singulier)
- Repository : PatientRepository.java
- Service : PatientService.java
- Controller : PatientController.java
- DTO : PatientDTO.java
- FXML : patient-list.fxml (kebab-case)


---


## VÉRIFICATIONS OBLIGATOIRES


Après génération, vérifie :
 - Service a bien @Service et @Transactional ?
 - Repository est une interface (pas classe) ?
 - Controller utilise DTO (pas Entity) ?
 - Pas de code dupliqué ?
 - LOG.md mis à jour ?
 - Chemin fichier correct ?


---
## README.MD MISE À JOUR OBLIGATOIRE
**Chemin :** `README.md` (racine projet)

- Créer ou mettre à jour README.md après chaque module
- Inclure sections :
  * Description module
  * Instructions utilisation (UI + API)
  * Exemples code (si pertinent)
  * Dépendances (librairies externes utilisées)
  * Tests (comment exécuter les tests unitaires)

---

## FLEXIBILITÉ AUTORISÉE


**Tu peux dévier des templates/structure SI :**
→ Contrainte technique impose alternative  
→ Best practice industrie suggère autre approche  
→ Contexte spécifique rend template inadapté  
→ Toute action qui t'oblige a sortir du scope ou qui n'est pas couverte par les instructions mais toujours en respectant les bonnes pratiques de codage. 
→ Si necessaire precise si certaines instructions ne sont pas pertinentes ou manquantes et propose une alternative adaptée au contexte.

**Dans ce cas :**
1. Applique la solution alternative
2. Documente dans `logs/LOG.md` avec section `⚠️ DÉVIATION TEMPLATE`
3. Justifie avec référence (doc officielle, best practice connue)


