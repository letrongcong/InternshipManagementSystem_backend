package com.project.internship_backend.entities;

import java.io.Serializable;
import java.util.Objects;

public class LecturerStudentId implements Serializable {
    private Long lecturerId;
    private Long studentCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LecturerStudentId that = (LecturerStudentId) o;
        return Objects.equals(lecturerId, that.lecturerId) &&
                Objects.equals(studentCode, that.studentCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lecturerId, studentCode);
    }
}
