package com.healthcenter.dto;

/**
 * DTO CalendrierVaccinal = Objet transfert données pour calendrier vaccinal (UI ↔ Service).
 * 
 * POJO sans annotations JPA.
 */
public class CalendrierVaccinalDTO {
    
    private String vaccin; // Nom enum TypeVaccin
    private Integer ageRecommande; // Âge en jours
    private Integer nombreRappels;
    private Integer delaiRappel; // Délai en jours
    private Boolean obligatoire;
    private String description;
    
    
    // ========== CONSTRUCTEURS ==========
    
    public CalendrierVaccinalDTO() {
    }
    
    public CalendrierVaccinalDTO(String vaccin, Integer ageRecommande, Integer nombreRappels, 
                                Integer delaiRappel, Boolean obligatoire, String description) {
        this.vaccin = vaccin;
        this.ageRecommande = ageRecommande;
        this.nombreRappels = nombreRappels;
        this.delaiRappel = delaiRappel;
        this.obligatoire = obligatoire;
        this.description = description;
    }
    
    
    // ========== GETTERS & SETTERS ==========
    
    public String getVaccin() {
        return vaccin;
    }
    
    public void setVaccin(String vaccin) {
        this.vaccin = vaccin;
    }
    
    public Integer getAgeRecommande() {
        return ageRecommande;
    }
    
    public void setAgeRecommande(Integer ageRecommande) {
        this.ageRecommande = ageRecommande;
    }
    
    public Integer getNombreRappels() {
        return nombreRappels;
    }
    
    public void setNombreRappels(Integer nombreRappels) {
        this.nombreRappels = nombreRappels;
    }
    
    public Integer getDelaiRappel() {
        return delaiRappel;
    }
    
    public void setDelaiRappel(Integer delaiRappel) {
        this.delaiRappel = delaiRappel;
    }
    
    public Boolean getObligatoire() {
        return obligatoire;
    }
    
    public void setObligatoire(Boolean obligatoire) {
        this.obligatoire = obligatoire;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}
