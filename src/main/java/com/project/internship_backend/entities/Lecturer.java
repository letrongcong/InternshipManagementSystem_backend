package com.project.internship_backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Data
@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "lecturers")
public class Lecturer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "academic_degree", nullable = false, length = 50)
    private String academicDegree;

    @Column(name = "academic_title", nullable = false, length = 50)
    private String academicTitle;

    @Column(name = "job_title", nullable = false, length = 50)
    private String jobTitle;

    @Column(name = "department", nullable = false, length = 50)
    private String department;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "lecturer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<LecturerStudent> lecturerStudents;
}
