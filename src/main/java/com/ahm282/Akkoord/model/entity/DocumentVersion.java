package com.ahm282.Akkoord.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "document_version")
@Getter @Setter @NoArgsConstructor
public class DocumentVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "version_number", nullable = false)
    private int versionNumber;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "file_hash") // Optional
    private String fileHash;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Many DocumentVersions belong to One Document
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    // Many DocumentVersions are submitted by One AppUser
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by_user_id", nullable = false)
    private AppUser submittedBy;

    // One DocumentVersion has Many ApprovalSteps
    @OneToMany(mappedBy = "documentVersion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApprovalStep> approvalSteps = new ArrayList<>();


    // ====== Constructors ====== //
    public DocumentVersion(int versionNumber, String filePath, String fileHash, Document document, AppUser submittedBy) {
        this.versionNumber = versionNumber;
        this.filePath = filePath;
        this.fileHash = fileHash;
        this.document = document;
        this.submittedBy = submittedBy;
    }

    public DocumentVersion(int versionNumber, String filePath, Document document, AppUser submittedBy) {
        this.versionNumber = versionNumber;
        this.filePath = filePath;
        this.document = document;
        this.submittedBy = submittedBy;
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

    // ====== ApprovalStep ====== //
    public void addApprovalStep(ApprovalStep approvalStep) {
        if (approvalStep != null) {
            this.approvalSteps.add(approvalStep);
            approvalStep.setDocumentVersion(this);
        }
    }

    public void removeApprovalStep(ApprovalStep approvalStep) {
        if (approvalStep != null && this.approvalSteps.contains(approvalStep)) {
            this.approvalSteps.remove(approvalStep);
            approvalStep.setDocumentVersion(null);
        }
    }

    // ====== Document ====== //
    public void setDocument(Document document) {
        // Prevent infinite loops if the relationship is already set
        if (this.document == document) {
            return;
        }

        // Remove this version from the old document's list if it was previously associated => on update
        if (this.document != null) {
            this.document.getVersions().remove(this);
        }

        // Set the new document
        this.document = document;

        // Add this version to the new document's list
        if (document != null) {
            document.getVersions().add(this);
        }
    }

    //  AppUser relationship is unidirectional => handled in the AppUser class
}