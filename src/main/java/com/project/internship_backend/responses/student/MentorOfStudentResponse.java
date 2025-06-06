package com.project.internship_backend.responses.student;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.internship_backend.entities.Mentor;
import com.project.internship_backend.entities.Student;
import lombok.*;

import java.util.Date;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MentorOfStudentResponse {
    @JsonProperty("student_code")
    private Long studentCode;

    @JsonProperty("mentor_name")
    private String mentorName;

    @JsonProperty("company_name")
    private String companyName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("date_of_birth")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dateOfBirth;

    @JsonProperty("position")
    private String position;

    public static MentorOfStudentResponse fromMentorOfStudentResponse(Student student) {
        Mentor mentor = student.getMentor();
        String mentorEmail = null;
        String mentorPhoneNumber = null;
        String mentorGender = null;
        Date mentorDateOfBirth = null;
        // Kiểm tra xem mentor có tồn tại không và lấy email + phone number nếu có
        if (mentor != null && mentor.getUser() != null) {
            mentorEmail = mentor.getUser().getEmail();
            mentorPhoneNumber = mentor.getUser().getPhoneNumber();
            mentorGender = mentor.getUser().getGender();
            mentorDateOfBirth = mentor.getUser().getDateOfBirth();
        }

        MentorOfStudentResponse mentorOfStudentResponse = MentorOfStudentResponse.builder()
                .studentCode(student.getStudentCode())
                .mentorName(student.getMentor().getUser().getFullName())
                .companyName(student.getMentor().getCompany().getCompanyName())
                .position(student.getMentor().getPosition())
                .email(mentorEmail)
                .phoneNumber(mentorPhoneNumber)
                .gender(mentorGender)
                .dateOfBirth(mentorDateOfBirth)
                .build();
        return mentorOfStudentResponse;
    }
}
