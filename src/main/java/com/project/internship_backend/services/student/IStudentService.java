package com.project.internship_backend.services.student;

import com.project.internship_backend.dtos.StudentDTO;
import com.project.internship_backend.entities.Student;
import com.project.internship_backend.exceptions.DataNotFoundException;
import com.project.internship_backend.responses.student.LecturerOfStudentResponse;
import com.project.internship_backend.responses.student.MentorOfStudentResponse;
import com.project.internship_backend.responses.student.StudentDetailResponse;
import com.project.internship_backend.responses.student.StudentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface IStudentService {
    Student createStudent(StudentDTO studentDTO) throws DataNotFoundException;
    StudentDetailResponse getStudentByCode(Long studentCode) throws DataNotFoundException;
    Page<StudentResponse> getAllStudents(String keyword, PageRequest pageRequest);
    List<StudentDetailResponse> getStudentsByLecturer(Long lecturerId, String keyword);
    List<StudentDetailResponse> getStudentsByMentor(Long mentorId, String keyword);
    Student updateStudent(Long StudentId, StudentDTO studentDTO) throws DataNotFoundException;
    Student updateUserId(Long userId, StudentDTO studentDTO) throws DataNotFoundException;
    void deleteStudent(Long studentId);
    MentorOfStudentResponse getMentorOfStudent(Long studentCode) throws DataNotFoundException;
    LecturerOfStudentResponse getLecturerOfStudent(Long studentCode) throws DataNotFoundException;

}
