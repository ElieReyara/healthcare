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
---

## 👥 Module Personnel Médical

### Fonctionnalités
- ✅ Gestion complète du personnel médical (8 fonctions)
- ✅ Planning hebdomadaire par jour avec créneaux horaires
- ✅ Statistiques d'activité (total consultations, ce mois, cette semaine)
- ✅ Fiche détaillée avec 3 onglets (Statistiques, Planning, Historique)
- ✅ Activation/Désactivation (soft delete)
- ✅ Validation email + téléphone format Sénégal
- ✅ Filtrage par fonction + recherche nom/prénom
- ✅ Vérification disponibilité jour/heure

### Utilisation

#### Créer un personnel
1. Menu **Modules > Personnel**
2. Cliquer **➕ Nouveau**
3. Remplir les champs obligatoires :
   - Nom* et Prénom*
   - Fonction* : MEDECIN, INFIRMIER, SAGE_FEMME, AIDE_SOIGNANT, PHARMACIEN, TECHNICIEN_LABO, GESTIONNAIRE, RECEPTIONNISTE
   - Spécialisation (activée automatiquement pour MEDECIN, ex: Pédiatre, Chirurgien)
4. Coordonnées :
   - Téléphone : Formats acceptés `77 123 45 67`, `771234567`, `+221771234567`
   - Email : Format standard `email@example.com`
   - Adresse complète
5. Administration :
   - N° Matricule : Identifiant unique (ex: MAT-2024-001)
   - Date Embauche : Date d'entrée en fonction
   - Statut : Actif par défaut
6. **Enregistrer**

#### Consulter la fiche détaillée
1. Sélectionner un personnel dans la liste
2. Cliquer **👁️ Détails**
3. Voir les 3 onglets :
   - **📊 Statistiques** : Total consultations, ce mois, cette semaine
   - **📅 Planning** : Disponibilités par jour (Lundi-Dimanche) avec créneaux horaires
   - **📝 Historique** : Liste complète des consultations effectuées

#### Gérer le planning hebdomadaire
1. Ouvrir fiche détaillée d'un personnel
2. Onglet **Planning**
3. Cliquer **✏️ Modifier Planning**
4. Définir les disponibilités par jour :
   - Format créneaux : `08:00-12:00, 14:00-17:00`
   - Plusieurs créneaux par jour possibles
   - Périodes de validité (dateDebut/dateFin)

#### Activer/Désactiver un personnel
- **Désactiver** : Cliquer **🔒 Désactiver** (personnel inactif, conserve historique)
- **Activer** : Cliquer **✅ Activer** (réactivation d'un personnel inactif)
- Filtre **Actifs uniquement** activé par défaut dans la liste

#### Filtrer et rechercher
- **Par fonction** : Sélectionner dans ComboBox (affiche les libellés français)
- **Par nom/prénom** : Saisir dans champ recherche (insensible casse)
- **Par statut** : Cocher/décocher "Actifs uniquement"

### Fonctions médicales disponibles
| Code | Libellé | Est Médical |
|------|---------|-------------|
| MEDECIN | Médecin | ✅ |
| INFIRMIER | Infirmier(ère) | ✅ |
| SAGE_FEMME | Sage-femme | ✅ |
| AIDE_SOIGNANT | Aide-soignant(e) | ✅ |
| PHARMACIEN | Pharmacien(ne) | ❌ |
| TECHNICIEN_LABO | Technicien de laboratoire | ❌ |
| GESTIONNAIRE | Gestionnaire | ❌ |
| RECEPTIONNISTE | Réceptionniste | ❌ |

### Validations appliquées
- **Nom, Prénom, Fonction** : Obligatoires
- **Email** : Regex `^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$`
- **Téléphone Sénégal** : Regex `^(\\+221)?[37][0378]\\d{7}$` (accepte préfixes 77/33/70/76/78)
- **Matricule** : Unique dans la base
- **Spécialisation** : Activée uniquement pour MEDECIN

### Architecture technique
- **Enums** : 
  * `FonctionPersonnel` (8 valeurs avec libellé + estMedical)
  * `JourSemaine` (7 jours avec ordre + DayOfWeek)
- **Entities** : 
  * `Personnel` (avec @OneToMany vers Consultation/Vaccination/DisponibilitePersonnel)
  * `DisponibilitePersonnel` (planning avec JourSemaine + créneaux + périodes)
- **Repositories** : 
  * `PersonnelRepository` avec JPQL (findByFonctionsActifs, compterConsultationsParPersonnel)
  * `DisponibilitePersonnelRepository` (findDisponibilitesActives avec validation dates)
- **Services** : 
  * `PersonnelService` : CRUD, statistiques (obtenirTopPersonnelActif, compterParFonction), disponibilités (estDisponible), activation/désactivation
  * `DisponibilitePersonnelService` : Gestion planning (definirPlanning parse format "HH:mm-HH:mm")
- **Controllers** : 
  * `PersonnelController` (liste TableView 9 cols, CellFactory statut ✅/❌, filtres)
  * `PersonnelFormController` (validation email + phone Sénégal, fonction-based fields)
  * `PersonnelDetailsController` (3 tabs: Stats/Planning/Historique consultations)
- **Tests** : 10 tests unitaires Mockito (CRUD, validation, statistiques, disponibilités)

---

## 📊 Module Statistiques & Rapports

### Fonctionnalités
- ✅ Dashboard interactif avec 12 indicateurs clés de performance (KPIs)
- ✅ 4 graphiques JavaFX (PieChart + BarChart)
- ✅ Génération rapports PDF (iText) et Excel (Apache POI)
- ✅ Exports CSV multi-formats
- ✅ Filtrage temporel avancé (6 périodes)
- ✅ Statistiques détaillées (patients, consultations, vaccinations, médicaments, personnel)
- ✅ Historique rapports générés

### Utilisation

#### Accéder au dashboard
1. Menu **Modules > Statistiques & Rapports**
2. Le tableau de bord affiche automatiquement :
   - **Patients** : Total + Nouveaux ce mois (tuile bleue)
   - **Consultations** : Total + Ce mois + Cette semaine + Aujourd'hui (tuiles vertes/oranges)
   - **Vaccinations** : Total + Ce mois (tuile violette)
   - **Médicaments** : Stock total + En rupture (tuile rouge)
   - **Personnel** : Actifs (tuile orange)
3. Cliquer **🔄 Actualiser** pour rafraîchir les données
4. Visualiser les 4 graphiques :
   - **Répartition par sexe** (camembert)
   - **Consultations 6 derniers mois** (barres)
   - **Maladies fréquentes** (barres top 10)
   - **Couverture vaccinale** (camembert %)

#### Générer un rapport
1. Cliquer **📄 Générer Rapport** depuis le dashboard
2. Sélectionner :
   - **Type de rapport** : Activité globale, Consultations, Vaccinations, Médicaments, Personnel, Maladies fréquentes
   - **Période** : Aujourd'hui, Semaine, Mois, Trimestre, Année, Personnalisée (avec sélection dates)
   - **Format** : PDF ou Excel (XLSX)
3. Cliquer **👁️ Afficher aperçu** pour prévisualiser le contenu (optionnel)
4. Cliquer **📥 Générer**
5. Choisir l'emplacement de sauvegarde
6. Une barre de progression s'affiche pendant la génération
7. À la fin, confirmer si vous voulez ouvrir le fichier automatiquement

#### Exporter des données en CSV
1. Depuis le dashboard, cliquer **💾 Exporter**
2. Ou depuis Statistiques Détaillées, cliquer **💾 Exporter CSV**
3. Sélectionner le dossier de destination
4. Le système génère les fichiers CSV (séparateur ";", format français)

#### Consulter les statistiques détaillées
1. Cliquer **📈 Statistiques Détaillées**
2. Sélectionner :
   - **Catégorie** : Patients, Consultations, Vaccinations, Médicaments, Personnel
   - **Période** : Filtrage temporel (aujourd'hui, semaine, mois, etc.)
3. Cliquer **▶️ Appliquer**
4. Explorer les 3 onglets :
   - **📊 Synthèse** : Indicateurs clés + répartitions
   - **📉 Graphiques** : Visualisations interactives
   - **📋 Données Détaillées** : TableView avec données brutes

### Types de rapports disponibles
| Type | Contenu | Granularité temporelle |
|------|---------|----------------------|
| **ACTIVITE_GLOBALE** | Dashboard complet + toutes les statistiques | Auto (JOUR/SEMAINE/MOIS) |
| **CONSULTATIONS** | Stats consultations + maladies fréquentes + consultations par personnel | Auto |
| **VACCINATIONS** | Stats vaccinations + évolution + couverture vaccinale | Auto |
| **MEDICAMENTS_STOCK** | Stock total, ruptures, valeur totale, top médicaments, alertes | Instantané |
| **PERSONNEL_ACTIVITE** | Stats personnel + consultations/vaccinations par personnel + répartition fonction | Période spécifiée |
| **MALADIES_FREQUENTES** | Top 30 maladies avec répartition + pourcentages | Période spécifiée |

### Granularité temporelle automatique
- **JOUR** : Si période < 30 jours
- **SEMAINE** : Si période entre 30 et 90 jours
- **MOIS** : Si période >= 90 jours

### Tranches d'âge patients
- **0-5 ans** : Petite enfance
- **6-18 ans** : Enfance/Adolescence
- **19-40 ans** : Adulte jeune
- **41-60 ans** : Adulte
- **60+ ans** : Senior

### Architecture technique
- **Enums** :
  * `TypeRapport` (8 valeurs : ACTIVITE_GLOBALE, CONSULTATIONS, VACCINATIONS, MEDICAMENTS_STOCK, PERSONNEL_ACTIVITE, MALADIES_FREQUENTES, MENSUEL, ANNUEL)
  * `FormatRapport` (PDF "pdf", EXCEL "xlsx")
  * `PeriodeStatistique` (6 valeurs avec calculs dates : AUJOURD_HUI, SEMAINE, MOIS, TRIMESTRE, ANNEE, PERSONNALISE)
- **Entity** :
  * `Rapport` (historique : typeRapport, dateGeneration, dateDebut/Fin, nomFichier, formatFichier, generePar, tailleFichier)
- **DTOs** :
  * `DashboardStats` (11 Long + dateGeneration)
  * `RepartitionData` (label, valeur, pourcentage avec calculerPourcentage)
  * `EvolutionData` (periode, valeur, date)
  * `StatistiquesPatients` (nbTotal, repartitionSexe/Age, moyenneAge, nouveauxPatientsMois)
  * `StatistiquesConsultations` (nbTotal, nbPeriode, evolutionTemporelle, maladiesFrequentes, consultationsParPersonnel, moyenneParJour)
- **Repository** :
  * `RapportRepository` (findByTypeRapport, findByDateGenerationBetween, findTop10ByOrderByDateGenerationDesc)
- **Services** (1500+ lignes total):
  * `StatistiqueService` : Agrégation 5 services (@Autowired PatientService, ConsultationService, MedicamentService, VaccinationService, PersonnelService), 13 méthodes publiques
  * `RapportService` : Génération PDF (iText 8.0.3) + Excel (Apache POI 5.2.5), 6 types rapports, async generation
  * `ExportService` : CSV avec séparateur ";", escape automatique, multi-fichiers
- **Controllers** (900+ lignes total):
  * `DashboardController` : 12 Labels KPIs + 4 Charts (PieChart x2, BarChart x2), chargerDashboard(), 4 méthodes graphiques
  * `RapportController` : Form avec 5 ComboBox/DatePickers, aperçu TextArea, génération async avec ProgressBar, confirmation ouverture fichier
  * `StatistiquesDetailsController` : Filtres catégorie/période, TabPane 3 tabs (Synthèse/Graphiques/Données), chargerStatistiques* dynamiques
- **FXML** :
  * `dashboard.fxml` : BorderPane avec 7 tuiles colorées (GridPane 3x3) + 4 charts (GridPane 2x2)
  * `rapport.fxml` : VBox avec GridPane form 5 rows + TextArea aperçu + ProgressBar + buttons
  * `statistiques-details.fxml` : BorderPane avec HBox filters TOP + TabPane CENTER + HBox buttons BOTTOM
- **Tests** : 13 tests unitaires (8 StatistiqueService + 5 RapportService avec @TempDir)
- **Dépendances** :
  * Apache POI 5.2.5 (org.apache.poi:poi-ooxml) pour Excel (XSSFWorkbook, Sheet, Row, Cell)
  * iText 8.0.3 (com.itextpdf:itext7-core) pour PDF (PdfWriter, PdfDocument, Document, Paragraph, Table)