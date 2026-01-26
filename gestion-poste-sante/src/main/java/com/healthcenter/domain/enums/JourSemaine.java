package com.healthcenter.domain.enums;

import java.time.DayOfWeek;

/**
 * ENUM - Jour de la semaine
 * 
 * Représente les 7 jours de la semaine pour gestion planning/disponibilités.
 * Mapping avec java.time.DayOfWeek pour conversion dates.
 * 
 * @author Health Center Team
 */
public enum JourSemaine {
    
    LUNDI("Lundi", 1, DayOfWeek.MONDAY),
    MARDI("Mardi", 2, DayOfWeek.TUESDAY),
    MERCREDI("Mercredi", 3, DayOfWeek.WEDNESDAY),
    JEUDI("Jeudi", 4, DayOfWeek.THURSDAY),
    VENDREDI("Vendredi", 5, DayOfWeek.FRIDAY),
    SAMEDI("Samedi", 6, DayOfWeek.SATURDAY),
    DIMANCHE("Dimanche", 7, DayOfWeek.SUNDAY);
    
    private final String libelle;
    private final int ordre;
    private final DayOfWeek dayOfWeek;
    
    /**
     * Constructeur JourSemaine.
     * 
     * @param libelle Nom français du jour
     * @param ordre Position dans la semaine (1=Lundi, 7=Dimanche)
     * @param dayOfWeek Équivalent java.time.DayOfWeek
     */
    JourSemaine(String libelle, int ordre, DayOfWeek dayOfWeek) {
        this.libelle = libelle;
        this.ordre = ordre;
        this.dayOfWeek = dayOfWeek;
    }
    
    /**
     * @return Libellé français du jour (ex: "Lundi")
     */
    public String getLibelle() {
        return libelle;
    }
    
    /**
     * @return Position dans la semaine (1-7)
     */
    public int getOrdre() {
        return ordre;
    }
    
    /**
     * @return Équivalent java.time.DayOfWeek
     */
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }
    
    /**
     * Convertit un DayOfWeek en JourSemaine.
     * 
     * @param dow java.time.DayOfWeek
     * @return JourSemaine correspondant
     * @throws IllegalArgumentException si dow est null
     */
    public static JourSemaine fromDayOfWeek(DayOfWeek dow) {
        if (dow == null) {
            throw new IllegalArgumentException("DayOfWeek ne peut pas être null");
        }
        
        for (JourSemaine jour : values()) {
            if (jour.dayOfWeek == dow) {
                return jour;
            }
        }
        
        throw new IllegalArgumentException("DayOfWeek inconnu : " + dow);
    }
    
    /**
     * @return Libellé pour affichage dans ComboBox JavaFX
     */
    @Override
    public String toString() {
        return libelle;
    }
}
