package com.project.internship_backend.repositories;

import com.project.internship_backend.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByUserIdAndSubmissionId(@Param("userId") Long userId,
                                           @Param("submissionId") Long submissionId);

    List<Comment> findBySubmissionId(@Param("submissionId") Long submissionId);
}
