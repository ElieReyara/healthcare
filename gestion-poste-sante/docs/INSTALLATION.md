# 📦 Guide d'installation (projet Maven JavaFX/Spring Boot)

> Ce projet est lancé depuis les sources avec Maven (pas de `start.bat` ou archive prête à l'emploi).

## Pré-requis

- Java 17 (ou 21) installé et dans le PATH
- Maven 3.9+
- PostgreSQL en local (optionnel si vous utilisez H2, mais le projet cible Postgres)

Vérifier Java :

```bash
java -version
```

Vérifier Maven :

```bash
mvn -v
```

## Configuration base de données

Fichier : `src/main/resources/application.properties`

```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/healthcenter_db
spring.datasource.username=postgres
spring.datasource.password=1234
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

Adaptez l'URL, le user et le mot de passe selon votre instance Postgres.

## Lancer l'application (mode JavaFX)

```bash
mvn clean javafx:run
```

## Lancer l'application (mode Spring Boot pur, sans JavaFX)

```bash
mvn clean spring-boot:run
```

## Identifiants par défaut

- Username : `admin`
- Password : `admin123`

Ces identifiants sont créés au démarrage via `InitialUserConfig`. Changez le mot de passe après la première connexion.

## Dépannage rapide

- Erreur JavaFX : assurez-vous d'utiliser Java 17+ et de lancer avec `javafx:run`.
- Erreur Postgres (connexion refusée) : vérifiez l'URL/port/user/password dans `application.properties` et que Postgres est démarré.
- Erreur de compilation sur les dépendances : exécutez `mvn clean install` pour retélécharger les artefacts.

## Notes NetBeans

- Configurez l'action **Run Project** sur `clean javafx:run` (ou `spring-boot:run` si vous ne lancez pas l'UI JavaFX).
- JDK utilisé par NetBeans : pointez vers Java 17 ou 21.

**Dernière mise à jour :** 2026-03-04
