package com.project.internship_backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportDTO {
    @NotNull(message = "Title is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;

    private String description;

    @JsonProperty("due_date")
    @NotNull(message = "Due date is required")
    private LocalDateTime dueDate;

    @JsonProperty("create_at")
    @NotNull(message = "Create at is required")
    private LocalDateTime createAt;

    @JsonProperty("lecturer_id")
    @NotNull(message = "Lecturer id is required")
    private Integer lecturerId;

    @NotNull(message = "List student is required")
    @JsonProperty("list_students")
    private List<Long> studentCodes;

}
