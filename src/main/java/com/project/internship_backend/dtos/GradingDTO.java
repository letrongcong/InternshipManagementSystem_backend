package com.project.internship_backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GradingDTO {
    @JsonProperty("grade")
    private Float grade;

    @JsonProperty("feedback")
    private String feedback;
}
