package com.project.internship_backend.repositories;

import com.project.internship_backend.entities.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    @Query("SELECT c FROM Company c WHERE " +
            "(:keyword IS NULL OR c.companyName LIKE %:keyword% OR c.phoneNumber LIKE %:keyword% OR" +
            " c.email LIKE %:keyword% OR c.address LIKE %:keyword% OR c.contactPerson LIKE %:keyword%)")
    Page<Company> findAllCompanies(@Param("keyword") String keyword, Pageable pageable);
}
