package com.project.internship_backend.services.student;

import com.project.internship_backend.dtos.StudentDTO;
import com.project.internship_backend.entities.*;
import com.project.internship_backend.exceptions.DataNotFoundException;
import com.project.internship_backend.repositories.*;
import com.project.internship_backend.responses.student.LecturerOfStudentResponse;
import com.project.internship_backend.responses.student.MentorOfStudentResponse;
import com.project.internship_backend.responses.student.StudentDetailResponse;
import com.project.internship_backend.responses.student.StudentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService implements IStudentService{
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final LecturerRepository lecturerRepository;
    private final CompanyRepository companyRepository;
    private final MentorRepository mentorRepository;
    @Override
    public Student createStudent(StudentDTO studentDTO) throws DataNotFoundException{
        //Kiểm tra xem user id có tồn tại hay không
        User existingUser = userRepository.findById(studentDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException(
                        "Cannot find user with id : "+studentDTO.getUserId()));
        //Kiểm tra xem company id có tồn tại hay không
        Company existingCompany = companyRepository.findById(studentDTO.getCompanyId())
                .orElseThrow(() -> new DataNotFoundException(
                        "Cannot find company with id : "+studentDTO.getCompanyId()));
        //Kiểm tra xem supervisor id có tồn tại hay không
        Lecturer existingLecturer = lecturerRepository.findById(studentDTO.getLecturerId())
                .orElseThrow(() -> new DataNotFoundException(
                        "Cannot find lecturer with id : "+studentDTO.getLecturerId()));
        //Kiểm tra xem company supervisor id có tồn tại hay không
        Mentor existingMentor = mentorRepository.findById(studentDTO.getMentorId())
                .orElseThrow(() -> new DataNotFoundException(
                        "Cannot find mentor with id : "+studentDTO.getMentorId()));
        Student newStudent = Student.builder()
                .studentCode(studentDTO.getStudentCode())
                .classStudent(studentDTO.getClassStudent())
                .major(studentDTO.getMajor())
                .yearOfStudy(studentDTO.getYearOfStudy())
                .company(existingCompany)
                .mentor(existingMentor)
                .lecturer(existingLecturer)
                .startDate(studentDTO.getStartDate())
                .endDate(studentDTO.getEndDate())
                .language(studentDTO.getLanguage())
                .position(studentDTO.getPosition())
                .user(existingUser)
                .status(studentDTO.getStatus())
                .build();
        return studentRepository.save(newStudent);
    }

    @Override
    public StudentDetailResponse getStudentByCode(Long studentCode) throws DataNotFoundException {
        Student student = studentRepository.findById(studentCode).orElseThrow(
                () -> new DataNotFoundException("Student not found")
        );
        return StudentDetailResponse.fromStudentDetail(student);
    }

    @Override
    public Page<StudentResponse> getAllStudents(String keyword, PageRequest pageRequest) {
        if (keyword == null || keyword.trim().isEmpty()) {
            keyword = null; // Không có từ khóa => hiển thị toàn bộ.
        }
        Page<Student> studentsPage;
        studentsPage = studentRepository.findAllStudents(keyword, pageRequest);
        return studentsPage.map(StudentResponse::fromStudent);
    }

    @Override
    public List<StudentDetailResponse> getStudentsByLecturer(Long lecturerId, String keyword) {
        List<Student> students = studentRepository.findAllStudentsByLecturer(lecturerId, keyword);
        return students.stream()
                .map(StudentDetailResponse::fromStudentDetail)
                .toList();
    }

    @Override
    public List<StudentDetailResponse> getStudentsByMentor(Long mentorId, String keyword) {
        List<Student> students = studentRepository.findAllStudentsByMentor(mentorId, keyword);
        return students.stream()
                .map(StudentDetailResponse::fromStudentDetail)
                .toList();
    }

    @Override
    public Student updateStudent(Long studentId, StudentDTO studentDTO) throws DataNotFoundException{
        //Tìm xem student có tồn tại hay không
        Student existingStudent = studentRepository.findById(studentId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find student with id: "+studentId));
        //Kiểm tra xem company id có tồn tại hay không
        Company existingCompany = companyRepository.findById(studentDTO.getCompanyId())
                .orElseThrow(() -> new DataNotFoundException(
                        "Cannot find user with id : "+studentDTO.getCompanyId()));
        //Kiểm tra xem supervisor id có tồn tại hay không
        Lecturer existingLecturer = lecturerRepository.findById(studentDTO.getLecturerId())
                .orElseThrow(() -> new DataNotFoundException(
                        "Cannot find user with id : "+studentDTO.getLecturerId()));
        //Kiểm tra xem company supervisor id có tồn tại hay không
        Mentor existingMentor = mentorRepository.findById(studentDTO.getMentorId())
                .orElseThrow(() -> new DataNotFoundException(
                        "Cannot find user with id : "+studentDTO.getMentorId()));
            existingStudent.setStudentCode(studentDTO.getStudentCode());
            existingStudent.setClassStudent(studentDTO.getClassStudent());
            existingStudent.setMajor(studentDTO.getMajor());
            existingStudent.setYearOfStudy(studentDTO.getYearOfStudy());
            existingStudent.setCompany(existingCompany);
            existingStudent.setLecturer(existingLecturer);
            existingStudent.setMentor(existingMentor);
            existingStudent.setStartDate(studentDTO.getStartDate());
            existingStudent.setEndDate(studentDTO.getEndDate());
            existingStudent.setLanguage(studentDTO.getLanguage());
            existingStudent.setPosition(studentDTO.getPosition());
            existingStudent.setStatus(studentDTO.getStatus());
        return studentRepository.save(existingStudent);
    }

    @Override
    public Student updateUserId(Long userId, StudentDTO studentDTO) throws DataNotFoundException {
        //Tìm xem student có tồn tại hay không
        Student existingUser = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find student with id: "+userId));
        //Kiểm tra xem company id có tồn tại hay không
        Company existingCompany = companyRepository.findById(studentDTO.getCompanyId())
                .orElseThrow(() -> new DataNotFoundException(
                        "Cannot find user with id : "+studentDTO.getCompanyId()));
        //Kiểm tra xem supervisor id có tồn tại hay không
        Lecturer existingLecturer = lecturerRepository.findById(studentDTO.getLecturerId())
                .orElseThrow(() -> new DataNotFoundException(
                        "Cannot find user with id : "+studentDTO.getLecturerId()));
        //Kiểm tra xem company supervisor id có tồn tại hay không
        Mentor existingMentor = mentorRepository.findById(studentDTO.getMentorId())
                .orElseThrow(() -> new DataNotFoundException(
                        "Cannot find user with id : "+studentDTO.getMentorId()));
        existingUser.setStudentCode(studentDTO.getStudentCode());
        existingUser.setClassStudent(studentDTO.getClassStudent());
        existingUser.setMajor(studentDTO.getMajor());
        existingUser.setYearOfStudy(studentDTO.getYearOfStudy());
        existingUser.setCompany(existingCompany);
        existingUser.setLecturer(existingLecturer);
        existingUser.setMentor(existingMentor);
        existingUser.setStartDate(studentDTO.getStartDate());
        existingUser.setEndDate(studentDTO.getEndDate());
        existingUser.setLanguage(studentDTO.getLanguage());
        existingUser.setPosition(studentDTO.getPosition());
        existingUser.setStatus(studentDTO.getStatus());
        return studentRepository.save(existingUser);
    }

    @Override
    public void deleteStudent(Long studentId) {
        studentRepository.deleteById(studentId);
    }

    @Override
    public MentorOfStudentResponse getMentorOfStudent(Long studentCode) throws DataNotFoundException {
        Student student = studentRepository.findById(studentCode).orElseThrow(
                () -> new DataNotFoundException("Student not found")
        );
        return MentorOfStudentResponse.fromMentorOfStudentResponse(student);
    }

    @Override
    public LecturerOfStudentResponse getLecturerOfStudent(Long studentCode) throws DataNotFoundException {
        Student student = studentRepository.findById(studentCode).orElseThrow(
                () -> new DataNotFoundException("Student not found")
        );
        return LecturerOfStudentResponse.fromLecturerOfStudentResponse(student);
    }
}
