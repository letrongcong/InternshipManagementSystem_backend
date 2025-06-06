package com.project.internship_backend.responses.report;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportDetailListResponse {
    private List<ReportDetailResponse> reportByStudentResponses;
    private int totalPages;
}
