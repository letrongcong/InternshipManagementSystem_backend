package com.project.internship_backend.responses.company;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.internship_backend.entities.Company;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyResponse {

    private Long id;

    @JsonProperty("company_name")
    private String companyName;

    private String logo;

    private String address;

    @JsonProperty("contact_person")
    private String contactPerson;

    private String email;

    @JsonProperty("phone_number")
    private String phoneNumber;

    public static CompanyResponse fromCompany(Company company) {
        return CompanyResponse.builder()
                .id(company.getId())
                .companyName(company.getCompanyName())
                .logo(company.getLogo())
                .address(company.getAddress())
                .contactPerson(company.getContactPerson())
                .email(company.getEmail())
                .phoneNumber(company.getPhoneNumber())
                .build();
    }
}
