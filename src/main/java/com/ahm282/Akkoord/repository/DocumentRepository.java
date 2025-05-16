package com.ahm282.Akkoord.repository;

import com.ahm282.Akkoord.model.entity.AppUser;
import com.ahm282.Akkoord.model.entity.Department;
import com.ahm282.Akkoord.model.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
    Optional<Document> findDocumentById(UUID id);
    Optional<Document> findDocumentByTitle(String title);
    Optional<Document> findDocumentByOwner(AppUser owner);
    List<Document> findByOwner(AppUser owner);
    List<Document> findDocumentsByDepartment(Department department);
}