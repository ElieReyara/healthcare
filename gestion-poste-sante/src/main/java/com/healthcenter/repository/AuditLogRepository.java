package com.healthcenter.repository;

import com.healthcenter.domain.entities.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository pour la gestion des logs d'audit
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    /**
     * Récupère les logs d'un utilisateur spécifique, triés par date décroissante
     */
    List<AuditLog> findByUtilisateurIdOrderByDateActionDesc(Long utilisateurId);
    
    /**
     * Récupère les logs dans une période donnée
     */
    List<AuditLog> findByDateActionBetween(LocalDateTime debut, LocalDateTime fin);
    
    /**
     * Récupère les logs pour un module spécifique
     */
    List<AuditLog> findByModule(String module);
    
    /**
     * Récupère les 100 derniers logs
     */
    List<AuditLog> findTop100ByOrderByDateActionDesc();
}
