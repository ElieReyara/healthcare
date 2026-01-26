# ✅ Checklist Intégration Nouveau Module

> **Utilité:** Guide étape par étape pour intégrer un module sans oublier d'étapes  
> **Impact:** Évite 100% des erreurs d'intégration (module invisible, navigation cassée)

---

## 📋 PHASE 1: ANALYSE PRÉALABLE (OBLIGATOIRE)

**Avant de générer une seule ligne de code:**

```
□ Lire SERVICES_API.md pour connaître les méthodes existantes
□ Lire ENTITIES_STRUCTURE.md pour connaître les champs des entités
□ Faire grep_search sur les services qui seront utilisés
□ Noter les signatures EXACTES des méthodes (ne rien inventer)
□ Vérifier les types de retour (List vs Map, LocalDate vs LocalDateTime)
```

**Temps estimé:** 10 minutes | **Erreurs évitées:** ~60 erreurs de compilation

---

## 📋 PHASE 2: GÉNÉRATION DU CODE BACKEND

### 2.1 Enums (si nécessaires)
```
□ Créer dans src/main/java/com/healthcenter/domain/enums/
□ Utiliser des noms en MAJUSCULES
□ Ajouter toString() si besoin d'affichage lisible
```

### 2.2 DTOs (Data Transfer Objects)
```
□ Créer dans src/main/java/com/healthcenter/dto/
□ Utiliser constructeur vide + getters/setters
□ Vérifier les types (Map<String,Long> pour répartitions, List pour évolutions)
□ Pas de logique métier dans les DTOs
```

### 2.3 Repository
```
□ Créer dans src/main/java/com/healthcenter/repository/
□ Extends JpaRepository<Entity, Long>
□ Annoter avec @Repository
□ Ajouter méthodes de requête custom si besoin
```

### 2.4 Service
```
□ Créer dans src/main/java/com/healthcenter/service/
□ Annoter avec @Service
□ Injecter les repositories avec @Autowired
□ Utiliser les noms de méthodes EXACTS (voir SERVICES_API.md)
□ Respecter les types de retour
□ Gérer les Optional correctement
```

**Temps estimé:** 1-2 heures | **Erreurs évitées:** ~35 erreurs méthodes + 25 erreurs champs

---

## 📋 PHASE 3: GÉNÉRATION DU CODE FRONTEND

### 3.1 Contrôleur JavaFX
```
□ Créer dans src/main/java/com/healthcenter/controller/
□ Annoter avec @Component (pas @Controller si JavaFX)
□ Injecter les services avec @Autowired
□ Initialiser dans @FXML initialize()
□ Gérer les événements avec @FXML handleXXX()
□ Afficher les erreurs à l'utilisateur (Alert)
```

### 3.2 Fichier FXML
```
□ Créer dans src/main/resources/fxml/
□ Définir fx:controller="com.healthcenter.controller.MonController"
□ Lier les fx:id avec les @FXML du contrôleur
□ Définir les onAction pour les boutons
□ Utiliser un layout cohérent (BorderPane, VBox, GridPane)
```

**Temps estimé:** 2-3 heures | **Erreurs évitées:** Contrôleur non trouvé, binding manquant

---

## 📋 PHASE 4: INTÉGRATION AU MENU (CRITIQUE)

### 4.1 Fichier main-menu.fxml
```
□ Ouvrir src/main/resources/fxml/main-menu.fxml
□ Ajouter MenuItem dans le Menu "Modules"
   <MenuItem text="🔷 MonModule" onAction="#handleMonModule"/>
□ Utiliser un emoji pertinent pour le module
```

### 4.2 MainMenuController
```
□ Ouvrir src/main/java/com/healthcenter/controller/MainMenuController.java
□ Ajouter handler:
   @FXML
   private void handleMonModule() {
       chargerMonModule();
   }
□ Ajouter méthode de chargement:
   private void chargerMonModule() {
       chargerVue("/fxml/mon-module.fxml", "MonModule");
   }
```

### 4.3 Dialogue "À propos"
```
□ Dans MainMenuController.handleAPropos()
□ Ajouter ligne dans alert.setContentText():
   "✅ MonModule - Description courte\n"
```

### 4.4 Page d'accueil main-menu.fxml
```
□ Ajouter Label dans la VBox des modules:
   <Label text="✅ MonModule - Description"
          style="-fx-font-size: 14px;"/>
```

**Temps estimé:** 10 minutes | **Impact:** Module visible et accessible ⭐⭐⭐⭐⭐

---

## 📋 PHASE 5: TESTS UNITAIRES

### 5.1 Tests des Services
```
□ Créer dans src/test/java/com/healthcenter/service/
□ Utiliser @ExtendWith(MockitoExtension.class)
□ Mock les repositories avec @Mock
□ Inject le service avec @InjectMocks
□ COPIER les noms de méthodes exacts du code de production
□ Ne JAMAIS utiliser setId() sur les entités
□ Utiliser lenient() pour les mocks optionnels
□ Vérifier que tous les mocks retournent des objets valides (pas null)
```

### 5.2 Tests des Contrôleurs (optionnel)
```
□ Tester uniquement la logique métier
□ Mock les services
□ Ne pas tester l'UI JavaFX (complexe)
```

**Temps estimé:** 1-2 heures | **Erreurs évitées:** 30 erreurs de test

---

## 📋 PHASE 6: COMPILATION ET VALIDATION

### 6.1 Compilation
```
□ Exécuter: mvn clean compile
□ Vérifier 0 erreur de compilation
□ Si erreurs: vérifier noms de méthodes dans SERVICES_API.md
□ Si erreurs: vérifier champs dans ENTITIES_STRUCTURE.md
```

### 6.2 Tests
```
□ Exécuter: mvn test
□ Vérifier tous les tests passent
□ Couvrir au moins 80% du code métier
```

### 6.3 Test Manuel
```
□ Lancer: mvn spring-boot:run
□ Vérifier module visible dans menu
□ Tester navigation vers le module
□ Tester fonctionnalités principales
□ Vérifier gestion des erreurs
```

**Temps estimé:** 30 minutes | **Criticité:** ⭐⭐⭐⭐⭐

---

## 📋 PHASE 7: DOCUMENTATION

### 7.1 Mise à jour CONTEXT.md
```
□ Incrémenter version (ex: 2.2.0 → 2.3.0)
□ Ajouter module dans "Modules Disponibles"
□ Décrire fonctionnalités principales
□ Mentionner dépendances ajoutées si applicable
```

### 7.2 Mise à jour README.md
```
□ Ajouter module dans la liste des fonctionnalités
□ Ajouter captures d'écran si pertinent
□ Mettre à jour instructions si nécessaire
```

### 7.3 Journal de Modifications
```
□ Créer entrée dans logs/LOG.md
□ Date, version, description
□ Lister fichiers créés/modifiés
□ Mentionner dépendances ajoutées
```

**Temps estimé:** 20 minutes | **Impact:** Traçabilité et maintenance

---

## 📋 PHASE 8: DÉPENDANCES (si nécessaire)

### Si PDF nécessaire
```
□ Ajouter dans pom.xml:
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
□ ⚠️ NE PAS utiliser type=pom
```

### Si Excel nécessaire
```
□ Ajouter dans pom.xml:
   <dependency>
       <groupId>org.apache.poi</groupId>
       <artifactId>poi-ooxml</artifactId>
       <version>5.2.5</version>
   </dependency>
```

### Si graphiques nécessaires
```
□ JavaFX Charts inclus par défaut
□ Vérifier dépendance javafx-controls présente
```

---

## 🎯 CHECKLIST RAPIDE (À IMPRIMER)

**Backend:**
- [ ] Enums créées et valides
- [ ] DTOs avec bons types (Map vs List)
- [ ] Repository avec @Repository
- [ ] Service avec noms méthodes corrects
- [ ] Pas d'invention de méthodes

**Frontend:**
- [ ] Contrôleur avec @Component
- [ ] FXML avec fx:controller correct
- [ ] fx:id liés aux @FXML

**Intégration:**
- [ ] MenuItem dans main-menu.fxml
- [ ] Handler dans MainMenuController
- [ ] Méthode chargerXXX() ajoutée
- [ ] "À propos" mis à jour
- [ ] Page accueil mise à jour

**Tests:**
- [ ] Tests avec noms corrects
- [ ] Pas de setId() sur entités
- [ ] Mocks retournent objets valides
- [ ] Tests compilent
- [ ] Tests passent (green)

**Validation:**
- [ ] mvn clean compile → SUCCESS
- [ ] mvn test → Tests run: X, Failures: 0
- [ ] mvn spring-boot:run → Module visible
- [ ] Navigation fonctionne
- [ ] Fonctionnalités testées

**Documentation:**
- [ ] CONTEXT.md mis à jour
- [ ] README.md mis à jour
- [ ] LOG.md avec entrée

---

## 🚨 ERREURS CRITIQUES À ÉVITER

| ❌ ERREUR | ✅ SOLUTION | Gain de Temps |
|----------|------------|---------------|
| Inventer noms de méthodes | Consulter SERVICES_API.md | 45 min |
| Deviner noms de champs | Consulter ENTITIES_STRUCTURE.md | 30 min |
| Oublier MenuItem | Checklist Phase 4 | 5 min |
| Tests avec erreurs code | Générer après correction code | 35 min |
| setId() dans tests | Jamais utiliser setId() | 10 min |

**Total temps gagné avec checklist:** ~2h30 par module

---

## 📚 RESSOURCES

- **SERVICES_API.md** → Noms exacts des méthodes de services
- **ENTITIES_STRUCTURE.md** → Structure des entités et DTOs
- **AUDIT_ERREURS_MODULE_STATISTIQUES.md** → Erreurs à éviter

**Dernière mise à jour:** 26 janvier 2026  
**Testé avec:** Module Statistiques & Rapports (42/42 tests ✅)
