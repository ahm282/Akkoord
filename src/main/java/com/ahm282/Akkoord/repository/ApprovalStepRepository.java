package com.ahm282.Akkoord.repository;

import com.ahm282.Akkoord.model.entity.AppUser;
import com.ahm282.Akkoord.model.entity.ApprovalStep;
import com.ahm282.Akkoord.model.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApprovalStepRepository extends JpaRepository<ApprovalStep, UUID> {
    List<ApprovalStep> findByDocument(Document document);
    List<ApprovalStep> findByAssignedUser(AppUser user);
    List<ApprovalStep> findByCreatedBy(AppUser user);
    Optional<ApprovalStep> findByDocumentAndStepOrder(Document document, Integer stepOrder);
}