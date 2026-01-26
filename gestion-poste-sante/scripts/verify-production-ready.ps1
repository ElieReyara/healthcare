# Verification Script - Gestion Poste de Santé v1.0.0
# Ce script vérifie que l'application est prête pour la production

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "🔍 VERIFICATION PRODUCTION READY v1.0.0" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

# Compteur d'erreurs
$ErrorCount = 0

# 1. Vérifier Java
Write-Host "1️⃣ Vérification de Java..." -ForegroundColor Yellow
try {
    $javaVersion = (java -version 2>&1)[0]
    Write-Host "   ✅ Java trouvé : $javaVersion" -ForegroundColor Green
    
    # Extraire le numéro de version
    if ($javaVersion -match 'version "(\d+)') {
        $majorVersion = [int]$matches[1]
        if ($majorVersion -lt 17) {
            Write-Host "   ❌ Version Java insuffisante (minimum Java 17 requis)" -ForegroundColor Red
            $ErrorCount++
        }
    }
} catch {
    Write-Host "   ❌ Java non trouvé" -ForegroundColor Red
    $ErrorCount++
}
Write-Host ""

# 2. Vérifier Maven
Write-Host "2️⃣ Vérification de Maven..." -ForegroundColor Yellow
try {
    $mvnVersion = (mvn -version)[0]
    Write-Host "   ✅ Maven trouvé : $mvnVersion" -ForegroundColor Green
} catch {
    Write-Host "   ⚠️ Maven non trouvé (optionnel si JAR déjà compilé)" -ForegroundColor DarkYellow
}
Write-Host ""

# 3. Vérifier la structure du projet
Write-Host "3️⃣ Vérification de la structure du projet..." -ForegroundColor Yellow

if (Test-Path "pom.xml") {
    Write-Host "   ✅ pom.xml présent" -ForegroundColor Green
} else {
    Write-Host "   ❌ pom.xml manquant" -ForegroundColor Red
    $ErrorCount++
}

if (Test-Path "src\main\java") {
    Write-Host "   ✅ src\main\java présent" -ForegroundColor Green
} else {
    Write-Host "   ❌ src\main\java manquant" -ForegroundColor Red
    $ErrorCount++
}

if (Test-Path "src\main\resources") {
    Write-Host "   ✅ src\main\resources présent" -ForegroundColor Green
} else {
    Write-Host "   ❌ src\main\resources manquant" -ForegroundColor Red
    $ErrorCount++
}

if (Test-Path "src\test\java") {
    Write-Host "   ✅ src\test\java présent" -ForegroundColor Green
} else {
    Write-Host "   ❌ src\test\java manquant" -ForegroundColor Red
    $ErrorCount++
}
Write-Host ""

# 4. Vérifier les fichiers de configuration
Write-Host "4️⃣ Vérification des fichiers de configuration..." -ForegroundColor Yellow

if (Test-Path "src\main\resources\application.properties") {
    Write-Host "   ✅ application.properties présent" -ForegroundColor Green
} else {
    Write-Host "   ❌ application.properties manquant" -ForegroundColor Red
    $ErrorCount++
}

if (Test-Path "src\main\resources\application-dev.properties") {
    Write-Host "   ✅ application-dev.properties présent" -ForegroundColor Green
} else {
    Write-Host "   ❌ application-dev.properties manquant" -ForegroundColor Red
    $ErrorCount++
}

if (Test-Path "src\main\resources\application-prod.properties") {
    Write-Host "   ✅ application-prod.properties présent" -ForegroundColor Green
} else {
    Write-Host "   ❌ application-prod.properties manquant" -ForegroundColor Red
    $ErrorCount++
}
Write-Host ""

# 5. Vérifier les scripts de démarrage
Write-Host "5️⃣ Vérification des scripts de démarrage..." -ForegroundColor Yellow

if (Test-Path "scripts\start.bat") {
    Write-Host "   ✅ start.bat présent (Windows)" -ForegroundColor Green
} else {
    Write-Host "   ❌ start.bat manquant" -ForegroundColor Red
    $ErrorCount++
}

if (Test-Path "scripts\start.sh") {
    Write-Host "   ✅ start.sh présent (Linux/Mac)" -ForegroundColor Green
} else {
    Write-Host "   ❌ start.sh manquant" -ForegroundColor Red
    $ErrorCount++
}
Write-Host ""

# 6. Vérifier la documentation
Write-Host "6️⃣ Vérification de la documentation..." -ForegroundColor Yellow

$docs = @("README.md", "INSTALLATION.md", "DEPLOIEMENT.md", "CHANGELOG.md", "CONTEXT.md", "CREDENTIALS.md")
foreach ($doc in $docs) {
    if (Test-Path $doc) {
        Write-Host "   ✅ $doc présent" -ForegroundColor Green
    } else {
        Write-Host "   ❌ $doc manquant" -ForegroundColor Red
        $ErrorCount++
    }
}
Write-Host ""

# 7. Vérifier les entités principales
Write-Host "7️⃣ Vérification des entités principales..." -ForegroundColor Yellow

$entities = @(
    "src\main\java\com\healthcenter\domain\entities\Patient.java",
    "src\main\java\com\healthcenter\domain\entities\Consultation.java",
    "src\main\java\com\healthcenter\domain\entities\Medicament.java",
    "src\main\java\com\healthcenter\domain\entities\Vaccination.java",
    "src\main\java\com\healthcenter\domain\entities\Personnel.java",
    "src\main\java\com\healthcenter\domain\entities\Utilisateur.java",
    "src\main\java\com\healthcenter\domain\entities\AuditLog.java"
)

foreach ($entity in $entities) {
    if (Test-Path $entity) {
        $name = Split-Path $entity -Leaf
        Write-Host "   ✅ $name présent" -ForegroundColor Green
    } else {
        $name = Split-Path $entity -Leaf
        Write-Host "   ❌ $name manquant" -ForegroundColor Red
        $ErrorCount++
    }
}
Write-Host ""

# 8. Vérifier les services principaux
Write-Host "8️⃣ Vérification des services principaux..." -ForegroundColor Yellow

$services = @(
    "src\main\java\com\healthcenter\service\PatientService.java",
    "src\main\java\com\healthcenter\service\ConsultationService.java",
    "src\main\java\com\healthcenter\service\MedicamentService.java",
    "src\main\java\com\healthcenter\service\VaccinationService.java",
    "src\main\java\com\healthcenter\service\PersonnelService.java",
    "src\main\java\com\healthcenter\service\UtilisateurService.java",
    "src\main\java\com\healthcenter\service\AuditService.java",
    "src\main\java\com\healthcenter\service\BackupService.java"
)

foreach ($service in $services) {
    if (Test-Path $service) {
        $name = Split-Path $service -Leaf
        Write-Host "   ✅ $name présent" -ForegroundColor Green
    } else {
        $name = Split-Path $service -Leaf
        Write-Host "   ❌ $name manquant" -ForegroundColor Red
        $ErrorCount++
    }
}
Write-Host ""

# 9. Vérifier les contrôleurs principaux
Write-Host "9️⃣ Vérification des contrôleurs principaux..." -ForegroundColor Yellow

$controllers = @(
    "src\main\java\com\healthcenter\controller\PatientController.java",
    "src\main\java\com\healthcenter\controller\ConsultationController.java",
    "src\main\java\com\healthcenter\controller\MedicamentController.java",
    "src\main\java\com\healthcenter\controller\VaccinationController.java",
    "src\main\java\com\healthcenter\controller\PersonnelController.java",
    "src\main\java\com\healthcenter\controller\LoginController.java",
    "src\main\java\com\healthcenter\controller\BackupController.java"
)

foreach ($controller in $controllers) {
    if (Test-Path $controller) {
        $name = Split-Path $controller -Leaf
        Write-Host "   ✅ $name présent" -ForegroundColor Green
    } else {
        $name = Split-Path $controller -Leaf
        Write-Host "   ❌ $name manquant" -ForegroundColor Red
        $ErrorCount++
    }
}
Write-Host ""

# 10. Compiler le projet (si Maven disponible)
try {
    Write-Host "🔟 Compilation du projet..." -ForegroundColor Yellow
    $compileOutput = mvn clean compile -q 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "   ✅ Compilation réussie" -ForegroundColor Green
    } else {
        Write-Host "   ❌ Échec de la compilation" -ForegroundColor Red
        $ErrorCount++
    }
    Write-Host ""
    
    # 11. Exécuter les tests
    Write-Host "1️⃣1️⃣ Exécution des tests..." -ForegroundColor Yellow
    $testOutput = mvn test -q 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "   ✅ Tous les tests passent" -ForegroundColor Green
    } else {
        Write-Host "   ❌ Certains tests échouent" -ForegroundColor Red
        $ErrorCount++
    }
    Write-Host ""
} catch {
    Write-Host "🔟 Compilation ignorée (Maven non disponible)" -ForegroundColor DarkYellow
    Write-Host ""
}

# 12. Vérifier Git
Write-Host "1️⃣2️⃣ Vérification de Git..." -ForegroundColor Yellow

try {
    if (Test-Path ".git") {
        Write-Host "   ✅ Dépôt Git initialisé" -ForegroundColor Green
        
        # Vérifier le tag v1.0.0
        $tags = git tag
        if ($tags -contains "v1.0.0") {
            Write-Host "   ✅ Tag v1.0.0 présent" -ForegroundColor Green
        } else {
            Write-Host "   ⚠️ Tag v1.0.0 manquant" -ForegroundColor DarkYellow
        }
        
        # Vérifier les changements non commités
        $status = git status --porcelain
        if ($status) {
            Write-Host "   ⚠️ Changements non commités présents" -ForegroundColor DarkYellow
        } else {
            Write-Host "   ✅ Tous les changements sont commités" -ForegroundColor Green
        }
    } else {
        Write-Host "   ⚠️ Pas de dépôt Git" -ForegroundColor DarkYellow
    }
} catch {
    Write-Host "   ⚠️ Git non trouvé (optionnel)" -ForegroundColor DarkYellow
}
Write-Host ""

# Résultat final
Write-Host "==========================================" -ForegroundColor Cyan
if ($ErrorCount -eq 0) {
    Write-Host "✅ VERIFICATION REUSSIE - PRODUCTION READY !" -ForegroundColor Green
    Write-Host "==========================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "🚀 L'application est prête pour la production !" -ForegroundColor Green
    Write-Host ""
    Write-Host "Prochaines étapes :" -ForegroundColor Yellow
    Write-Host "  1. Compiler : mvn clean package"
    Write-Host "  2. Tester : mvn test"
    Write-Host "  3. Demarrer (dev) : set SPRING_PROFILES_ACTIVE=dev ; scripts\start.bat"
    Write-Host "  4. Démarrer (prod) : scripts\start.bat"
    Write-Host "  5. Accéder : http://localhost:8080"
    Write-Host "  6. Connexion : admin / admin123"
    Write-Host ""
    exit 0
} else {
    Write-Host "❌ VERIFICATION ECHOUEE - $ErrorCount ERREUR(S) DETECTEE(S)" -ForegroundColor Red
    Write-Host "==========================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Corriger les erreurs ci-dessus avant le deploiement." -ForegroundColor Yellow
    Write-Host ""
    exit 1
}
