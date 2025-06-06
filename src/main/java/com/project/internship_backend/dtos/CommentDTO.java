package com.project.internship_backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    @JsonProperty("submission_id")
    private Long submissionId;

    @JsonProperty("user_id")
    private Long userId;

    private String content;
}

