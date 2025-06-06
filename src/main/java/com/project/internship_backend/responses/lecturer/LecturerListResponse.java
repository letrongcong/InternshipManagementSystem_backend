package com.project.internship_backend.responses.lecturer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LecturerListResponse {
    private List<LecturerDetailResponse> lecturers;
    private int totalPages;
}
