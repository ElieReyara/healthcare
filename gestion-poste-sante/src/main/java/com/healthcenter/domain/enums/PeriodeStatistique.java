package com.healthcenter.domain.enums;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/**
 * Enumération des périodes statistiques disponibles.
 * Permet de définir des périodes prédéfinies avec calcul automatique des dates.
 */
public enum PeriodeStatistique {
    
    AUJOURD_HUI("Aujourd'hui"),
    SEMAINE("Cette semaine"),
    MOIS("Ce mois"),
    TRIMESTRE("Ce trimestre"),
    ANNEE("Cette année"),
    PERSONNALISE("Période personnalisée");
    
    private final String libelle;
    
    /**
     * Constructeur.
     *
     * @param libelle Le libellé de la période
     */
    PeriodeStatistique(String libelle) {
        this.libelle = libelle;
    }
    
    /**
     * Retourne le libellé de la période.
     *
     * @return Le libellé
     */
    public String getLibelle() {
        return libelle;
    }
    
    /**
     * Calcule la date de début de la période par rapport à une date de référence.
     *
     * @param reference La date de référence (généralement aujourd'hui)
     * @return La date de début de la période
     */
    public LocalDate getDateDebut(LocalDate reference) {
        if (reference == null) {
            reference = LocalDate.now();
        }
        
        return switch (this) {
            case AUJOURD_HUI -> reference;
            case SEMAINE -> reference.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            case MOIS -> reference.withDayOfMonth(1);
            case TRIMESTRE -> {
                int moisDebut = ((reference.getMonthValue() - 1) / 3) * 3 + 1;
                yield reference.withMonth(moisDebut).withDayOfMonth(1);
            }
            case ANNEE -> reference.withDayOfYear(1);
            case PERSONNALISE -> reference;
        };
    }
    
    /**
     * Calcule la date de fin de la période par rapport à une date de référence.
     *
     * @param reference La date de référence (généralement aujourd'hui)
     * @return La date de fin de la période
     */
    public LocalDate getDateFin(LocalDate reference) {
        if (reference == null) {
            reference = LocalDate.now();
        }
        
        return switch (this) {
            case AUJOURD_HUI -> reference;
            case SEMAINE -> reference.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            case MOIS -> reference.with(TemporalAdjusters.lastDayOfMonth());
            case TRIMESTRE -> {
                int moisDebut = ((reference.getMonthValue() - 1) / 3) * 3 + 1;
                int moisFin = moisDebut + 2;
                yield reference.withMonth(moisFin).with(TemporalAdjusters.lastDayOfMonth());
            }
            case ANNEE -> reference.with(TemporalAdjusters.lastDayOfYear());
            case PERSONNALISE -> reference;
        };
    }
    
    @Override
    public String toString() {
        return libelle;
    }
}
