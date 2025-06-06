package com.project.internship_backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Data
@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "grading")
public class Grading {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "submission_id", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private Submission submission;

    @ManyToOne
    @JoinColumn(name = "lecturer_id", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private Lecturer lecturer;

    @Column(name = "grade")
    private Float grade;

    @Column(name = "feedback")
    private String feedback;
}
