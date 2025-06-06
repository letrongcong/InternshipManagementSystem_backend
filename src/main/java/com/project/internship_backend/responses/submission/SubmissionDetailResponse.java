package com.project.internship_backend.responses.submission;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.internship_backend.entities.Grading;
import com.project.internship_backend.entities.Submission;
import com.project.internship_backend.responses.BaseResponse;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionDetailResponse extends BaseResponse {
    private Long id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("due_date")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime dueDate;

    @JsonProperty("lecturer_id")
    private Long lecturerId;

    @JsonProperty("lecturer_name")
    private String lecturerName;

    @JsonProperty("report_id")
    private Long reportId;

    @JsonProperty("student_code")
    private Long studentCode;

    @JsonProperty("student_name")
    private String studentName;

    @JsonProperty("student_class")
    private String studentClass;

    private String file;

    @JsonProperty("submission_date")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime submissionDate;

    private String note;

    @JsonProperty("status_submission")
    private String statusSubmission;

    @JsonProperty("grading_id")
    private Long gradingId;

    private Float grade;

    private String feedback;

    public static SubmissionDetailResponse fromSubmissionDetailResponse(Submission submission) {
        Grading grading = submission.getGrading();
        SubmissionDetailResponse submissionDetailResponse = SubmissionDetailResponse.builder()
                .id(submission.getId())
                .title(submission.getReport().getTitle())
                .description(submission.getReport().getDescription())
                .dueDate(submission.getReport().getDueDate())
                .lecturerId(submission.getReport().getLecturer().getId())
                .lecturerName(submission.getReport().getLecturer().getUser().getFullName())
                .reportId(submission.getReport().getId())
                .studentCode(submission.getStudent().getStudentCode())
                .studentName(submission.getStudent().getUser().getFullName())
                .studentClass(submission.getStudent().getClassStudent())
                .file(submission.getFile())
                .submissionDate(submission.getSubmissionDate())
                .note(submission.getNote())
                .statusSubmission(submission.getStatus())
                .gradingId(grading != null ? grading.getId() : null)
                .grade(grading != null ? grading.getGrade() : null)
                .feedback(grading != null ? grading.getFeedback() : null)
                .build();
        submissionDetailResponse.setCreatedAt(submission.getCreatedAt());
        return submissionDetailResponse;
    }
}
