package com.ahm282.Akkoord.model.entity;

import com.ahm282.Akkoord.model.enums.ApprovalStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "approval_step")
@Getter @Setter @NoArgsConstructor
@Check(constraints = "(assigned_user_id IS NOT NULL AND assigned_department_id IS NULL) OR" +
        "(assigned_department_id IS NOT NULL AND assigned_user_id IS NULL)")
public class ApprovalStep {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "step_order", nullable = false)
    private int stepOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus status = ApprovalStatus.PENDING;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Many ApprovalSteps belong to One DocumentVersion
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_version_id", nullable = false)
    private DocumentVersion documentVersion;

    // Many ApprovalSteps can be assigned to One AppUser
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_user_id")
    private AppUser assignedUser;

    // Many ApprovalSteps can be assigned to One Department
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_department_id")
    private Department assignedDepartment;

    // Many ApprovalSteps are created by One AppUser
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private AppUser createdBy;

    // One ApprovalStep has Many Comments
    @OneToMany(mappedBy = "approvalStep", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();


    // ====== Constructors ====== //
    public ApprovalStep(int stepOrder, DocumentVersion documentVersion, AppUser assignedUser, AppUser createdBy) {
        this.stepOrder = stepOrder;
        this.documentVersion = documentVersion;
        this.assignedUser = assignedUser;
        this.createdBy = createdBy;
    }

    public ApprovalStep(int stepOrder, DocumentVersion documentVersion, Department assignedDepartment, AppUser createdBy) {
        this.stepOrder = stepOrder;
        this.documentVersion = documentVersion;
        this.assignedDepartment = assignedDepartment;
        this.createdBy = createdBy;
    }

    // ====== Lifecycle methods ====== //
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.status == null) {
            this.status = ApprovalStatus.PENDING;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ====== Helper methods for bidirectional relationships ====== //

    // ====== Comments ====== //
    public void addComment(Comment comment) {
        if (comment != null) {
            this.comments.add(comment);
            comment.setApprovalStep(this);
        }
    }

    public void removeComment(Comment comment) {
        if (comment != null && this.comments.contains(comment)) {
            this.comments.remove(comment);
            comment.setApprovalStep(null);
        }
    }

    // Ensure that only one of assignedUser or assignedDepartment is active at a time
     public void assignToUser(AppUser user) {
         this.assignedUser = user;
         this.assignedDepartment = null;
     }
     public void assignToDepartment(Department department) {
         this.assignedDepartment = department;
         this.assignedUser = null;
     }
}