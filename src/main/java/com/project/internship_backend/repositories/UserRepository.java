package com.project.internship_backend.repositories;

import com.project.internship_backend.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    @Query("SELECT u FROM User u WHERE " +
            "(:keyword IS NULL OR u.fullName LIKE %:keyword% OR u.fullName LIKE %:keyword% " +
            "OR u.phoneNumber LIKE %:keyword% OR u.email LIKE %:keyword%)")
    Page<User> findAllUsers(@Param("keyword") String keyword, Pageable pageable);
}
