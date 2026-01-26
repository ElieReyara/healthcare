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
