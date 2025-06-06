package com.project.internship_backend.responses.company;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyListResponse {
    private List<CompanyResponse> companies;
    private int totalPages;
}
