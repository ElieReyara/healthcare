package com.healthcenter.dto;

import java.time.LocalDate;

/**
 * DTO pour les données d'évolution temporelle (graphiques LineChart).
 * Représente une valeur à une période donnée.
 */
public class EvolutionData {
    
    private String periode;
    private Number valeur;
    private LocalDate date;
    
    /**
     * Constructeur vide.
     */
    public EvolutionData() {
    }
    
    /**
     * Constructeur avec tous les paramètres.
     *
     * @param periode Le libellé de la période (ex: "2024-01", "Semaine 5")
     * @param valeur La valeur numérique
     * @param date La date de référence de la période
     */
    public EvolutionData(String periode, Number valeur, LocalDate date) {
        this.periode = periode;
        this.valeur = valeur;
        this.date = date;
    }
    
    // Getters et Setters
    
    public String getPeriode() {
        return periode;
    }
    
    public void setPeriode(String periode) {
        this.periode = periode;
    }
    
    public Number getValeur() {
        return valeur;
    }
    
    public void setValeur(Number valeur) {
        this.valeur = valeur;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    @Override
    public String toString() {
        return periode + ": " + valeur;
    }
}
