# 🔐 Identifiants par Défaut - HealthCare

## ⚠️ IMPORTANT - Sécurité

**Ce fichier contient les identifiants par défaut de l'application (desktop JavaFX + Spring Boot).**

- **NE PAS PARTAGER** ce fichier publiquement
- **CHANGER** tous les mots de passe après la première connexion
- **SUPPRIMER** ce fichier après configuration initiale
- **NE PAS COMMITER** dans un dépôt public

---

## 👨‍💼 Compte Administrateur

### Informations de Connexion

**Username:** `admin`  
**Password:** `admin123`  
**Rôle:** `ADMIN`

### Permissions

✅ **Accès complet aux modules disponibles dans l'application actuelle :**
- Patients
- Consultations
- Médicaments & Stock
- Vaccinations & Calendrier
- Personnel
- Statistiques & Rapports
- Gestion des utilisateurs

💡 Certaines fonctionnalités documentées (ex. backup/restauration, audit étendu) peuvent ne pas être implémentées dans cette version desktop. Vérifiez le code avant communication externe.

### Actions Requises

1. **Première connexion**
   - Se connecter avec `admin / admin123`
   - L'application affiche un avertissement de sécurité

2. **Changer le mot de passe**
   - Menu **Personnel** → **Gestion Utilisateurs**
   - Sélectionner `admin`
   - **Changer mot de passe**
   - Choisir un mot de passe fort:
     - Minimum 8 caractères
     - Majuscules + minuscules
     - Chiffres
     - Caractères spéciaux recommandés

3. **Créer des utilisateurs supplémentaires**
   - Menu **Personnel** → **Gestion Utilisateurs**
   - **Nouveau Utilisateur**
   - Attribuer le rôle approprié

---

## 👥 Rôles Disponibles

### 1. ADMIN (Administrateur)
**Permissions:** Accès complet
```
Modules: *, USERS, BACKUP, CONFIG
Actions: CREATE, READ, UPDATE, DELETE, EXPORT, GENERATE_REPORT, BACKUP, RESTORE
```

**Cas d'usage:** Directeur du poste de santé, IT Admin

---

### 2. MEDECIN (Médecin)
**Permissions:** Patients, Consultations, Vaccinations, Statistiques
```
Modules: PATIENTS, CONSULTATIONS, VACCINATIONS, STATISTIQUES
Actions: CREATE, READ, UPDATE, DELETE, EXPORT, GENERATE_REPORT
```

**Restrictions:**
- ❌ Gestion utilisateurs
- ❌ Backup/restauration
- ❌ Modification du stock (lecture seule)
- ❌ Gestion du personnel

**Cas d'usage:** Médecin généraliste, pédiatre

---

### 3. INFIRMIER (Infirmier/Infirmière)
**Permissions:** Patients, Vaccinations, Stock Médicaments
```
Modules: PATIENTS, VACCINATIONS, STOCK
Actions: CREATE, READ, UPDATE (partiel)
```

**Restrictions:**
- ❌ Consultations médicales complètes
- ✅ Prise des constantes uniquement
- ❌ Rapports et statistiques
- ❌ Gestion du personnel

**Cas d'usage:** Infirmier de salle, vaccinateur

---

### 4. SAGE_FEMME (Sage-Femme)
**Permissions:** Patients, Consultations prénatales
```
Modules: PATIENTS, CONSULTATIONS (type=PRENATALE uniquement)
Actions: CREATE, READ, UPDATE (consultations prénatales)
```

**Restrictions:**
- ✅ Consultations prénatales uniquement
- ❌ Autres types de consultations
- ❌ Vaccinations
- ❌ Stock médicaments
- ❌ Statistiques complètes

**Cas d'usage:** Sage-femme, consultations grossesse

---

### 5. GESTIONNAIRE (Gestionnaire)
**Permissions:** Statistiques, Rapports, Personnel, Stock
```
Modules: STATISTIQUES, RAPPORTS, PERSONNEL, STOCK
Actions: READ, EXPORT, GENERATE_REPORT, UPDATE (stock)
```

**Restrictions:**
- ❌ Patients directs
- ❌ Consultations médicales
- ❌ Vaccinations
- ✅ Statistiques complètes
- ✅ Gestion personnel administratif

**Cas d'usage:** Gestionnaire administratif, responsable logistique

---

### 6. RECEPTIONNISTE (Réceptionniste)
**Permissions:** Patients (création, consultation)
```
Modules: PATIENTS
Actions: CREATE, READ (limité)
```

**Restrictions:**
- ✅ Enregistrement nouveaux patients
- ✅ Consultation des dossiers patients (lecture seule)
- ❌ Modification des dossiers médicaux
- ❌ Consultations
- ❌ Vaccinations
- ❌ Stock
- ❌ Statistiques

**Cas d'usage:** Agent d'accueil, secrétariat

---

## 🔑 Création d'Utilisateurs Exemples

### Exemple 1: Créer un compte Médecin

```
Username: dr.dupont
Email: dr.dupont@posante.local
Password: Medecin2026!
Rôle: MEDECIN
Personnel lié: Dr. Jean Dupont (optionnel)
Actif: ✅ Oui
```

### Exemple 2: Créer un compte Infirmier

```
Username: inf.martin
Email: martin.inf@posante.local
Password: Infirmier2026!
Rôle: INFIRMIER
Personnel lié: Marie Martin (optionnel)
Actif: ✅ Oui
```

### Exemple 3: Créer un compte Réceptionniste

```
Username: accueil
Email: accueil@posante.local
Password: Accueil2026!
Rôle: RECEPTIONNISTE
Personnel lié: (aucun)
Actif: ✅ Oui
```

---

## 🔒 Politique de Mots de Passe

### Recommandations

**Longueur:** Minimum 8 caractères (12+ recommandé)

**Complexité:**
- ✅ Majuscules (A-Z)
- ✅ Minuscules (a-z)
- ✅ Chiffres (0-9)
- ✅ Caractères spéciaux (!@#$%^&*)

**À éviter:**
- ❌ Mots du dictionnaire
- ❌ Dates de naissance
- ❌ Prénoms ou noms
- ❌ Séquences (123456, azerty)
- ❌ Mots de passe réutilisés

### Exemples de Mots de Passe Forts

```
✅ P@sT3_S@nT3_2026!
✅ M3d€c1n#S3cur3
✅ V@cc1n@t10n!2026
✅ Gest10n$P0st€2026
```

---

## 🛡️ Sécurité Implémentée

### Hachage des Mots de Passe

**Algorithme:** BCrypt (Spring Security Crypto)
- Coût: 10 rounds
- Salt automatique unique par utilisateur
- Résistant aux attaques rainbow tables

**Exemple de hash stocké en DB:**
```
$2a$10$8K1p/a0jZ.EH6qNi0s8nYe8mMx1c5YO0nZrK7NX3R/8n5LnO6oXJC
```

### Audit Trail

Toutes les actions sont enregistrées:
- **LOGIN:** Connexion réussie (utilisateur, date, IP)
- **LOGOUT:** Déconnexion (utilisateur, date)
- **CREATE:** Création d'entité (type, ID, utilisateur)
- **UPDATE:** Modification (type, ID, utilisateur)
- **DELETE:** Suppression (type, ID, utilisateur)
- **EXPORT:** Export de données
- **GENERATE_REPORT:** Génération de rapport
- **BACKUP:** Sauvegarde DB
- **RESTORE:** Restauration DB

### Session Management

**Singleton SessionManager:**
- Un utilisateur connecté par instance d'application
- Session stockée en mémoire (non persistante)
- Déconnexion automatique à la fermeture de l'application

**Limitations:**
- ⚠️ Pas de timeout de session (implémentation future)
- ⚠️ Pas de multi-sessions concurrentes
- ⚠️ Pas de "Remember Me"

---

## 🔐 Checklist de Sécurité Post-Déploiement

### Avant la Production

- [ ] Changer le mot de passe admin par défaut
- [ ] Créer des comptes utilisateurs individuels (ne pas partager admin)
- [ ] Attribuer les rôles appropriés (principe du moindre privilège)
- [ ] Désactiver les comptes non utilisés
- [ ] Vérifier que H2 Console est désactivée en production
- [ ] Configurer les backups automatiques
- [ ] Tester la restauration d'un backup
- [ ] Documenter les procédures de récupération
- [ ] Informer les utilisateurs des bonnes pratiques
- [ ] Planifier une revue des logs d'audit régulière

### Pendant l'Utilisation

- [ ] Changer les mots de passe tous les 90 jours
- [ ] Désactiver immédiatement les comptes des employés quittant le poste
- [ ] Vérifier les logs d'audit mensuellement
- [ ] Surveiller les tentatives de connexion échouées
- [ ] Maintenir les backups à jour (vérifier les sauvegardes auto)
- [ ] Tester la restauration trimestriellement
- [ ] Former les nouveaux utilisateurs aux procédures de sécurité

---

## 📋 Logs d'Audit - Exemples de Requêtes

### Voir les dernières connexions

```sql
SELECT u.username, a.date_action, a.adresse_ip
FROM audit_log a
JOIN utilisateur u ON a.utilisateur_id = u.id
WHERE a.action = 'LOGIN'
ORDER BY a.date_action DESC
LIMIT 20;
```

### Voir les actions d'un utilisateur

```sql
SELECT a.action, a.module, a.description, a.date_action
FROM audit_log a
JOIN utilisateur u ON a.utilisateur_id = u.id
WHERE u.username = 'admin'
ORDER BY a.date_action DESC;
```

### Voir les modifications sur les patients

```sql
SELECT u.username, a.action, a.entity_id, a.description, a.date_action
FROM audit_log a
JOIN utilisateur u ON a.utilisateur_id = u.id
WHERE a.module = 'PATIENTS' AND a.action IN ('CREATE', 'UPDATE', 'DELETE')
ORDER BY a.date_action DESC;
```

### Voir les backups effectués

```sql
SELECT u.username, a.description, a.date_action
FROM audit_log a
JOIN utilisateur u ON a.utilisateur_id = u.id
WHERE a.action = 'BACKUP'
ORDER BY a.date_action DESC;
```

---

## 🚨 Procédure en Cas de Compromission

### Si un compte est compromis:

1. **Désactivation immédiate**
   - Se connecter avec un compte ADMIN non compromis
   - Menu **Personnel** → **Gestion Utilisateurs**
   - Sélectionner le compte compromis
   - Cliquer sur **Désactiver**

2. **Analyse des logs**
   - Vérifier les actions récentes du compte compromis
   - Identifier les données accédées/modifiées

3. **Notification**
   - Informer l'utilisateur propriétaire du compte
   - Documenter l'incident

4. **Réactivation**
   - Réinitialiser le mot de passe
   - Réactiver le compte
   - Former l'utilisateur aux bonnes pratiques

### Si l'admin est compromis:

**⚠️ URGENCE - Base de données compromise**

1. **Arrêter l'application immédiatement**
   ```bash
   # Windows: Fermer la fenêtre ou Ctrl+C
   # Linux: kill <PID>
   ```

2. **Restaurer un backup propre**
   - Identifier le dernier backup avant compromission
   - Utiliser la procédure de restauration

3. **Recréer l'admin**
   - Supprimer `data/healthcenter.mv.db`
   - Redémarrer → admin/admin123 recréé
   - Changer immédiatement le mot de passe

4. **Recréer les comptes utilisateurs**
   - Recréer manuellement tous les comptes
   - Utiliser de nouveaux mots de passe

5. **Investigation**
   - Analyser les logs du serveur
   - Identifier la source de la compromission
   - Corriger la vulnérabilité

---

**Document créé le:** 26 janvier 2026  
**Version:** 1.0.0  
**Statut:** 🔴 CONFIDENTIEL - NE PAS PARTAGER
