package com.project.internship_backend.responses.mentor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorListResponse {
    private List<MentorDetailResponse> mentors;
    private int totalPages;
}



