package com.project.internship_backend.responses.lecturer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.internship_backend.entities.Lecturer;
import com.project.internship_backend.entities.User;
import com.project.internship_backend.responses.user.UserResponse;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LecturerDetailResponse {
    private Long id;

    @JsonProperty("academic_degree")
    private String academicDegree;

    @JsonProperty("academic_title")
    private String academicTitle;

    @JsonProperty("job_title")
    private String jobTitle;

    @JsonProperty("department")
    private String department;

    @JsonProperty("user_response")
    private UserResponse userResponse;

    public static LecturerDetailResponse fromLecturerDetail(Lecturer lecturer) {
        User user = lecturer.getUser();
        UserResponse userResponse = UserResponse.fromUser(user);  // Tạo đối tượng UserResponse từ User

        LecturerDetailResponse mentorDetailResponse = LecturerDetailResponse.builder()
                .id(lecturer.getId())
                .academicDegree(lecturer.getAcademicDegree())
                .academicTitle(lecturer.getAcademicTitle())
                .jobTitle(lecturer.getJobTitle())
                .department(lecturer.getDepartment())
                .userResponse(userResponse)
                .build();
        return mentorDetailResponse;
    }
}
