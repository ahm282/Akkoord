package com.ahm282.Akkoord.repository;

import com.ahm282.Akkoord.model.entity.Document;
import com.ahm282.Akkoord.model.entity.DocumentVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentVersionRepository extends JpaRepository<DocumentVersion, UUID> {
    List<DocumentVersion> findByDocument(Document document);
    List<DocumentVersion> findByDocumentOrderByVersionNumberDesc(Document document);
    Optional<DocumentVersion> findByDocumentAndVersionNumber(Document document, Integer versionNumber);
    Optional<DocumentVersion> findFirstByDocumentOrderByVersionNumberDesc(Document document);
}