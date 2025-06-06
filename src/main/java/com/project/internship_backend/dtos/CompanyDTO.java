package com.project.internship_backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDTO {
    @JsonProperty("company_name")
    @NotNull(message = "Company name is required")
    @Size(min = 1, max = 100, message = "Company name must be between 1 and 100 characters")
    private String companyName;

//    @JsonProperty("logo")
//    @NotNull(message = "Logo is required")
//    @Size(min = 1, max = 300, message = "Logo must be between 1 and 50 characters")
//    private String logo;

    @JsonProperty("address")
    @NotNull(message = "Address is required")
    @Size(min = 1, max = 100, message = "Address must be between 1 and 100 characters")
    private String address;

    @JsonProperty("contact_person")
    @NotNull(message = "Contact person is required")
    @Size(min = 1, max = 100, message = "Contact person must be between 1 and 100 characters")
    private String contactPerson;

    @JsonProperty("email")
    @NotNull(message = "Email is required")
    @Size(min = 1, max = 100, message = "Email must be between 1 and 100 characters")
    private String email;

    @JsonProperty("phone_number")
    @NotNull(message = "Phone number is required")
    @Size(min = 1, max = 15, message = "Phone number must be between 1 and 15 characters")
    private String phoneNumber;
}
