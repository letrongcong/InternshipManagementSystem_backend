package com.project.internship_backend.responses.lecturer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.internship_backend.entities.Lecturer;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LecturerResponse {
    private Long id;

    @JsonProperty("academic_degree")
    private String academicDegree;

    @JsonProperty("academic_title")
    private String academicTitle;

    @JsonProperty("job_title")
    private String jobTitle;

    @JsonProperty("department")
    private String department;

    @JsonProperty("user_id")
    private Long userId;

    public static LecturerResponse fromLecturer(Lecturer lecturer) {
        return LecturerResponse.builder()
                .id(lecturer.getId())
                .academicDegree(lecturer.getAcademicDegree())
                .academicTitle(lecturer.getAcademicTitle())
                .jobTitle(lecturer.getJobTitle())
                .department(lecturer.getDepartment())
                .userId(lecturer.getUser().getId())
                .build();
    }
}
