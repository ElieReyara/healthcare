package com.healthcenter.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DTO pour les statistiques des patients.
 * Regroupe les données démographiques et d'évolution.
 */
public class StatistiquesPatients {
    
    private Long nbTotal;
    private Map<String, Long> repartitionSexe;
    private Map<String, Long> repartitionAge;
    private Double moyenneAge;
    private List<EvolutionData> nouveauxPatientsMois;
    
    /**
     * Constructeur vide.
     */
    public StatistiquesPatients() {
        this.repartitionSexe = new HashMap<>();
        this.repartitionAge = new HashMap<>();
        this.nouveauxPatientsMois = new ArrayList<>();
    }
    
    // Getters et Setters
    
    public Long getNbTotal() {
        return nbTotal;
    }
    
    public void setNbTotal(Long nbTotal) {
        this.nbTotal = nbTotal;
    }
    
    public Map<String, Long> getRepartitionSexe() {
        return repartitionSexe;
    }
    
    public void setRepartitionSexe(Map<String, Long> repartitionSexe) {
        this.repartitionSexe = repartitionSexe;
    }
    
    public Map<String, Long> getRepartitionAge() {
        return repartitionAge;
    }
    
    public void setRepartitionAge(Map<String, Long> repartitionAge) {
        this.repartitionAge = repartitionAge;
    }
    
    public Double getMoyenneAge() {
        return moyenneAge;
    }
    
    public void setMoyenneAge(Double moyenneAge) {
        this.moyenneAge = moyenneAge;
    }
    
    public List<EvolutionData> getNouveauxPatientsMois() {
        return nouveauxPatientsMois;
    }
    
    public void setNouveauxPatientsMois(List<EvolutionData> nouveauxPatientsMois) {
        this.nouveauxPatientsMois = nouveauxPatientsMois;
    }
}
