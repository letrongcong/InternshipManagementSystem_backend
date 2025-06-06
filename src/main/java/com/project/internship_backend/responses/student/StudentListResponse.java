package com.project.internship_backend.responses.student;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentListResponse {
    private List<StudentResponse> students;
    private int totalPages;
}
