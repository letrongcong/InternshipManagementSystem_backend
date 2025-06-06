package com.project.internship_backend.responses.student;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.internship_backend.entities.Student;
import lombok.*;

import java.util.Date;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentResponse {
    @JsonProperty("student_code")
    private Long studentCode;

    @JsonProperty("student_name")
    private String studentName;

    @JsonProperty("class_student")
    private String classStudent;

    private String major;

    @JsonProperty("year_of_study")
    private Integer yearOfStudy;

    @JsonProperty("company_id")
    private Long companyId;

    @JsonProperty("company_name")
    private String companyName;

    @JsonProperty("mentor_id")
    private Long mentorId;

    @JsonProperty("mentor_name")
    private String mentorName;

    @JsonProperty("lecturer_id")
    private Long lecturerId;

    @JsonProperty("lecturer_name")
    private String lecturerName;

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

    @JsonProperty("student_status")
    private String studentStatus;

    @JsonProperty("user_id")
    private Long userId;

    public static StudentResponse fromStudent(Student student) {
        StudentResponse studentResponse = StudentResponse.builder()
                .studentCode(student.getStudentCode())
                .studentName(student.getUser().getFullName())
                .classStudent(student.getClassStudent())
                .major(student.getMajor())
                .yearOfStudy(student.getYearOfStudy())
                .companyId(student.getCompany().getId())
                .companyName(student.getCompany().getCompanyName())
                .mentorId(student.getMentor().getId())
                .mentorName(student.getMentor().getUser().getFullName())
                .lecturerId(student.getLecturer().getId())
                .lecturerName(student.getLecturer().getUser().getFullName())
                .startDate(student.getStartDate())
                .endDate(student.getEndDate())
                .language(student.getLanguage())
                .position(student.getPosition())
                .studentStatus(student.getStatus())
                .userId(student.getUser().getId())
                .build();
        return studentResponse;
    }
}
