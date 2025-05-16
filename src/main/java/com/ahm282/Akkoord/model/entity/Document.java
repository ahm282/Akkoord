package com.ahm282.Akkoord.model.entity;

import com.ahm282.Akkoord.model.enums.DocumentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "document")
@Getter @Setter @NoArgsConstructor
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String title;

    // Description is nullable
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentStatus status;

    @Column(name = "current_version", nullable = false)
    private int currentVersion;

    // Many Documents belong to One Department
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Many Documents belong to One AppUser (owner)
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private AppUser owner;

    // One Document has Many DocumentVersions
    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentVersion> versions = new ArrayList<>();

    // One Document has Many AuditLogs
    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AuditLog> auditLogs = new ArrayList<>();

    // ====== Constructors ====== //
    // No description, no version, no audit logs or department
    public Document(String title, DocumentStatus status, AppUser owner) {
        this.title = title;
        this.status = status;
        this.currentVersion = 1; // Initial version
        this.owner = owner;
    }

    // With description, no version, no audit logs or department
    public Document(String title, String description, DocumentStatus status, AppUser owner) {
        this.title = title;
        this.status = status;
        this.currentVersion = 1; // Initial version
        this.owner = owner;
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


    // ====== Helper methods for bidirectional relationships ====== //

    // ====== DocumentVersion ====== //
    public void addVersion(DocumentVersion version) {
        if (version != null) {
            this.versions.add(version);
            version.setDocument(this);
        }
    }

    public void removeVersion(DocumentVersion version) {
        if (version != null) {
            this.versions.remove(version);
            version.setDocument(null);
        }
    }

    // ====== AuditLog ====== //
    public void addAuditLog(AuditLog log) {
        if (log != null) {
            this.auditLogs.add(log);
            log.setDocument(this);
        }
    }

    public void removeAuditLog(AuditLog log) {
        if (log != null) {
            this.auditLogs.remove(log);
            log.setDocument(null);
        }
    }
}
