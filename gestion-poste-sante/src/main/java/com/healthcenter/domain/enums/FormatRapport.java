package com.healthcenter.domain.enums;

/**
 * Enumération des formats de rapports disponibles.
 * Définit les formats de fichiers pour l'export des rapports.
 */
public enum FormatRapport {
    
    PDF("PDF", "pdf"),
    EXCEL("Excel (XLSX)", "xlsx");
    
    private final String libelle;
    private final String extension;
    
    /**
     * Constructeur.
     *
     * @param libelle Le libellé du format
     * @param extension L'extension du fichier (sans le point)
     */
    FormatRapport(String libelle, String extension) {
        this.libelle = libelle;
        this.extension = extension;
    }
    
    /**
     * Retourne le libellé du format.
     *
     * @return Le libellé
     */
    public String getLibelle() {
        return libelle;
    }
    
    /**
     * Retourne l'extension du fichier.
     *
     * @return L'extension (sans le point)
     */
    public String getExtension() {
        return extension;
    }
    
    @Override
    public String toString() {
        return libelle;
    }
}
