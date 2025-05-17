package com.ahm282.Akkoord.repository;

import com.ahm282.Akkoord.model.entity.AppUser;
import com.ahm282.Akkoord.model.entity.AuditLog;
import com.ahm282.Akkoord.model.entity.Document;
import com.ahm282.Akkoord.model.enums.AuditAction;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    List<AuditLog> findByDocument(Document document);
    List<AuditLog> findByActor(AppUser user);
    List<AuditLog> findByAction(AuditAction action);
    List<AuditLog> findByCreatedAtBetween(LocalDateTime createdAtBefore, LocalDateTime createdAtAfter, Limit limit);
}