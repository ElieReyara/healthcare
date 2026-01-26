## 🤖 AI-Driven Development Showcase

Ce projet démontre une approche **Documentation-Driven Development** avec orchestration GitHub Copilot.

### Méthodologie
→ **Guardrails stricts** : `.github/copilot-instructions.md` définit les règles architecture  
→ **Context living doc** : `CONTEXT.md` maintient le modèle de données + roadmap  
→ **Audit trail** : `logs/LOG.md` trace chronologiquement chaque modification  
→ **Clean Architecture** : Séparation couches (UI, Service, Repository, Domain)

### Workflow orchestration
1. Mise à jour `CONTEXT.md` (module en cours)
2. Prompt structuré à Copilot (avec références fichiers)
3. Validation manuelle (compilation + tests)
4. Logging automatique dans `logs/LOG.md`
5. Commit Git après validation complète

### Résultat
✅ Code cohérent et maintenable  
✅ Architecture scalable  
✅ Documentation synchronisée avec le code  
✅ Traçabilité complète des décisions techniques

**Voir `.github/copilot-instructions.md` pour les détails d'implémentation.**

---

## 💊 Module Médicaments + Stock

### Fonctionnalités
- ✅ CRUD complet pour médicaments (Nom, Dosage, Forme, Prix, Stock, Seuil)
- ✅ Ajustement stock (ENTRÉE/SORTIE) avec traçabilité automatique
- ✅ Alertes visuelles stock faible (⚠️ en rouge si stock < seuil)
- ✅ Historique complet des mouvements avec stockAvant/stockApres
- ✅ Recherche par nom + filtrage par forme pharmaceutique

### Utilisation

#### Créer un médicament
1. Menu **Modules > Médicaments**
2. Cliquer **➕ Nouveau**
3. Remplir : Nom*, Dosage*, Forme*, Prix*, Stock initial, Seuil d'alerte
4. **Enregistrer**

#### Ajuster le stock
1. Sélectionner un médicament dans la liste
2. Cliquer **📦 Ajuster Stock**
3. Choisir Type (ENTRÉE = ajout, SORTIE = retrait)
4. Saisir Quantité + Motif (optionnel)
5. **Valider** → Le système crée automatiquement un mouvement avec stockAvant/stockApres

#### Consulter l'historique
1. Sélectionner un médicament
2. Cliquer **📊 Historique**
3. Voir tous les mouvements avec détails complets

#### Alertes stock faible
- La colonne **Statut** affiche :
  - ✅ **OK** (vert) si stock >= seuil
  - ⚠️ **ALERTE** (rouge) si stock < seuil
- Permet d'identifier rapidement les médicaments à réapprovisionner

### Architecture technique
- **Enums** : `FormeMedicament` (COMPRIME, SIROP, INJECTION, POMMADE, GELULE), `TypeMouvement` (ENTREE, SORTIE)
- **Entities** : `Medicament` (avec @OneToMany mouvements), `MouvementStock` (avec @ManyToOne medicament)
- **Repositories** : Requêtes JPQL custom (findMedicamentsEnRuptureStock, etc.)
- **Services** : `MedicamentService.ajusterStock()` avec transaction atomique (calcul stock + création mouvement)
- **Controllers** : TableView avec CellFactory custom pour colStatut, dialog ajustement inline
- **Tests** : 8 tests unitaires JUnit 5 (dont test critique stock insuffisant)

---

##  Module Vaccinations + Calendrier Vaccinal

### Fonctionnalités
-  Enregistrement vaccinations avec calcul automatique des rappels
-  Calendrier vaccinal Sénégal (16 vaccins officiels)
-  Alertes visuelles rappels en retard () et prochains 7 jours ()
-  Carnet vaccinal complet par patient (vaccins faits, manquants, prévus)
-  Détection automatique vaccins manquants selon âge du patient
-  Statistiques de couverture vaccinale (%)
-  Filtrage par patient, vaccin, rappels seulement
-  Coloration visuelle des statuts ( vert,  orange,  rouge)

### Utilisation

#### Enregistrer une vaccination
1. Menu **Modules > Vaccinations**
2. Cliquer ** Nouvelle Vaccination**
3. Sélectionner **Patient** + **Vaccin**
4. Voir **Info calendrier** (âge recommandé, nombre de rappels)
5. Date d'administration (par défaut = aujourd'hui)
6. **Option 1** : Cocher "Calcul auto"  Le système calcule la date de rappel automatiquement selon le calendrier
7. **Option 2** : Décocher et saisir manuellement la date de rappel
8. Saisir Numéro de lot + Observations (optionnel)
9. **Enregistrer**

#### Consulter le carnet vaccinal d'un patient
1. Sélectionner un patient dans la liste
2. Cliquer ** Carnet Vaccinal**
3. Voir tous les vaccins :
   -  **Fait** (vert) : Vaccin administré
   -  **Rappel prévu** (orange) : Rappel planifié dans moins de 7 jours
   -  **Rappel en retard** (rouge) : Rappel passé, action requise
   -  **Non fait (obligatoire)** (rouge) : Vaccin manquant selon âge + calendrier
   -  **Pas encore requis** (gris) : Vaccin pas encore nécessaire selon l'âge
4. Imprimer le carnet (fonctionnalité à venir)

#### Gérer les rappels
- **Rappels en retard** : Cliquer bouton ** Rappels en retard**
  * Filtre automatique les vaccinations avec rappel passé
  * Alerte si des rappels sont trouvés
- **Rappels prochains (7j)** : Cliquer bouton ** Rappels prochains**
  * Filtre automatique les vaccinations avec rappel dans les 7 prochains jours
  * Affiche le nombre de rappels à venir

#### Calendrier vaccinal du Sénégal
Le système utilise le calendrier vaccinal officiel du Sénégal :
- **BCG** : À la naissance (0 jours)
- **POLIO** : Série de 4 doses (naissance, 6, 10, 14 semaines)
- **PENTA** : 3 doses à 6, 10, 14 semaines + 2 rappels espacés de 28 jours
- **PNEUMO** : 3 doses à 6, 10, 14 semaines
- **ROTA** : 2 doses à 6 et 10 semaines
- **VAR** (Rougeole) : À 9 mois (270 jours)
- **FIÈVRE JAUNE** : À 9 mois (270 jours)
- **MÉNINGITE** : À 9 mois (270 jours)

Le calendrier est automatiquement initialisé au démarrage de l'application.

### Architecture technique
- **Enums** : `TypeVaccin` (16 vaccins), `StatutVaccination` (ADMINISTRE, RAPPEL_PREVU, RAPPEL_EN_RETARD)
- **Entities** : 
  * `Vaccination` (avec @ManyToOne Patient, méthodes isRappelProche/isRappelEnRetard)
  * `CalendrierVaccinal` (référentiel avec âges recommandés, délais rappels)
- **Repositories** : 
  * `VaccinationRepository` avec JPQL (findRappelsEnRetard, findRappelsProchains, compterParTypeVaccin)
  * `CalendrierVaccinalRepository` (findByVaccin, findByObligatoireTrue)
- **Services** : 
  * `VaccinationService` : CRUD, calcul auto rappels, carnet vaccinal, détection vaccins manquants, stats couverture
  * `CalendrierVaccinalService` : Initialisation données Sénégal avec méthode `initialiserCalendrierDefaut()`
- **Controllers** : 
  * `VaccinationController` (liste + filtres + alertes)
  * `VaccinationFormController` (création/édition + calcul auto)
  * `CarnetVaccinalController` (carnet complet patient)
- **Tests** : 10 tests unitaires JUnit 5 (validation DTO, calcul rappels, détection manquants, stats)
- **Initialisation** : `VaccinationDataInitializer` (@PostConstruct) pour peupler le calendrier au démarrage
