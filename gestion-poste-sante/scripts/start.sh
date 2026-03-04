#!/bin/bash

echo "========================================"
echo "   Poste de Sante - Health Center"
echo "========================================"
echo ""

# Verifier Java installe
if ! command -v java &> /dev/null; then
    echo "ERREUR : Java n'est pas installe"
    echo "Installez Java 17+ : sudo apt install openjdk-17-jdk"
    exit 1
fi

echo "Demarrage de l'application..."
echo ""

# Definir profil production
export SPRING_PROFILES_ACTIVE=prod

# Verifier que le JAR existe
if [ ! -f "target/gestion-poste-sante-1.0-SNAPSHOT.jar" ]; then
    echo "ERREUR : Le fichier JAR n'existe pas"
    echo "Veuillez compiler le projet avec : mvn clean package"
    exit 1
fi

# Lancer JAR
java -Xms512m -Xmx1024m -Dspring.profiles.active=prod -jar target/gestion-poste-sante-1.0-SNAPSHOT.jar

echo ""
echo "Application arretee."
