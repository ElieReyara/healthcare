package com.healthcenter.dto;

/**
 * DTO pour les données de répartition (graphiques PieChart, BarChart).
 * Utilisé pour représenter une donnée avec son label et sa valeur.
 */
public class RepartitionData {
    
    private String label;
    private Number valeur;
    private Double pourcentage;
    
    /**
     * Constructeur vide.
     */
    public RepartitionData() {
    }
    
    /**
     * Constructeur avec label et valeur.
     *
     * @param label Le label de la donnée
     * @param valeur La valeur numérique
     */
    public RepartitionData(String label, Number valeur) {
        this.label = label;
        this.valeur = valeur;
    }
    
    /**
     * Calcule le pourcentage par rapport à un total.
     *
     * @param total Le total pour le calcul du pourcentage
     */
    public void calculerPourcentage(Number total) {
        if (total != null && total.doubleValue() > 0 && valeur != null) {
            this.pourcentage = (valeur.doubleValue() / total.doubleValue()) * 100.0;
        } else {
            this.pourcentage = 0.0;
        }
    }
    
    // Getters et Setters
    
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
    public Number getValeur() {
        return valeur;
    }
    
    public void setValeur(Number valeur) {
        this.valeur = valeur;
    }
    
    public Double getPourcentage() {
        return pourcentage;
    }
    
    public void setPourcentage(Double pourcentage) {
        this.pourcentage = pourcentage;
    }
    
    /**
     * Retourne le pourcentage formaté.
     *
     * @return Le pourcentage avec 2 décimales et le symbole %
     */
    public String getPourcentageFormate() {
        return pourcentage != null ? String.format("%.2f%%", pourcentage) : "0%";
    }
    
    @Override
    public String toString() {
        return label + ": " + valeur + (pourcentage != null ? " (" + getPourcentageFormate() + ")" : "");
    }
}
