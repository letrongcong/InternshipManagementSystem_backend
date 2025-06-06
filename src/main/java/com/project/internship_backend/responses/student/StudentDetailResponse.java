package com.project.internship_backend.responses.student;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.internship_backend.entities.Student;
import com.project.internship_backend.entities.User;
import com.project.internship_backend.responses.user.UserResponse;
import lombok.*;

import java.util.Date;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentDetailResponse {
    @JsonProperty("student_code")
    private Long studentCode;

    @JsonProperty("student_name")
    private String studentName;

    @JsonProperty("class_student")
    private String classStudent;

    private String major;

    @JsonProperty("year_of_study")
    private Integer yearOfStudy;

    @JsonProperty("company_name")
    private String companyName;

    @JsonProperty("company_id")
    private Long companyId;

    @JsonProperty("mentor_name")
    private String mentorName;
    @JsonProperty("mentor_id")
    private Long mentorId;

    @JsonProperty("lecturer_name")
    private String lecturerName;

    @JsonProperty("lecturer_id")
    private Long lecturerId;

    @JsonProperty("start_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date startDate;

    @JsonProperty("end_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date endDate;

    @JsonProperty("language")
    private String language;

    @JsonProperty("position")
    private String position;

    @JsonProperty("user_response")
    private UserResponse userResponse;

    @JsonProperty("student_status")
    private String studentStatus;

    public static StudentDetailResponse fromStudentDetail(Student student) {
        User user = student.getUser();
        UserResponse userResponse = UserResponse.fromUser(user);  // Tạo đối tượng UserResponse từ User

        StudentDetailResponse studentDetailResponse = StudentDetailResponse.builder()
                .studentCode(student.getStudentCode())
                .studentName(student.getUser().getFullName())
                .classStudent(student.getClassStudent())
                .major(student.getMajor())
                .yearOfStudy(student.getYearOfStudy())
                .companyName(student.getCompany().getCompanyName())
                .companyId(student.getCompany().getId())
                .mentorName(student.getMentor().getUser().getFullName())
                .mentorId(student.getMentor().getId())
                .lecturerName(student.getLecturer().getUser().getFullName())
                .lecturerId(student.getLecturer().getId())
                .startDate(student.getStartDate())
                .endDate(student.getEndDate())
                .language(student.getLanguage())
                .position(student.getPosition())
                .studentStatus(student.getStatus())
                .userResponse(userResponse)  // Thêm đối tượng UserResponse vào StudentResponse
                .build();
        return studentDetailResponse;
    }
}
