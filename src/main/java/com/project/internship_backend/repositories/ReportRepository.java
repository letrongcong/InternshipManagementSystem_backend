package com.project.internship_backend.repositories;

import com.project.internship_backend.entities.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReportRepository extends JpaRepository<Report, Long> {
    @Query("SELECT r FROM Report r WHERE " +
            "(:keyword IS NULL OR r.title LIKE %:keyword%)")
    Page<Report> findAllReports(@Param("keyword") String keyword, Pageable pageable);


    @Query("SELECT r FROM Report r " +
            "WHERE r.id IN (SELECT rs.report.id FROM ReportStudent rs WHERE rs.student.studentCode = :studentCode) " +
            "OR r.lecturer.id IN (SELECT ls.lecturer.id FROM LecturerStudent ls WHERE ls.student.studentCode = :studentCode)")
    Page<Report> findReportsByStudentCode(@Param("keyword") String keyword,
                                          Pageable pageable,
                                          @Param("studentCode") Long studentCode);

    @Query("SELECT s FROM Report s WHERE s.lecturer.id = :lecturerId AND " +
            "(:keyword IS NULL OR s.title LIKE %:keyword%)")
    Page<Report> findReportsByLecturer(@Param("keyword") String keyword,
                                       Pageable pageable,
                                       @Param("lecturerId") Long lecturerId);
//    @Query("""
//    SELECT r
//    FROM ReportStudent rs
//    JOIN rs.report r
//    LEFT JOIN Submission s
//        ON s.report.id = rs.report.id AND s.student.studentCode = rs.studentCode
//    WHERE rs.studentCode = :studentCode
//      AND s.id IS NULL
//""")
//    Page<Report> findReportsNotSubmittedByStudent(Pageable pageable, @Param("studentCode") Long studentCode);

}
