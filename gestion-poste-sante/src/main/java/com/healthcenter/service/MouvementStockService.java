package com.healthcenter.service;

import com.healthcenter.domain.entities.MouvementStock;
import com.healthcenter.domain.enums.TypeMouvement;
import com.healthcenter.repository.MouvementStockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * SERVICE LAYER = Logique métier pour MouvementStock.
 * 
 * Responsabilités :
 * - Fournir accès lecture seule à l'historique des mouvements
 * - Filtres par date, type, médicament
 * 
 * Note : La création de mouvements se fait via MedicamentService.ajusterStock()
 * (car nécessite mise à jour du stock médicament atomique).
 */
@Service
public class MouvementStockService {
    
    @Autowired
    private MouvementStockRepository mouvementStockRepository;
    
    
    // ========== READ (Lecture historique) ==========
    
    /**
     * Obtenir mouvements de stock d'un médicament.
     * Triés par date décroissante (plus récents en premier).
     * 
     * @param medicamentId ID du médicament
     * @return Liste des mouvements
     */
    @Transactional(readOnly = true)
    public List<MouvementStock> obtenirMouvementsParMedicament(Long medicamentId) {
        return mouvementStockRepository.findByMedicamentIdOrderByDateMouvementDesc(medicamentId);
    }
    
    /**
     * Obtenir mouvements dans un intervalle de dates.
     * Utile pour rapports mensuels/annuels.
     * 
     * @param debut Date/heure début (inclusive)
     * @param fin Date/heure fin (inclusive)
     * @return Liste des mouvements dans l'intervalle
     */
    @Transactional(readOnly = true)
    public List<MouvementStock> obtenirMouvementsEntreDates(LocalDateTime debut, LocalDateTime fin) {
        if (debut == null || fin == null) {
            throw new IllegalArgumentException("Les dates de début et fin sont obligatoires");
        }
        
        if (debut.isAfter(fin)) {
            throw new IllegalArgumentException("La date de début doit être antérieure à la date de fin");
        }
        
        return mouvementStockRepository.findByDateMouvementBetween(debut, fin);
    }
    
    /**
     * Obtenir mouvements par type (ENTREE ou SORTIE).
     * 
     * @param type Type de mouvement
     * @return Liste des mouvements de ce type
     */
    @Transactional(readOnly = true)
    public List<MouvementStock> obtenirMouvementsParType(TypeMouvement type) {
        if (type == null) {
            throw new IllegalArgumentException("Le type de mouvement est obligatoire");
        }
        
        return mouvementStockRepository.findByType(type);
    }
    
    /**
     * Obtenir tous les mouvements (historique complet).
     * À utiliser avec précaution (peut être volumineux).
     * 
     * @return Liste de tous les mouvements
     */
    @Transactional(readOnly = true)
    public List<MouvementStock> obtenirTousMouvements() {
        return mouvementStockRepository.findAll();
    }
}
