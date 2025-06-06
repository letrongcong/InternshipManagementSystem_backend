package com.project.internship_backend.responses.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.internship_backend.entities.BaseEntity;
import com.project.internship_backend.responses.BaseResponse;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse extends BaseResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("profile_image")
    private String profileImage;

    private String email;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("date_of_birth")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dateOfBirth;

    @JsonProperty("desired_role")
    private String desiredRole;

    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("role")
    private com.project.internship_backend.entities.Role role;
    public static UserResponse fromUser(com.project.internship_backend.entities.User user) {
         UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                 .profileImage(user.getProfileImage())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .desiredRole(user.getDesiredRole())
                .isActive(user.isActive())
                .role(user.getRole())
                .build();
        userResponse.setCreatedAt(user.getCreatedAt());
        return userResponse;
    }
}
