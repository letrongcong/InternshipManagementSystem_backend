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
public class MentorDTO {
    @JsonProperty("company_id")
    @NotNull(message = "Company ID is required")
    private Long companyId;

    @JsonProperty("position")
    @NotNull(message = "Position is required")
    @Size(min = 1, max = 50, message = "Position must be between 1 and 50 characters")
    private String position;

    @JsonProperty("user_id")
    private Long userId;
}
