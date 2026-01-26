package com.healthcenter.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO pour les statistiques des consultations.
 * Regroupe les données d'activité et de répartition.
 */
public class StatistiquesConsultations {
    
    private Long nbTotal;
    private Long nbPeriode;
    private List<EvolutionData> evolutionTemporelle;
    private List<RepartitionData> maladiesFrequentes;
    private List<RepartitionData> consultationsParPersonnel;
    private Double moyenneParJour;
    
    /**
     * Constructeur vide.
     */
    public StatistiquesConsultations() {
        this.evolutionTemporelle = new ArrayList<>();
        this.maladiesFrequentes = new ArrayList<>();
        this.consultationsParPersonnel = new ArrayList<>();
    }
    
    // Getters et Setters
    
    public Long getNbTotal() {
        return nbTotal;
    }
    
    public void setNbTotal(Long nbTotal) {
        this.nbTotal = nbTotal;
    }
    
    public Long getNbPeriode() {
        return nbPeriode;
    }
    
    public void setNbPeriode(Long nbPeriode) {
        this.nbPeriode = nbPeriode;
    }
    
    public List<EvolutionData> getEvolutionTemporelle() {
        return evolutionTemporelle;
    }
    
    public void setEvolutionTemporelle(List<EvolutionData> evolutionTemporelle) {
        this.evolutionTemporelle = evolutionTemporelle;
    }
    
    public List<RepartitionData> getMaladiesFrequentes() {
        return maladiesFrequentes;
    }
    
    public void setMaladiesFrequentes(List<RepartitionData> maladiesFrequentes) {
        this.maladiesFrequentes = maladiesFrequentes;
    }
    
    public List<RepartitionData> getConsultationsParPersonnel() {
        return consultationsParPersonnel;
    }
    
    public void setConsultationsParPersonnel(List<RepartitionData> consultationsParPersonnel) {
        this.consultationsParPersonnel = consultationsParPersonnel;
    }
    
    public Double getMoyenneParJour() {
        return moyenneParJour;
    }
    
    public void setMoyenneParJour(Double moyenneParJour) {
        this.moyenneParJour = moyenneParJour;
    }
}
