package com.project.internship_backend.responses.report;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.internship_backend.entities.Report;
import com.project.internship_backend.responses.BaseResponse;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportDetailResponse extends BaseResponse {
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

    public static ReportDetailResponse fromReport(Report report) {
        ReportDetailResponse reportDetailResponse = ReportDetailResponse.builder()
                .id(report.getId())
                .title(report.getTitle())
                .description(report.getDescription())
                .dueDate(report.getDueDate())
                .lecturerId(report.getLecturer().getId())
                .lecturerName(report.getLecturer().getUser().getFullName())
                .build();
        reportDetailResponse.setCreatedAt(report.getCreatedAt());
        return reportDetailResponse;
    }
}