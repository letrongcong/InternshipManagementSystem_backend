package com.project.internship_backend.responses.report;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportListResponse {
    private List<ReportResponse> reports;
    private int totalPages;
}
