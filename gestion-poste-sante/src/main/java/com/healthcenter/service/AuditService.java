package com.healthcenter.service;

import com.healthcenter.domain.entities.AuditLog;
import com.healthcenter.domain.entities.Utilisateur;
import com.healthcenter.domain.enums.ActionAudit;
import com.healthcenter.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service de gestion des logs d'audit
 * Trace toutes les actions importantes du système pour conformité et sécurité
 */
@Service
public class AuditService {
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    
    /**
     * Enregistre une action dans les logs d'audit
     * @Async permet l'exécution asynchrone pour ne pas ralentir l'application
     */
    @Async
    @Transactional
    public void logAction(Utilisateur utilisateur, ActionAudit action, String module, Long entityId, String description) {
        AuditLog log = new AuditLog();
        log.setUtilisateur(utilisateur);
        log.setAction(action);
        log.setModule(module);
        log.setEntityId(entityId);
        log.setDescription(description);
        log.setDateAction(LocalDateTime.now());
        
        auditLogRepository.save(log);
    }
    
    /**
     * Récupère tous les logs d'un utilisateur
     */
    public List<AuditLog> obtenirLogsUtilisateur(Long utilisateurId) {
        return auditLogRepository.findByUtilisateurIdOrderByDateActionDesc(utilisateurId);
    }
    
    /**
     * Récupère les logs d'une période donnée
     */
    public List<AuditLog> obtenirLogsPeriode(LocalDate debut, LocalDate fin) {
        LocalDateTime dateDebut = debut.atStartOfDay();
        LocalDateTime dateFin = fin.plusDays(1).atStartOfDay();
        return auditLogRepository.findByDateActionBetween(dateDebut, dateFin);
    }
    
    /**
     * Récupère les logs d'un module spécifique
     */
    public List<AuditLog> obtenirLogsModule(String module) {
        return auditLogRepository.findByModule(module);
    }
    
    /**
     * Récupère les derniers logs (limite)
     */
    public List<AuditLog> obtenirDerniersLogs(int limit) {
        List<AuditLog> logs = auditLogRepository.findTop100ByOrderByDateActionDesc();
        return logs.size() > limit ? logs.subList(0, limit) : logs;
    }
    
    /**
     * Récupère tous les logs (attention: peut être volumineux)
     */
    public List<AuditLog> obtenirTousLesLogs() {
        return auditLogRepository.findAll();
    }
}
