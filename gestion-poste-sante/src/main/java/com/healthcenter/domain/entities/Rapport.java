package com.healthcenter.domain.entities;

import com.healthcenter.domain.enums.FormatRapport;
import com.healthcenter.domain.enums.TypeRapport;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entité représentant un rapport généré dans le système.
 * Conserve l'historique des rapports avec leurs métadonnées.
 */
@Entity
@Table(name = "rapports")
public class Rapport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type_rapport", nullable = false, length = 50)
    private TypeRapport typeRapport;
    
    @Column(name = "date_generation", nullable = false)
    private LocalDateTime dateGeneration;
    
    @Column(name = "date_debut")
    private LocalDate dateDebut;
    
    @Column(name = "date_fin")
    private LocalDate dateFin;
    
    @Column(name = "nom_fichier", length = 500)
    private String nomFichier;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "format_fichier", nullable = false, length = 20)
    private FormatRapport formatFichier;
    
    @Column(name = "genere_par", length = 100)
    private String generePar;
    
    @Column(name = "taille_fichier")
    private Long tailleFichier;
    
    /**
     * Constructeur vide pour JPA.
     */
    public Rapport() {
        this.dateGeneration = LocalDateTime.now();
        this.generePar = "System";
    }
    
    /**
     * Constructeur avec paramètres.
     *
     * @param typeRapport Le type de rapport
     * @param dateDebut La date de début de la période couverte
     * @param dateFin La date de fin de la période couverte
     * @param formatFichier Le format du fichier généré
     */
    public Rapport(TypeRapport typeRapport, LocalDate dateDebut, LocalDate dateFin, FormatRapport formatFichier) {
        this();
        this.typeRapport = typeRapport;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.formatFichier = formatFichier;
    }
    
    // Getters et Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public TypeRapport getTypeRapport() {
        return typeRapport;
    }
    
    public void setTypeRapport(TypeRapport typeRapport) {
        this.typeRapport = typeRapport;
    }
    
    public LocalDateTime getDateGeneration() {
        return dateGeneration;
    }
    
    public void setDateGeneration(LocalDateTime dateGeneration) {
        this.dateGeneration = dateGeneration;
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
    
    public String getNomFichier() {
        return nomFichier;
    }
    
    public void setNomFichier(String nomFichier) {
        this.nomFichier = nomFichier;
    }
    
    public FormatRapport getFormatFichier() {
        return formatFichier;
    }
    
    public void setFormatFichier(FormatRapport formatFichier) {
        this.formatFichier = formatFichier;
    }
    
    public String getGenerePar() {
        return generePar;
    }
    
    public void setGenerePar(String generePar) {
        this.generePar = generePar;
    }
    
    public Long getTailleFichier() {
        return tailleFichier;
    }
    
    public void setTailleFichier(Long tailleFichier) {
        this.tailleFichier = tailleFichier;
    }
    
    /**
     * Retourne la taille du fichier formatée.
     *
     * @return La taille formatée (ex: "2.5 MB")
     */
    public String getTailleFormatee() {
        if (tailleFichier == null || tailleFichier == 0) {
            return "0 B";
        }
        
        double taille = tailleFichier;
        String[] unites = {"B", "KB", "MB", "GB"};
        int uniteIndex = 0;
        
        while (taille >= 1024 && uniteIndex < unites.length - 1) {
            taille /= 1024;
            uniteIndex++;
        }
        
        return String.format("%.2f %s", taille, unites[uniteIndex]);
    }
    
    @Override
    public String toString() {
        return "Rapport{" +
                "id=" + id +
                ", typeRapport=" + typeRapport +
                ", dateGeneration=" + dateGeneration +
                ", periode=" + dateDebut + " - " + dateFin +
                ", format=" + formatFichier +
                '}';
    }
}
