package com.healthcenter.service;

import com.healthcenter.domain.entities.Medicament;
import com.healthcenter.domain.entities.MouvementStock;
import com.healthcenter.domain.enums.FormeMedicament;
import com.healthcenter.domain.enums.TypeMouvement;
import com.healthcenter.dto.MedicamentDTO;
import com.healthcenter.repository.MedicamentRepository;
import com.healthcenter.repository.MouvementStockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * SERVICE LAYER = Logique métier pour Medicament.
 * 
 * Responsabilités :
 * - Valider les données médicament (business rules)
 * - Gérer les mouvements de stock (entrées/sorties)
 * - Orchestrer MedicamentRepository + MouvementStockRepository
 * - Convertir DTO ↔ Entity
 * - Gérer les transactions (@Transactional)
 * 
 * Règles métier Medicament :
 * - Nom : OBLIGATOIRE, unique recommandé
 * - Forme : OBLIGATOIRE
 * - Stock : >= 0 toujours
 * - Stock sortie : vérifier stock suffisant
 */
@Service
public class MedicamentService {
    
    @Autowired
    private MedicamentRepository medicamentRepository;
    
    @Autowired
    private MouvementStockRepository mouvementStockRepository;
    
    
    // ========== CREATE ==========
    
    /**
     * Créer un nouveau médicament à partir d'un DTO.
     * 
     * @Transactional : Si erreur, rollback automatique.
     * @param dto Données venant de l'UI
     * @return Medicament créé avec ID généré
     * @throws IllegalArgumentException si validation échoue
     */
    @Transactional
    public Medicament creerMedicament(MedicamentDTO dto) {
        // VALIDATION
        validerMedicamentDTO(dto);
        
        // CONVERSION DTO → Entity
        Medicament medicament = mapToEntity(dto);
        
        // SAUVEGARDE
        return medicamentRepository.save(medicament);
    }
    
    
    // ========== READ ==========
    
    /**
     * Récupérer tous les médicaments.
     * @return Liste complète (peut être vide)
     */
    @Transactional(readOnly = true)
    public List<Medicament> obtenirTousMedicaments() {
        return medicamentRepository.findAll();
    }
    
    /**
     * Rechercher un médicament par ID.
     * @param id Identifiant
     * @return Optional (vide si non trouvé)
     */
    @Transactional(readOnly = true)
    public Optional<Medicament> obtenirMedicamentParId(Long id) {
        return medicamentRepository.findById(id);
    }
    
    /**
     * Rechercher médicaments par nom (insensible casse, partielle).
     * 
     * @param nom Nom ou partie du nom
     * @return Liste des médicaments correspondants
     */
    @Transactional(readOnly = true)
    public List<Medicament> rechercherMedicamentsParNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            return obtenirTousMedicaments();
        }
        return medicamentRepository.findByNomContainingIgnoreCase(nom.trim());
    }
    
    /**
     * Obtenir médicaments en rupture de stock.
     * Stock actuel < seuil d'alerte.
     * 
     * @return Liste des médicaments en alerte
     */
    @Transactional(readOnly = true)
    public List<Medicament> obtenirMedicamentsEnRuptureStock() {
        return medicamentRepository.findMedicamentsEnRuptureStock();
    }
    
    /**
     * Rechercher médicaments par forme.
     * 
     * @param forme Forme pharmaceutique
     * @return Liste des médicaments de cette forme
     */
    @Transactional(readOnly = true)
    public List<Medicament> obtenirMedicamentsParForme(FormeMedicament forme) {
        if (forme == null) {
            return obtenirTousMedicaments();
        }
        return medicamentRepository.findByForme(forme);
    }
    
    
    // ========== UPDATE ==========
    
    /**
     * Mettre à jour un médicament existant.
     * 
     * @param id ID du médicament à modifier
     * @param dto Nouvelles données
     * @return Medicament mis à jour
     * @throws IllegalArgumentException si médicament inexistant
     */
    @Transactional
    public Medicament mettreAJourMedicament(Long id, MedicamentDTO dto) {
        // Vérifier existence
        Medicament medicament = medicamentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "Médicament avec ID " + id + " introuvable"
            ));
        
        // VALIDATION
        validerMedicamentDTO(dto);
        
        // Mise à jour des champs
        medicament.setNom(dto.getNom());
        medicament.setDosage(dto.getDosage());
        medicament.setForme(FormeMedicament.valueOf(dto.getForme()));
        medicament.setPrix(dto.getPrix());
        medicament.setStockActuel(dto.getStockActuel());
        medicament.setSeuilAlerte(dto.getSeuilAlerte());
        
        return medicamentRepository.save(medicament);
    }
    
    
    // ========== DELETE ==========
    
    /**
     * Supprimer un médicament par ID.
     * ATTENTION : Vérifier pas de mouvements stock liés (cascade = ALL les supprime !).
     * 
     * @param id Identifiant
     * @throws IllegalArgumentException si médicament inexistant ou a des mouvements
     */
    @Transactional
    public void supprimerMedicament(Long id) {
        // Vérifier existence
        Medicament medicament = medicamentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "Médicament avec ID " + id + " introuvable"
            ));
        
        // Vérifier pas de mouvements liés (sécurité)
        List<MouvementStock> mouvements = mouvementStockRepository.findByMedicamentIdOrderByDateMouvementDesc(id);
        if (!mouvements.isEmpty()) {
            throw new IllegalArgumentException(
                "Impossible de supprimer : Le médicament a " + mouvements.size() + 
                " mouvement(s) de stock enregistré(s). Supprimez d'abord l'historique ou archivez le médicament."
            );
        }
        
        medicamentRepository.deleteById(id);
    }
    
    
    // ========== GESTION STOCK (SPÉCIFIQUE MÉDICAMENTS) ==========
    
    /**
     * Ajuster le stock d'un médicament (ENTREE ou SORTIE).
     * Enregistre le mouvement dans l'historique.
     * 
     * @Transactional : Stock + Mouvement sauvegardés atomiquement.
     * 
     * @param medicamentId ID du médicament
     * @param quantite Quantité à ajouter/retirer (toujours positif)
     * @param type Type de mouvement (ENTREE ou SORTIE)
     * @param motif Raison du mouvement
     * @return Medicament avec stock mis à jour
     * @throws IllegalArgumentException si stock insuffisant (SORTIE) ou données invalides
     */
    @Transactional
    public Medicament ajusterStock(Long medicamentId, Integer quantite, TypeMouvement type, String motif) {
        // VALIDATION
        if (medicamentId == null) {
            throw new IllegalArgumentException("L'ID du médicament est obligatoire");
        }
        
        if (quantite == null || quantite <= 0) {
            throw new IllegalArgumentException("La quantité doit être supérieure à 0");
        }
        
        if (type == null) {
            throw new IllegalArgumentException("Le type de mouvement est obligatoire");
        }
        
        // Récupérer médicament
        Medicament medicament = medicamentRepository.findById(medicamentId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Médicament avec ID " + medicamentId + " introuvable"
            ));
        
        // Stock avant mouvement
        Integer stockAvant = medicament.getStockActuel();
        Integer stockApres;
        
        // Calculer nouveau stock selon type
        if (type == TypeMouvement.ENTREE) {
            // ENTREE : Ajout stock
            stockApres = stockAvant + quantite;
        } else {
            // SORTIE : Retrait stock
            // Vérifier stock suffisant
            if (stockAvant < quantite) {
                throw new IllegalArgumentException(
                    "Stock insuffisant pour sortie. Stock actuel : " + stockAvant + 
                    ", Quantité demandée : " + quantite
                );
            }
            stockApres = stockAvant - quantite;
        }
        
        // Créer mouvement pour historique
        MouvementStock mouvement = new MouvementStock(medicament, type, quantite, motif);
        mouvement.setStockAvant(stockAvant);
        mouvement.setStockApres(stockApres);
        mouvement.setDateMouvement(LocalDateTime.now());
        
        // Mettre à jour stock médicament
        medicament.setStockActuel(stockApres);
        
        // Sauvegarder mouvement + médicament
        mouvementStockRepository.save(mouvement);
        return medicamentRepository.save(medicament);
    }
    
    /**
     * Obtenir l'historique des mouvements de stock d'un médicament.
     * Triés par date décroissante (plus récents en premier).
     * 
     * @param medicamentId ID du médicament
     * @return Liste des mouvements
     */
    @Transactional(readOnly = true)
    public List<MouvementStock> obtenirHistoriqueStock(Long medicamentId) {
        return mouvementStockRepository.findByMedicamentIdOrderByDateMouvementDesc(medicamentId);
    }
    
    
    // ========== MÉTHODES PRIVÉES (Helpers) ==========
    
    /**
     * Validation des données métier.
     * @throws IllegalArgumentException si données invalides
     */
    private void validerMedicamentDTO(MedicamentDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Les données du médicament sont obligatoires");
        }
        
        // Nom obligatoire
        if (dto.getNom() == null || dto.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du médicament est obligatoire");
        }
        
        // Forme obligatoire
        if (dto.getForme() == null || dto.getForme().trim().isEmpty()) {
            throw new IllegalArgumentException("La forme pharmaceutique est obligatoire");
        }
        
        // Vérifier forme valide (enum)
        try {
            FormeMedicament.valueOf(dto.getForme());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Forme pharmaceutique invalide : " + dto.getForme() + 
                ". Valeurs acceptées : COMPRIME, SIROP, INJECTION, POMMADE, GELULE"
            );
        }
        
        // Stock >= 0
        if (dto.getStockActuel() == null || dto.getStockActuel() < 0) {
            throw new IllegalArgumentException("Le stock actuel doit être supérieur ou égal à 0");
        }
        
        // Seuil alerte >= 0 si défini
        if (dto.getSeuilAlerte() != null && dto.getSeuilAlerte() < 0) {
            throw new IllegalArgumentException("Le seuil d'alerte doit être supérieur ou égal à 0");
        }
    }
    
    /**
     * Convertir DTO → Entity.
     * 
     * @param dto DTO médicament
     * @return Entity Medicament
     */
    private Medicament mapToEntity(MedicamentDTO dto) {
        Medicament medicament = new Medicament();
        medicament.setNom(dto.getNom());
        medicament.setDosage(dto.getDosage());
        medicament.setForme(FormeMedicament.valueOf(dto.getForme()));
        medicament.setPrix(dto.getPrix());
        medicament.setStockActuel(dto.getStockActuel());
        medicament.setSeuilAlerte(dto.getSeuilAlerte());
        return medicament;
    }
}
