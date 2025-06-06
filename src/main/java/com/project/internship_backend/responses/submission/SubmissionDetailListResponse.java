package com.project.internship_backend.responses.submission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionDetailListResponse {
    private List<SubmissionDetailResponse> submissionDetailResponses;
    private int totalPages;
}
