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

# Lancer JAR
java -Xms512m -Xmx1024m -Dspring.profiles.active=prod -jar gestion-poste-sante-1.0-SNAPSHOT.jar

echo ""
echo "Application arretee."
