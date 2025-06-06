package com.project.internship_backend.repositories;

import com.project.internship_backend.entities.Lecturer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LecturerRepository extends JpaRepository<Lecturer, Long> {
    @Query("SELECT l FROM Lecturer l WHERE " +
            "(:keyword IS NULL OR l.user.fullName LIKE %:keyword% OR l.department LIKE %:keyword%)")
    Page<Lecturer> findAllLecturers(@Param("keyword") String keyword, Pageable pageable);

    Optional<Lecturer> findByUserId(Long userId);

    @Query("SELECT l FROM Lecturer l JOIN l.lecturerStudents ls JOIN ls.student s WHERE s.studentCode = :studentCode")
    Optional<Lecturer> findLecturerByStudentCode(@Param("studentCode") Long studentCode);
}
