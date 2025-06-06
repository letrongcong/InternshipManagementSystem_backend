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
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    public static String ADMIN = "ADMIN";
    public static String STUDENT = "STUDENT";
    public static String LECTURER = "LECTURER";
    public static String MENTOR = "MENTOR";
}