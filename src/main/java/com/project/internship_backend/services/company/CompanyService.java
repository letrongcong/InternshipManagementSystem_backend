package com.project.internship_backend.services.company;

import com.project.internship_backend.dtos.CompanyDTO;
import com.project.internship_backend.entities.Company;
import com.project.internship_backend.entities.User;
import com.project.internship_backend.exceptions.DataNotFoundException;
import com.project.internship_backend.repositories.CompanyRepository;
import com.project.internship_backend.responses.company.CompanyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompanyService implements ICompanyService {
    private final CompanyRepository companyRepository;
    @Override
    public Company createCompany(CompanyDTO companyDTO) {
       Company newCompany = Company.builder()
               .companyName(companyDTO.getCompanyName())
               .address(companyDTO.getAddress())
               .contactPerson(companyDTO.getContactPerson())
               .phoneNumber(companyDTO.getPhoneNumber())
               .email(companyDTO.getEmail())
               .build();
       return companyRepository.save(newCompany);
    }

    @Override
    public Company getCompanyById(Long companyId) {
        return companyRepository.findById(companyId).orElseThrow(
                () -> new RuntimeException("Company not found")
        );
    }

    @Override
    public Page<CompanyResponse> getAllCompanies(String keyword, PageRequest pageRequest) {
        Page<Company> companiesPage;
        companiesPage = companyRepository.findAllCompanies(keyword, pageRequest);
        return companiesPage.map(CompanyResponse::fromCompany);
    }

    @Override
    public Company updateCompany(Long companyId, CompanyDTO companyDTO) {
        Company existingCompany = getCompanyById(companyId);
        if(existingCompany != null) {
            if(companyDTO.getCompanyName() != null &&
                !companyDTO.getCompanyName().isEmpty()){
                existingCompany.setCompanyName(companyDTO.getCompanyName());
            }
            if (companyDTO.getAddress() != null &&
                    !companyDTO.getAddress().isEmpty()) {
                existingCompany.setAddress(companyDTO.getAddress());
            }
            if(companyDTO.getContactPerson() != null &&
                    !companyDTO.getContactPerson().isEmpty()) {
                existingCompany.setContactPerson(companyDTO.getContactPerson());
            }
            if(companyDTO.getPhoneNumber() != null &&
                    !companyDTO.getPhoneNumber().isEmpty()) {
                existingCompany.setPhoneNumber(companyDTO.getPhoneNumber());
            }
            if(companyDTO.getEmail() != null &&
                    !companyDTO.getEmail().isEmpty()) {
                existingCompany.setEmail(companyDTO.getEmail());
            }
            companyRepository.save(existingCompany);
        }
        return null;
    }

    @Override
    public void deleteCompany(Long companyId) {
        companyRepository.deleteById(companyId);
    }

    @Override
    @Transactional
    public void changeLogoImage(Long companyId, String imageName) throws Exception {
        Company existingCompany = companyRepository.findById(companyId)
                .orElseThrow(() -> new DataNotFoundException("Company not found"));
        existingCompany.setLogo(imageName);
        companyRepository.save(existingCompany);
    }
}
