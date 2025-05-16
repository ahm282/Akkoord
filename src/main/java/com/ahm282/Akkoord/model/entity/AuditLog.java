package com.ahm282.Akkoord.model.entity;

import com.ahm282.Akkoord.model.enums.AuditAction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_log")
@Getter @Setter @NoArgsConstructor
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    private AuditAction action; // => CREATED, APPROVED, etc...

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // JSONB column mapping for metadata
    @Column(name = "metadata", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String metadata; // additional data related to the action

    // Many AuditLogs belong to One Document
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    // Many AuditLogs are performed by One AppUser (the actor)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", nullable = false)
    private AppUser actor;


    // ====== Constructors ====== //
    public AuditLog(AuditAction action, Document document, AppUser actor) {
        this.action = action;
        this.document = document;
        this.actor = actor;
    }

    public AuditLog(AuditAction action, String metadata, Document document, AppUser actor) {
        this.action = action;
        this.metadata = metadata;
        this.document = document;
        this.actor = actor;
    }

    // ====== Lifecycle methods ====== //
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Helper methods are on Document side (addAuditLog, removeAuditLog)
}
