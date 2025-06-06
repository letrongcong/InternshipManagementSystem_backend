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
public class LecturerDTO {
    @JsonProperty("academic_degree")
    @NotNull(message = "Academic degree is required")
    @Size(min = 1, max = 50, message = "Academic degree be between 1 and 100 characters")
    private String academicDegree;

    @JsonProperty("academic_title")
    @NotNull(message = "academic_title is required")
    @Size(min = 1, max = 50, message = "Academic title be between 1 and 100 characters")
    private String academicTitle;

    @JsonProperty("job_title")
    @NotNull(message = "Job title is required")
    @Size(min = 1, max = 50, message = "Job title be between 1 and 100 characters")
    private String jobTitle;

    @JsonProperty("department")
    @NotNull(message = "Department is required")
    @Size(min = 1, max = 50, message = "Department be between 1 and 50 characters")
    private String department;

    @JsonProperty("user_id")
    private Long userId;
}
