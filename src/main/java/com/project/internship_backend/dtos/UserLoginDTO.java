package com.project.internship_backend.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO {
    @NotNull(message = "email is required")
    private String email;

    @NotNull(message = "Password cannot be blank")
    private String password;
}
