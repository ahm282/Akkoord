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
@Table(name= "department")
@Getter @Setter @NoArgsConstructor
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name="name", nullable = false, unique = true)
    private String name;

    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name="updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // One Department has Many AppUsers
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AppUser> users = new ArrayList<>(); // Initialize list

    // One Department has Many Documents
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> documents = new ArrayList<>(); // Initialize list


    // ====== Constructors ====== //
    public Department(String name) {
        this.name = name;
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

    // ====== Helper methods for bidirectional relationship ====== //

    //====== AppUser ====== //
    public void addUser(AppUser user) {
        if (user != null) {
            this.users.add(user);
            user.setDepartment(this);
        }
    }

    public void removeUser(AppUser user) {
        if (user != null && this.users.contains(user)) {
            this.users.remove(user);
            user.setDepartment(null);
        }
    }

    //====== Document ====== //
    public void addDocument(Document document) {
        if (document != null) {
            this.documents.add(document);
            document.setDepartment(this);
        }
    }

    public void removeDocument(Document document) {
        if (document != null && this.documents.contains(document)) {
            this.documents.remove(document);
            document.setDepartment(null);
        }
    }
}
