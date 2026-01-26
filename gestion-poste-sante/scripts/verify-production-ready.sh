#!/bin/bash
# Verification Script - Gestion Poste de Santé v1.0.0
# Ce script vérifie que l'application est prête pour la production

echo "=========================================="
echo "🔍 VERIFICATION PRODUCTION READY v1.0.0"
echo "=========================================="
echo ""

# Compteur d'erreurs
ERRORS=0

# 1. Vérifier Java
echo "1️⃣ Vérification de Java..."
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | awk -F '"' '{print $2}')
    echo "   ✅ Java trouvé : $JAVA_VERSION"
    
    # Vérifier version >= 17
    MAJOR_VERSION=$(echo $JAVA_VERSION | cut -d'.' -f1)
    if [ "$MAJOR_VERSION" -lt 17 ]; then
        echo "   ❌ Version Java insuffisante (minimum Java 17 requis)"
        ERRORS=$((ERRORS+1))
    fi
else
    echo "   ❌ Java non trouvé"
    ERRORS=$((ERRORS+1))
fi
echo ""

# 2. Vérifier Maven
echo "2️⃣ Vérification de Maven..."
if command -v mvn &> /dev/null; then
    MVN_VERSION=$(mvn -version | head -n 1 | awk '{print $3}')
    echo "   ✅ Maven trouvé : $MVN_VERSION"
else
    echo "   ⚠️ Maven non trouvé (optionnel si JAR déjà compilé)"
fi
echo ""

# 3. Vérifier la structure du projet
echo "3️⃣ Vérification de la structure du projet..."

if [ -f "pom.xml" ]; then
    echo "   ✅ pom.xml présent"
else
    echo "   ❌ pom.xml manquant"
    ERRORS=$((ERRORS+1))
fi

if [ -d "src/main/java" ]; then
    echo "   ✅ src/main/java présent"
else
    echo "   ❌ src/main/java manquant"
    ERRORS=$((ERRORS+1))
fi

if [ -d "src/main/resources" ]; then
    echo "   ✅ src/main/resources présent"
else
    echo "   ❌ src/main/resources manquant"
    ERRORS=$((ERRORS+1))
fi

if [ -d "src/test/java" ]; then
    echo "   ✅ src/test/java présent"
else
    echo "   ❌ src/test/java manquant"
    ERRORS=$((ERRORS+1))
fi
echo ""

# 4. Vérifier les fichiers de configuration
echo "4️⃣ Vérification des fichiers de configuration..."

if [ -f "src/main/resources/application.properties" ]; then
    echo "   ✅ application.properties présent"
else
    echo "   ❌ application.properties manquant"
    ERRORS=$((ERRORS+1))
fi

if [ -f "src/main/resources/application-dev.properties" ]; then
    echo "   ✅ application-dev.properties présent"
else
    echo "   ❌ application-dev.properties manquant"
    ERRORS=$((ERRORS+1))
fi

if [ -f "src/main/resources/application-prod.properties" ]; then
    echo "   ✅ application-prod.properties présent"
else
    echo "   ❌ application-prod.properties manquant"
    ERRORS=$((ERRORS+1))
fi
echo ""

# 5. Vérifier les scripts de démarrage
echo "5️⃣ Vérification des scripts de démarrage..."

if [ -f "scripts/start.bat" ]; then
    echo "   ✅ start.bat présent (Windows)"
else
    echo "   ❌ start.bat manquant"
    ERRORS=$((ERRORS+1))
fi

if [ -f "scripts/start.sh" ]; then
    echo "   ✅ start.sh présent (Linux/Mac)"
    # Vérifier les permissions
    if [ -x "scripts/start.sh" ]; then
        echo "   ✅ start.sh exécutable"
    else
        echo "   ⚠️ start.sh non exécutable (chmod +x scripts/start.sh)"
    fi
else
    echo "   ❌ start.sh manquant"
    ERRORS=$((ERRORS+1))
fi
echo ""

# 6. Vérifier la documentation
echo "6️⃣ Vérification de la documentation..."

DOCS=("README.md" "INSTALLATION.md" "DEPLOIEMENT.md" "CHANGELOG.md" "CONTEXT.md" "CREDENTIALS.md")
for doc in "${DOCS[@]}"; do
    if [ -f "$doc" ]; then
        echo "   ✅ $doc présent"
    else
        echo "   ❌ $doc manquant"
        ERRORS=$((ERRORS+1))
    fi
done
echo ""

# 7. Vérifier les entités principales
echo "7️⃣ Vérification des entités principales..."

ENTITIES=(
    "src/main/java/com/healthcenter/domain/entities/Patient.java"
    "src/main/java/com/healthcenter/domain/entities/Consultation.java"
    "src/main/java/com/healthcenter/domain/entities/Medicament.java"
    "src/main/java/com/healthcenter/domain/entities/Vaccination.java"
    "src/main/java/com/healthcenter/domain/entities/Personnel.java"
    "src/main/java/com/healthcenter/domain/entities/Utilisateur.java"
    "src/main/java/com/healthcenter/domain/entities/AuditLog.java"
)

for entity in "${ENTITIES[@]}"; do
    if [ -f "$entity" ]; then
        echo "   ✅ $(basename $entity) présent"
    else
        echo "   ❌ $(basename $entity) manquant"
        ERRORS=$((ERRORS+1))
    fi
done
echo ""

# 8. Vérifier les services principaux
echo "8️⃣ Vérification des services principaux..."

SERVICES=(
    "src/main/java/com/healthcenter/service/PatientService.java"
    "src/main/java/com/healthcenter/service/ConsultationService.java"
    "src/main/java/com/healthcenter/service/MedicamentService.java"
    "src/main/java/com/healthcenter/service/VaccinationService.java"
    "src/main/java/com/healthcenter/service/PersonnelService.java"
    "src/main/java/com/healthcenter/service/UtilisateurService.java"
    "src/main/java/com/healthcenter/service/AuditService.java"
    "src/main/java/com/healthcenter/service/BackupService.java"
)

for service in "${SERVICES[@]}"; do
    if [ -f "$service" ]; then
        echo "   ✅ $(basename $service) présent"
    else
        echo "   ❌ $(basename $service) manquant"
        ERRORS=$((ERRORS+1))
    fi
done
echo ""

# 9. Vérifier les contrôleurs principaux
echo "9️⃣ Vérification des contrôleurs principaux..."

CONTROLLERS=(
    "src/main/java/com/healthcenter/controller/PatientController.java"
    "src/main/java/com/healthcenter/controller/ConsultationController.java"
    "src/main/java/com/healthcenter/controller/MedicamentController.java"
    "src/main/java/com/healthcenter/controller/VaccinationController.java"
    "src/main/java/com/healthcenter/controller/PersonnelController.java"
    "src/main/java/com/healthcenter/controller/LoginController.java"
    "src/main/java/com/healthcenter/controller/BackupController.java"
)

for controller in "${CONTROLLERS[@]}"; do
    if [ -f "$controller" ]; then
        echo "   ✅ $(basename $controller) présent"
    else
        echo "   ❌ $(basename $controller) manquant"
        ERRORS=$((ERRORS+1))
    fi
done
echo ""

# 10. Compiler le projet (si Maven disponible)
if command -v mvn &> /dev/null; then
    echo "🔟 Compilation du projet..."
    mvn clean compile -q
    if [ $? -eq 0 ]; then
        echo "   ✅ Compilation réussie"
    else
        echo "   ❌ Échec de la compilation"
        ERRORS=$((ERRORS+1))
    fi
    echo ""
    
    # 11. Exécuter les tests
    echo "1️⃣1️⃣ Exécution des tests..."
    mvn test -q
    if [ $? -eq 0 ]; then
        echo "   ✅ Tous les tests passent"
    else
        echo "   ❌ Certains tests échouent"
        ERRORS=$((ERRORS+1))
    fi
    echo ""
else
    echo "🔟 Compilation ignorée (Maven non disponible)"
    echo ""
fi

# 12. Vérifier Git
echo "1️⃣2️⃣ Vérification de Git..."

if command -v git &> /dev/null; then
    if [ -d ".git" ]; then
        echo "   ✅ Dépôt Git initialisé"
        
        # Vérifier le tag v1.0.0
        if git tag | grep -q "v1.0.0"; then
            echo "   ✅ Tag v1.0.0 présent"
        else
            echo "   ⚠️ Tag v1.0.0 manquant"
        fi
        
        # Vérifier les changements non commités
        if git status --porcelain | grep -q .; then
            echo "   ⚠️ Changements non commités présents"
        else
            echo "   ✅ Tous les changements sont commités"
        fi
    else
        echo "   ⚠️ Pas de dépôt Git"
    fi
else
    echo "   ⚠️ Git non trouvé (optionnel)"
fi
echo ""

# Résultat final
echo "=========================================="
if [ $ERRORS -eq 0 ]; then
    echo "✅ VERIFICATION REUSSIE - PRODUCTION READY !"
    echo "=========================================="
    echo ""
    echo "🚀 L'application est prête pour la production !"
    echo ""
    echo "Prochaines étapes :"
    echo "  1. Compiler : mvn clean package"
    echo "  2. Tester : mvn test"
    echo "  3. Démarrer (dev) : SPRING_PROFILES_ACTIVE=dev ./scripts/start.sh"
    echo "  4. Démarrer (prod) : ./scripts/start.sh"
    echo "  5. Accéder : http://localhost:8080"
    echo "  6. Connexion : admin / admin123"
    echo ""
    exit 0
else
    echo "❌ VERIFICATION ECHOUEE - $ERRORS ERREUR(S) DETECTEE(S)"
    echo "=========================================="
    echo ""
    echo "⚠️ Corriger les erreurs ci-dessus avant le déploiement."
    echo ""
    exit 1
fi
