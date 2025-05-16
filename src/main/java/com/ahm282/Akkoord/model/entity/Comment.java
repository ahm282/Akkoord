package com.ahm282.Akkoord.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "comment")
@Getter @Setter @NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Many Comments are written by One AppUser (author)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private AppUser author;

    // Many Comments belong to One ApprovalStep
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approval_step_id", nullable = false)
    private ApprovalStep approvalStep;


    // ====== Constructors ====== //
    public Comment(String content, AppUser author, ApprovalStep approvalStep) {
        this.content = content;
        this.author = author;
        this.approvalStep = approvalStep;
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

    // --- Helper methods are on the ApprovalStep class (addComment, removeComment)
}
