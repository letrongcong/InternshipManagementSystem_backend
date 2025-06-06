package com.project.internship_backend.services.lecturer;

import com.project.internship_backend.dtos.LecturerDTO;
import com.project.internship_backend.entities.Lecturer;
import com.project.internship_backend.exceptions.DataNotFoundException;
import com.project.internship_backend.responses.lecturer.LecturerDetailResponse;
import com.project.internship_backend.responses.lecturer.LecturerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ILecturerService {
    Lecturer createLecturer(LecturerDTO lecturerDTO) throws DataNotFoundException;

    Page<LecturerDetailResponse> getAllLecturers(String keyword, PageRequest pageRequest);
    LecturerDetailResponse getLecturerById(Long lecturerId) throws DataNotFoundException;
    LecturerDetailResponse getLecturerByToken(Long lecturerId) throws DataNotFoundException;

    Lecturer updateLecturer(Long lecturerId, LecturerDTO lecturerDTO) throws DataNotFoundException;

    void deleteLecturer(Long lecturerId);
}
