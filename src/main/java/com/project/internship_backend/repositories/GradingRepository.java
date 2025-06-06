package com.project.internship_backend.repositories;

import com.project.internship_backend.entities.Grading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GradingRepository extends JpaRepository<Grading, Long> {
    @Query("SELECT g FROM Grading g WHERE g.submission.id = :submissionId")
    Optional<Grading> findBySubmissionId(@Param("submissionId") Long submissionId);

}
