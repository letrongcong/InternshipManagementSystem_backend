package com.project.internship_backend.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    @JsonProperty("full_name")
    @NotNull(message = "Full name is required")
    @Size(min = 1, max = 100, message = "Full name must be between 1 and 100 characters")
    private String fullName;

    @JsonProperty("email")
    @NotNull(message = "Email is required")
    @Size(min = 1, max = 100, message = "Email must be between 1 and 100 characters")
    private String email;

    @JsonProperty("phone_number")
    @NotNull(message = "Phone number is required")
    @Size(min = 1, max = 15, message = "Phone number must be between 1 and 15 characters")
    private String phoneNumber;

    @JsonProperty("gender")
    @NotNull(message = "Gender is required")
    @Size(min = 1, max = 10, message = "Gender must be between 1 and 10 characters")
    private String gender;

    @JsonProperty("date_of_birth")
    private Date dateOfBirth;

    @JsonProperty("desired_role")
    @NotNull(message = "Desired role is required")
    @Size(min = 1, max = 50, message = "Desired role must be between 1 and 50 characters")
    private String desiredRole;

    @JsonProperty("password")
    @NotNull(message = "Password is required")
    @Size(min = 1, max = 100, message = "Password must be between 1 and 100 characters")
    private String password;

    @JsonProperty("retype_password")
    @NotNull(message = "Retype password is required")
    @Size(min = 1, max = 100, message = "Retype password must be between 1 and 100 characters")
    private String retypePassword;

    @JsonProperty("role_id")
    private Long roleId;
}
