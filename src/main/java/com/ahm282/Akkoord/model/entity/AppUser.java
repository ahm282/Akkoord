package com.ahm282.Akkoord.model.entity;

import com.ahm282.Akkoord.model.enums.UserAccessLevel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "app_user")
@Getter @Setter @NoArgsConstructor
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name="full_name", nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name="password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name="role", nullable = false)
    private UserAccessLevel role;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name="updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy="owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> documents = new ArrayList<>();

    @OneToMany(mappedBy = "assignedUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApprovalStep> assignedApprovalSteps = new ArrayList<>();

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApprovalStep> createdApprovalSteps = new ArrayList<>();


    // ====== Constructors ====== //
    // User (no department)
    public AppUser(String fullName, String email, String passwordHash, UserAccessLevel role) {
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role; // initial role
    }

    // User with a department
    public AppUser(String fullName, String email, String passwordHash, UserAccessLevel role, Department department) {
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role; // initial role
        this.department = department;
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
        this.updatedAt= LocalDateTime.now();
    }

    // ====== Helper methods for bidirectional relationships ====== //

    // ====== Department ====== //
    public void setDepartment(Department department) {
        this.department = department;
        if (department != null) {
            department.getUsers().add(this);
        }
    }

    public void removeDepartment() {
        if (this.department != null) {
            this.department.getUsers().remove(this); // Remove from department's users
            this.department = null;
        }
    }

    // ====== Document ====== //
    public void addDocument(Document document) {
        this.documents.add(document);
        document.setOwner(this);
    }

    public void removeDocument(Document document) {
        this.documents.remove(document);
        document.setOwner(null);
    }

    // ====== Assigned ApprovalStep ====== //
    public void addAssignedApprovalStep(ApprovalStep approvalStep) {
        this.assignedApprovalSteps.add(approvalStep);
        approvalStep.setAssignedUser(this);
    }

    public void removeAssignedApprovalStep(ApprovalStep approvalStep) {
        this.assignedApprovalSteps.remove(approvalStep);
        approvalStep.setAssignedUser(null);
    }

    // ====== Created ApprovalStep ====== //
    public void addCreatedApprovalStep(ApprovalStep approvalStep) {
        this.createdApprovalSteps.add(approvalStep);
        approvalStep.setCreatedBy(this);
    }

    public void removeCreatedApprovalStep(ApprovalStep approvalStep) {
        this.createdApprovalSteps.remove(approvalStep);
        approvalStep.setCreatedBy(null);
    }
}
