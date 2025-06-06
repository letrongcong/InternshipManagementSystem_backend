package com.project.internship_backend.responses.submission;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class SubmissionResponse extends BaseResponse {
    private Long id;

    @JsonProperty("report_id")
    private Long reportId;

    @JsonProperty("report_name")
    private String reportName;

    @JsonProperty("student_code")
    private Long studentCode;

    @JsonProperty("student_class")
    private String studentClass;

    @JsonProperty("student_name")
    private String studentName;

    private String file;

    private String note;

    @JsonProperty("submission_date")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime submissionDate;

    @JsonProperty("status_submission")
    private String statusSubmission;

    public static SubmissionResponse fromSubmission(Submission submission) {
        SubmissionResponse submissionResponse = SubmissionResponse.builder()
                .id(submission.getId())
                .reportId(submission.getReport().getId())
                .reportName(submission.getReport().getTitle())
                .studentCode(submission.getStudent().getStudentCode())
                .studentName(submission.getStudent().getUser().getFullName())
                .studentClass(submission.getStudent().getClassStudent())
                .file(submission.getFile())
                .note(submission.getNote())
                .submissionDate(submission.getSubmissionDate())
                .statusSubmission(submission.getStatus())
                .build();
        submissionResponse.setCreatedAt(submission.getCreatedAt());
        return submissionResponse;
    }
}
