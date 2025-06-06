package com.project.internship_backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateUserDTO {
    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("profile_image")
    private String profileImage;

    @NotNull(message = "Email is required")
    private String email;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @NotNull(message = "Gender is required")
    private String gender;

    @JsonProperty("date_of_birth")
    private Date dateOfBirth;

    @JsonProperty("desired_role")
    private String desiredRole;

    private String password;

    private String retypePassword;
}
