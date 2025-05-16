package com.ahm282.Akkoord.repository;

import com.ahm282.Akkoord.model.entity.AppUser;
import com.ahm282.Akkoord.model.entity.ApprovalStep;
import com.ahm282.Akkoord.model.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    Optional<Comment> findByContent(String content);
    Optional<Comment> findByAuthor(AppUser author);
    List<Comment> findCommentsByApprovalStep(ApprovalStep approvalStep);
}