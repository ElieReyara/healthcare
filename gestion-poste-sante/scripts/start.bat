@echo off
echo ========================================
echo   Poste de Sante - Health Center
echo ========================================
echo.

REM Verifier Java installe
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERREUR : Java n'est pas installe ou non configure dans PATH
    echo Telechargez Java 17+ depuis : https://adoptium.net/
    pause
    exit /b 1
)

echo Demarrage de l'application...
echo.

REM Definir profil production
set SPRING_PROFILES_ACTIVE=prod

REM Lancer JAR avec memoire suffisante
java -Xms512m -Xmx1024m -Dspring.profiles.active=prod -jar gestion-poste-sante-1.0-SNAPSHOT.jar

pause
