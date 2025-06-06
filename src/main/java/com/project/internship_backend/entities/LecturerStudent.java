package com.project.internship_backend.entities;
import jakarta.persistence.*;
import lombok.*;

@Data
@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "lecturer_student")
public class LecturerStudent {
    @EmbeddedId
    private LecturerStudentId id; // Sử dụng EmbeddedId cho khóa chính phức hợp

    @ManyToOne
    @MapsId("lecturerId") // Ánh xạ với phần lecturerId trong EmbeddedId
    @JoinColumn(name = "lecturer_id", nullable = false)
    private Lecturer lecturer;

    @ManyToOne
    @MapsId("studentCode") // Ánh xạ với phần studentCode trong EmbeddedId
    @JoinColumn(name = "student_code", nullable = false)
    private Student student;
}
