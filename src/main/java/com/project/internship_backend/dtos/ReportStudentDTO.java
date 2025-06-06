package com.project.internship_backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportStudentDTO {
    @JsonProperty("report_id")
    private Long reportId;

    @JsonProperty("student_code")
    private Long studentCode;
}
