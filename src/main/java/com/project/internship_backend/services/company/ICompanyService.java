package com.project.internship_backend.services.company;

import com.project.internship_backend.dtos.CompanyDTO;
import com.project.internship_backend.entities.Company;
import com.project.internship_backend.responses.company.CompanyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ICompanyService {
    Company createCompany(CompanyDTO companyDTO);

    Company getCompanyById(Long companyId);

    Page<CompanyResponse> getAllCompanies(String keyword, PageRequest pageRequest);

    Company updateCompany(Long companyId, CompanyDTO companyDTO);

    void deleteCompany(Long companyId);

    void changeLogoImage(Long companyId, String imageName) throws Exception;
}
