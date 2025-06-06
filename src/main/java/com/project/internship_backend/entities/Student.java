package com.project.internship_backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Set;

@Data
@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "students")
public class Student {
    @Id
    @Column(name = "student_code", nullable = false)
    private Long studentCode;

    @Column(name = "class_student", nullable = false)
    private String classStudent;

    @Column(name = "major", nullable = false)
    private String major;

    @Column(name = "year_of_study", nullable = false)
    private Integer yearOfStudy;

    @OneToOne
    @JoinColumn(name = "company_id", referencedColumnName = "id", nullable = false)
    private Company company;

    @OneToOne
    @JoinColumn(name = "mentor_id", referencedColumnName = "id", nullable = false)
    private Mentor mentor;

    @OneToOne
    @JoinColumn(name = "lecturer_id", referencedColumnName = "id", nullable = false)
    private Lecturer lecturer;

    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @Column(name = "end_date", nullable = false)
    private Date endDate;

    @Column(name = "language", nullable = false)
    private String language;

    @Column(name = "position", nullable = false)
    private String position;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnore
    private User user;

    @Column(name = "status", nullable = false)
    private String status;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<LecturerStudent> lecturerStudents;
}

