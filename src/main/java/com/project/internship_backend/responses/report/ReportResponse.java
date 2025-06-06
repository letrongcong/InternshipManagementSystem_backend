package com.project.internship_backend.responses.report;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.internship_backend.entities.Report;
import com.project.internship_backend.entities.ReportStudent;
import com.project.internship_backend.responses.BaseResponse;
import com.project.internship_backend.responses.student.StudentResponse;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponse extends BaseResponse {
    private Long id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("due_date")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime dueDate;

    @JsonProperty("lecturer_name")
    private String lecturerName;

    @JsonProperty("report_students")
    private List<StudentResponse> reportStudents;

    public static ReportResponse fromReport(Report report) {
        ReportResponse reportResponse = ReportResponse.builder()
                .id(report.getId())
                .title(report.getTitle())
                .description(report.getDescription())
                .dueDate(report.getDueDate())
                .lecturerName(report.getLecturer().getUser().getFullName())
                .reportStudents(
                        report.getReportStudents().stream()
                                .map(ReportStudent::getStudent)
                                .map(StudentResponse::fromStudent)
                                .collect(Collectors.toList())
                )
                .build();
        reportResponse.setCreatedAt(report.getCreatedAt());
        return reportResponse;
    }
}
