# 📦 Guide d'Installation - Health Center

## Pré-requis

- **Java 17 ou supérieur** ([Télécharger Adoptium](https://adoptium.net/))
- **Windows 10+** ou **Linux (Ubuntu 20.04+)**
- **2 GB RAM minimum**
- **500 MB espace disque**

---

## 🪟 Installation Windows

### 1. Vérifier Java

Ouvrir **PowerShell** ou **Command Prompt** :

```cmd
java -version
```

✅ Doit afficher : `openjdk version "17.x.x"` ou supérieur

❌ Si erreur "java n'est pas reconnu" :
1. Télécharger Java 17+ depuis [Adoptium](https://adoptium.net/)
2. Installer avec option "Add to PATH"
3. Redémarrer terminal

### 2. Extraire l'archive

- Décompresser `HealthCenter-1.0.0-distribution.zip`
- Placer dans `C:\HealthCenter\` (ou autre dossier)

### 3. Premier démarrage

- Double-clic sur `start.bat`
- Application démarre (peut prendre 30 secondes au premier lancement)

### 4. Connexion

- **Username :** `admin`
- **Password :** `admin123`

⚠️ **IMPORTANT:** Changez le mot de passe admin après première connexion !

---

## 🐧 Installation Linux/Mac

### 1. Installer Java

**Ubuntu/Debian :**
```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

**Fedora/RHEL :**
```bash
sudo dnf install java-17-openjdk
```

**Mac (Homebrew) :**
```bash
brew install openjdk@17
```

Vérifier :
```bash
java -version
```

### 2. Extraire l'archive

```bash
unzip HealthCenter-1.0.0-distribution.zip -d /opt/
cd /opt/HealthCenter
```

### 3. Rendre script exécutable

```bash
chmod +x start.sh
```

### 4. Démarrer

```bash
./start.sh
```

---

## 🔐 Première Connexion

### Identifiants par défaut

- **Username :** `admin`
- **Password :** `admin123`

### Changer le mot de passe admin

1. Connexion avec `admin/admin123`
2. Menu **"Administration"** → **"Utilisateurs"**
3. Sélectionner utilisateur `admin`
4. Clic **"Changer mot de passe"**
5. Entrer nouveau mot de passe (min 8 caractères)

---

## ⚙️ Configuration

### Changer le port (si 8080 occupé)

Éditer `config/application-prod.properties` :

```properties
server.port=9090
```

### Changer chemin base de données

Par défaut : `./data/healthcenter.mv.db`

Modifier dans `config/application-prod.properties` :

```properties
spring.datasource.url=jdbc:h2:file:C:/MesBackups/healthcenter
```

### Configuration backup automatique

Par défaut : **Tous les jours à 2h du matin**

Modifier cron dans `config/application-prod.properties` :

```properties
backup.cron=0 0 2 * * ?

# Exemples :
# Tous les jours à 3h : 0 0 3 * * ?
# Toutes les 6h : 0 0 */6 * * ?
# Tous les dimanches à minuit : 0 0 0 * * SUN
```

---

## 💾 Gestion des Backups

### Backup manuel

1. Menu **"Administration"** → **"Backup & Restore"**
2. Clic **"Créer Backup"**
3. Choisir dossier de destination
4. Fichier `.sql` créé

### Restaurer backup

1. Menu **"Backup & Restore"**
2. Sélectionner fichier backup dans la liste
3. Clic **"Restaurer"**
4. Confirmation (⚠️ écrase données actuelles)
5. **Redémarrer application** pour appliquer

### Localisation backups automatiques

```
Windows : C:\HealthCenter\backups\
Linux   : /opt/HealthCenter/backups/
```

---

## 👥 Gestion Utilisateurs

### Créer nouvel utilisateur

1. Connexion avec compte **ADMIN**
2. Menu **"Administration"** → **"Utilisateurs"**
3. Clic **"Ajouter utilisateur"**
4. Remplir formulaire :
   - Username (unique)
   - Mot de passe (min 8 caractères)
   - Nom / Prénom
   - **Rôle** (voir ci-dessous)
5. Clic **"Enregistrer"**

### Rôles disponibles

| Rôle | Accès Modules |
|------|---------------|
| **ADMIN** | Tous modules + Administration utilisateurs + Backups |
| **MEDECIN** | Patients, Consultations, Vaccinations, Statistiques |
| **INFIRMIER** | Patients, Vaccinations, Médicaments (stock) |
| **SAGE_FEMME** | Patients, Consultations (limitées) |
| **GESTIONNAIRE** | Statistiques, Rapports, Personnel, Médicaments |
| **RECEPTIONNISTE** | Patients (lecture/création uniquement) |

### Désactiver utilisateur

1. Sélectionner utilisateur
2. Clic **"Désactiver"**
3. Utilisateur ne peut plus se connecter (données conservées)

---

## 🔍 Dépannage

### ❌ "Java n'est pas reconnu"

**Solution :**
1. Vérifier installation Java
2. Ajouter Java au PATH système
3. Redémarrer terminal

### ❌ Application ne démarre pas

**Vérifications :**

1. **Port 8080 libre ?**
   ```bash
   # Windows
   netstat -ano | findstr :8080
   
   # Linux
   lsof -i :8080
   ```
   Si occupé → changer port (voir Configuration)

2. **Logs d'erreur ?**
   Consulter : `logs/healthcenter.log`

3. **Base de données corrompue ?**
   Supprimer `data/healthcenter.mv.db` (⚠️ perte données)

### ❌ Mot de passe admin oublié

**Solution :**

1. Arrêter application
2. Supprimer fichier base de données :
   ```bash
   # Windows
   del data\healthcenter.mv.db
   
   # Linux
   rm data/healthcenter.mv.db
   ```
3. Redémarrer → `admin/admin123` recréé

⚠️ **ATTENTION:** Toutes les données seront perdues. Restaurer backup si disponible.

### ❌ "Error creating bean 'utilisateurService'"

**Cause :** Dépendances Spring Security manquantes

**Solution :**
```bash
# Recompiler
mvn clean install
```

---

## 🗑️ Désinstallation

1. Arrêter application
2. Supprimer dossier `HealthCenter`
3. *Optionnel* : Sauvegarder `backups/` avant suppression

---

## 📞 Support

### Logs

Consulter : `logs/healthcenter.log`

### Documentation

- **Manuel utilisateur :** `README.md`
- **Documentation technique :** `CONTEXT.md`

### Problème non résolu ?

1. Vérifier `logs/healthcenter.log`
2. Vérifier version Java : `java -version`
3. Réinstaller application

---

## 🚀 Mise à jour

### Sauvegarder données

1. Créer backup manuel
2. Copier dossier `data/` et `backups/`

### Installer nouvelle version

1. Arrêter ancienne version
2. Extraire nouvelle archive
3. Copier anciens dossiers `data/` et `backups/` dans nouvelle installation
4. Démarrer nouvelle version

---

**Version du guide :** 1.0.0  
**Dernière mise à jour :** 26 janvier 2026
