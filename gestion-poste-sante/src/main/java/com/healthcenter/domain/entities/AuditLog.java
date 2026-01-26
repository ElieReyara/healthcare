package com.healthcenter.domain.entities;

import com.healthcenter.domain.enums.ActionAudit;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity représentant une entrée d'audit
 * Trace toutes les actions effectuées dans le système pour la conformité et la sécurité
 */
@Entity
@Table(name = "audit_logs")
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionAudit action;
    
    @Column(length = 100)
    private String module; // Ex: "PATIENT", "CONSULTATION", "MEDICAMENT"
    
    @Column(name = "entity_id")
    private Long entityId; // ID de l'entité concernée
    
    @Column(length = 1000)
    private String description;
    
    @Column(name = "date_action", nullable = false)
    private LocalDateTime dateAction;
    
    @Column(name = "adresse_ip", length = 50)
    private String adresseIP; // Optionnel pour traçabilité réseau
    
    // Constructeurs
    public AuditLog() {
        this.dateAction = LocalDateTime.now();
    }
    
    public AuditLog(Utilisateur utilisateur, ActionAudit action, String module, Long entityId, String description) {
        this();
        this.utilisateur = utilisateur;
        this.action = action;
        this.module = module;
        this.entityId = entityId;
        this.description = description;
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }
    
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
    
    public ActionAudit getAction() {
        return action;
    }
    
    public void setAction(ActionAudit action) {
        this.action = action;
    }
    
    public String getModule() {
        return module;
    }
    
    public void setModule(String module) {
        this.module = module;
    }
    
    public Long getEntityId() {
        return entityId;
    }
    
    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getDateAction() {
        return dateAction;
    }
    
    public void setDateAction(LocalDateTime dateAction) {
        this.dateAction = dateAction;
    }
    
    public String getAdresseIP() {
        return adresseIP;
    }
    
    public void setAdresseIP(String adresseIP) {
        this.adresseIP = adresseIP;
    }
}
