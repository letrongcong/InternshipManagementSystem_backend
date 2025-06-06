package com.project.internship_backend.repositories;

import com.project.internship_backend.entities.Mentor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MentorRepository extends JpaRepository<Mentor, Long> {

    @Query("SELECT m FROM Mentor m " +
            "JOIN m.company c " +
            "WHERE (:keyword IS NULL OR " +
            "LOWER(m.user.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(m.position) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.companyName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Mentor> findAllMentors(@Param("keyword") String keyword, Pageable pageable);

    // Tìm Student theo userId
    Optional<Mentor> findByUserId(Long userId);
}
