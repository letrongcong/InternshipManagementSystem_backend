package com.project.internship_backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentDTO {
    @JsonProperty("student_code")
    @NotNull(message = "Student code is required")
    private Long studentCode;

    @JsonProperty("class_student")
    @NotNull(message = "Class student is required")
    private String classStudent;

    @JsonProperty("major")
    @NotNull(message = "Major is required")
    private String major;

    @JsonProperty("year_of_study")
    @NotNull(message = "Year of study is required")
    @Min(value = 1, message = "Year of study must be greater than or equal to 1")
    private Integer yearOfStudy;

    @JsonProperty("company_id")
    @NotNull(message = "Company is required")
    @Min(value = 1, message = "Company must be greater than or equal to 1")
    private Long companyId;

    @JsonProperty("mentor_id")
    @NotNull(message = "Mentor is required")
    @Min(value = 1, message = "Mentor must be greater than or equal to 1")
    private Long mentorId;

    @JsonProperty("lecturer_id")
    @NotNull(message = "Lecturer is required")
    @Min(value = 1, message = "Lecturer must be greater than or equal to 1")
    private Long lecturerId;

    @JsonProperty("start_date")
    @NotNull(message = "Start date is required")
    private Date startDate;

    @JsonProperty("end_date")
    @NotNull(message = "End date is required")
    private Date endDate;

    @JsonProperty("language")
    @NotNull(message = "Language is required")
    private String language;

    @JsonProperty("position")
    @NotNull(message = "Position is required")
    private String position;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("status")
    @NotNull(message = "Status is required")
    private String status;
}
