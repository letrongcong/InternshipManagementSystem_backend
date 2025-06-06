package com.project.internship_backend.repositories;

import com.project.internship_backend.entities.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    @Query("SELECT s FROM Submission s")
    Page<Submission> findAllSubmissions(Pageable pageable);

    @Query("""
        SELECT s FROM Submission s
        WHERE s.report.id = :reportId
          AND (:keyword IS NULL OR 
               LOWER(s.student.user.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR 
               CAST(s.student.studentCode AS string) LIKE CONCAT('%', :keyword, '%') OR 
               LOWER(s.student.classStudent) LIKE LOWER(CONCAT('%', :keyword, '%')))
    """)
    Page<Submission> findSubmissionsByReport(
            @Param("reportId") Long reportId,
            @Param("keyword") String keyword,
            Pageable pageable
    );
    @Query("""
        SELECT s FROM Submission s
        WHERE s.student.studentCode = :studentCode
          AND (:keyword IS NULL OR 
               LOWER(s.report.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
    """)
    Page<Submission> getSubmissionsByStudent(
            @Param("studentCode") Long studentCode,
            @Param("keyword") String keyword,
            Pageable pageable
    );
    @Query("""
        SELECT s FROM Submission s
        WHERE 
        s.report.id = :reportId AND
        s.student.studentCode = :studentCode
          AND (:keyword IS NULL OR 
               LOWER(s.report.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
    """)
    Page<Submission> getSubmissionsByStudentCodeAndReportId(
            @Param("studentCode") Long studentCode,
            @Param("reportId") Long reportId,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
