package com.project.internship_backend.responses.student;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.internship_backend.entities.Lecturer;
import com.project.internship_backend.entities.Student;
import lombok.*;

import java.util.Date;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LecturerOfStudentResponse {
    @JsonProperty("student_code")
    private Long studentCode;

    @JsonProperty("lecturer_name")
    private String lecturerName;

    @JsonProperty("department")
    private String department;

    @JsonProperty("email")
    private String email;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("date_of_birth")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dateOfBirth;

    public static LecturerOfStudentResponse fromLecturerOfStudentResponse(Student student) {
        Lecturer lecturer = student.getLecturer();
        String lecturerEmail = null;
        String lecturerPhoneNumber = null;
        String lecturerGender = null;
        Date lecturerDateOfBirth = null;
        // Kiểm tra xem mentor có tồn tại không và lấy email + phone number nếu có
        if (lecturer != null && lecturer.getUser() != null) {
            lecturerEmail = lecturer.getUser().getEmail();
            lecturerPhoneNumber = lecturer.getUser().getPhoneNumber();
            lecturerGender = lecturer.getUser().getGender();
            lecturerDateOfBirth = lecturer.getUser().getDateOfBirth();
        }

        LecturerOfStudentResponse lecturerOfStudentResponse = LecturerOfStudentResponse.builder()
                .studentCode(student.getStudentCode())
                .lecturerName(student.getLecturer().getUser().getFullName())
                .department(student.getLecturer().getDepartment())
                .email(lecturerEmail)
                .phoneNumber(lecturerPhoneNumber)
                .gender(lecturerGender)
                .dateOfBirth(lecturerDateOfBirth)
                .build();
        return lecturerOfStudentResponse;
    }
}
