package com.healthcenter.domain.entities;

import com.healthcenter.domain.enums.TypeVaccin;
import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * ENTITY CalendrierVaccinal = Schéma vaccinal officiel (référence Sénégal).
 * 
 * Définit pour chaque vaccin :
 * - Âge recommandé d'administration (en jours)
 * - Nombre et délai des rappels
 * - Caractère obligatoire
 */
@Entity
@Table(name = "calendrier_vaccinal")
public class CalendrierVaccinal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false, length = 50)
    private TypeVaccin vaccin;
    
    @Column(name = "age_recommande", nullable = false)
    private Integer ageRecommande; // Âge en jours
    
    @Column(name = "nombre_rappels")
    private Integer nombreRappels = 0;
    
    @Column(name = "delai_rappel")
    private Integer delaiRappel; // Délai en jours entre rappels
    
    @Column(nullable = false)
    private Boolean obligatoire = true;
    
    @Column(length = 500)
    private String description;
    
    
    // ========== CONSTRUCTEURS ==========
    
    public CalendrierVaccinal() {
    }
    
    public CalendrierVaccinal(TypeVaccin vaccin, Integer ageRecommande, Integer nombreRappels, Integer delaiRappel) {
        this.vaccin = vaccin;
        this.ageRecommande = ageRecommande;
        this.nombreRappels = nombreRappels;
        this.delaiRappel = delaiRappel;
        this.obligatoire = true;
    }
    
    
    // ========== MÉTHODES UTILITAIRES ==========
    
    /**
     * Calcule la date du premier rappel à partir de la date d'administration.
     * 
     * @param dateAdministration Date à laquelle le vaccin a été administré
     * @return Date du rappel ou null si pas de rappels
     */
    public LocalDate calculerDateRappel(LocalDate dateAdministration) {
        if (nombreRappels == null || nombreRappels == 0 || delaiRappel == null) {
            return null;
        }
        return dateAdministration.plusDays(delaiRappel);
    }
    
    
    // ========== GETTERS & SETTERS ==========
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public TypeVaccin getVaccin() {
        return vaccin;
    }
    
    public void setVaccin(TypeVaccin vaccin) {
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
