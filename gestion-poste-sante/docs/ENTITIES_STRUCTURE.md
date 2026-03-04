# 🏗️ Structure des entités (synchronisée)

> Source de vérité : les classes du dossier `src/main/java/com/healthcenter/domain/entities`. Cette page liste les champs clés confirmés ; ouvrez la classe si un doute persiste.

## Patient
Fichier : `domain/entities/Patient.java`

Champs principaux : `id`, `nom`, `prenom`, `dateNaissance (LocalDate)`, `sexe (HOMME|FEMME)`, `telephone`, `adresse`, `numeroCarnet`. Relations : `List<Consultation> consultations`.

Notes :
- IDs générés par JPA (pas de `setId`).
- Pas de `getNomComplet()`. Concaténer `getNom()` + `getPrenom()` si besoin.

## Utilisateur
Fichier : `domain/entities/Utilisateur.java`

Champs principaux : `id`, `username`, `password` (BCrypt), `nom`, `prenom`, `email`, `role (RoleUtilisateur)`, `actif`, `dateCreation`, `derniereConnexion`, `personnel` (optionnel).

Enum rôle : `RoleUtilisateur` (ADMIN, MEDECIN, INFIRMIER, SAGE_FEMME, GESTIONNAIRE, RECEPTIONNISTE, ...).

## Autres entités
- Consultation, Medicament, MouvementStock, Vaccination, CalendrierVaccinal, Personnel, DisponibilitePersonnel, Maladie, Rapport, etc. → ouvrir chaque fichier pour la liste exacte des champs et enums associés.

## Règles JPA à respecter
- Ne jamais appeler `setId(...)` sur une entité gérée : JPA génère l’ID.
- Préférer les types modernes (`LocalDate`, `LocalDateTime`).
- Vérifier les enums utilisés (ex. Sexe = HOMME|FEMME).

**Dernière mise à jour :** 2026-03-04
