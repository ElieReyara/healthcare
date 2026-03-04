@echo off
echo ========================================
echo   Poste de Sante - Health Center
echo   MODE DEVELOPPEMENT
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

echo Demarrage de l'application en mode DEVELOPPEMENT...
echo.
echo Configuration active :
echo  - Base de donnees : H2 (./data/healthcenter-dev.mv.db)
echo  - H2 Console : ACTIVEE (http://localhost:8080/h2-console)
echo  - Logs : DETAILLES (SQL visible)
echo  - DDL : AUTO-UPDATE (cree automatiquement les tables)
echo.

REM Verifier que le JAR existe
if not exist "target\gestion-poste-sante-1.0-SNAPSHOT.jar" (
    echo ERREUR : Le fichier JAR n'existe pas
    echo Veuillez compiler le projet avec : mvn clean package
    pause
    exit /b 1
)

REM Definir profil developpement
set SPRING_PROFILES_ACTIVE=dev

REM Lancer JAR avec memoire suffisante
java -Xms512m -Xmx1024m -Dspring.profiles.active=dev -jar target\gestion-poste-sante-1.0-SNAPSHOT.jar

pause
