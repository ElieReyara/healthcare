package com.healthcenter.domain.entities;

import com.healthcenter.domain.enums.JourSemaine;
import jakarta.persistence.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * ENTITY - Disponibilité Personnel
 * 
 * Représente les créneaux horaires de disponibilité du personnel par jour de la semaine.
 * Permet de gérer le planning hebdomadaire (ex: Lundi 08:00-12:00, Mardi 14:00-18:00).
 * 
 * Validation période : dateDebut <= date actuelle <= dateFin (null = permanent)
 * 
 * Exemple :
 * - Dr Diallo : LUNDI, 08:00-12:00, dateDebut=2024-01-01, dateFin=null → permanent
 * - Infirmière Ba : MERCREDI, 14:00-18:00, dateDebut=2024-06-01, dateFin=2024-12-31 → temporaire
 * 
 * @author Health Center Team
 */
@Entity
@Table(name = "disponibilites_personnel")
public class DisponibilitePersonnel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personnel_id", nullable = false)
    private Personnel personnel;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "jour_semaine", nullable = false, length = 20)
    private JourSemaine jourSemaine;
    
    @Column(name = "heure_debut", nullable = false)
    private LocalTime heureDebut;
    
    @Column(name = "heure_fin", nullable = false)
    private LocalTime heureFin;
    
    @Column(name = "date_debut")
    private LocalDate dateDebut;
    
    @Column(name = "date_fin")
    private LocalDate dateFin;
    
    
    // ========== CONSTRUCTEURS ==========
    
    /**
     * Constructeur vide (requis par JPA).
     */
    public DisponibilitePersonnel() {
    }
    
    /**
     * Constructeur avec champs obligatoires.
     * 
     * @param personnel Personnel concerné
     * @param jourSemaine Jour de la semaine
     * @param heureDebut Heure début créneau
     * @param heureFin Heure fin créneau
     */
    public DisponibilitePersonnel(Personnel personnel, JourSemaine jourSemaine, 
                                   LocalTime heureDebut, LocalTime heureFin) {
        this.personnel = personnel;
        this.jourSemaine = jourSemaine;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
    }
    
    
    // ========== MÉTHODES UTILITAIRES ==========
    
    /**
     * Vérifie si cette disponibilité est active à une date donnée.
     * 
     * @param date Date à vérifier
     * @return true si date dans la période de validité
     */
    public boolean estActif(LocalDate date) {
        if (date == null) {
            return false;
        }
        
        // Vérifie dateDebut (null = depuis toujours)
        if (dateDebut != null && date.isBefore(dateDebut)) {
            return false;
        }
        
        // Vérifie dateFin (null = permanent)
        if (dateFin != null && date.isAfter(dateFin)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * @return Durée du créneau horaire (heureFin - heureDebut)
     */
    public Duration getDuree() {
        if (heureDebut == null || heureFin == null) {
            return Duration.ZERO;
        }
        return Duration.between(heureDebut, heureFin);
    }
    
    /**
     * @return Représentation textuelle du créneau (ex: "08:00-12:00")
     */
    public String getCreneauFormate() {
        if (heureDebut == null || heureFin == null) {
            return "";
        }
        return heureDebut.toString() + "-" + heureFin.toString();
    }
    
    
    // ========== GETTERS / SETTERS ==========
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Personnel getPersonnel() {
        return personnel;
    }
    
    public void setPersonnel(Personnel personnel) {
        this.personnel = personnel;
    }
    
    public JourSemaine getJourSemaine() {
        return jourSemaine;
    }
    
    public void setJourSemaine(JourSemaine jourSemaine) {
        this.jourSemaine = jourSemaine;
    }
    
    public LocalTime getHeureDebut() {
        return heureDebut;
    }
    
    public void setHeureDebut(LocalTime heureDebut) {
        this.heureDebut = heureDebut;
    }
    
    public LocalTime getHeureFin() {
        return heureFin;
    }
    
    public void setHeureFin(LocalTime heureFin) {
        this.heureFin = heureFin;
    }
    
    public LocalDate getDateDebut() {
        return dateDebut;
    }
    
    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }
    
    public LocalDate getDateFin() {
        return dateFin;
    }
    
    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }
}
