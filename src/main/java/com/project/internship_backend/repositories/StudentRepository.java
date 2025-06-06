package com.project.internship_backend.repositories;

import com.project.internship_backend.entities.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("SELECT s FROM Student s WHERE " +
            "(:keyword IS NULL OR s.user.fullName LIKE %:keyword% OR CAST(s.studentCode AS string) LIKE %:keyword%)")
    Page<Student> findAllStudents(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT s FROM Student s WHERE s.lecturer.id = :lecturerId AND " +
            "(:keyword IS NULL OR s.user.fullName LIKE %:keyword% OR CAST(s.studentCode AS string) LIKE %:keyword%)")
    List<Student> findAllStudentsByLecturer(@Param("lecturerId") Long lecturerId,
                                           @Param("keyword") String keyword);

    @Query("SELECT s FROM Student s WHERE s.mentor.id = :mentorId AND " +
            "(:keyword IS NULL OR s.user.fullName LIKE %:keyword% OR CAST(s.studentCode AS string) LIKE %:keyword%)")
    List<Student> findAllStudentsByMentor(@Param("mentorId") Long mentorId,
                                            @Param("keyword") String keyword);

    // Tìm Student theo userId
    Optional<Student> findByUserId(Long userId);

}