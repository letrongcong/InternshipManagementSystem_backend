package com.project.internship_backend.services.lecturer;

import com.project.internship_backend.dtos.LecturerDTO;
import com.project.internship_backend.entities.Lecturer;
import com.project.internship_backend.entities.User;
import com.project.internship_backend.exceptions.DataNotFoundException;
import com.project.internship_backend.repositories.LecturerRepository;
import com.project.internship_backend.repositories.UserRepository;
import com.project.internship_backend.responses.lecturer.LecturerDetailResponse;
import com.project.internship_backend.responses.lecturer.LecturerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LecturerService implements ILecturerService {
    private final LecturerRepository lecturerRepository;
    private final UserRepository userRepository;

    @Override
    public Lecturer createLecturer(LecturerDTO lecturerDTO) throws DataNotFoundException{
        //Kiểm tra xem user id có tồn tại hay không
        User existingUser = userRepository.findById(lecturerDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException(
                        "Cannot find user with id : "+ lecturerDTO.getUserId()));
        Lecturer newLecturer = Lecturer.builder()
                .academicDegree(lecturerDTO.getAcademicDegree())
                .academicTitle(lecturerDTO.getAcademicTitle())
                .jobTitle(lecturerDTO.getJobTitle())
                .department(lecturerDTO.getDepartment())
                .user(existingUser)
                .build();

        return lecturerRepository.save(newLecturer);
    }

    @Override
    public Page<LecturerDetailResponse> getAllLecturers(String keyword, PageRequest pageRequest) {
        Page<Lecturer> lecturersPage;
        lecturersPage = lecturerRepository.findAllLecturers(keyword, pageRequest);
        return lecturersPage.map(LecturerDetailResponse::fromLecturerDetail);
    }

    @Override
    public LecturerDetailResponse getLecturerById(Long lecturerId) throws DataNotFoundException{
        Lecturer lecturer = lecturerRepository.findById(lecturerId).orElseThrow(
                () -> new DataNotFoundException("Lecturer not found")
        );
        return LecturerDetailResponse.fromLecturerDetail(lecturer);
    }

    @Override
    public LecturerDetailResponse getLecturerByToken(Long userId) throws DataNotFoundException {
        Lecturer existingLecturer = lecturerRepository.findByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find mentor with id: "+userId));
        return LecturerDetailResponse.fromLecturerDetail(existingLecturer);
    }

    @Override
    public Lecturer updateLecturer(Long lecturerId, LecturerDTO lecturerDTO) throws DataNotFoundException{
        Lecturer existingLecturer = lecturerRepository.findById(lecturerId).orElseThrow(
                () -> new DataNotFoundException("lecturer not found"));
        existingLecturer.setAcademicDegree(lecturerDTO.getAcademicDegree());
        existingLecturer.setAcademicTitle(lecturerDTO.getAcademicTitle());
        existingLecturer.setJobTitle(lecturerDTO.getJobTitle());
        existingLecturer.setDepartment(lecturerDTO.getDepartment());
        return lecturerRepository.save(existingLecturer);
    }

    @Override
    public void deleteLecturer(Long lecturerId) {
        lecturerRepository.deleteById(lecturerId);
    }
}
