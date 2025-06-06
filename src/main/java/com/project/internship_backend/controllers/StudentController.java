package com.project.internship_backend.controllers;

import com.project.internship_backend.components.SecurityUtils;
import com.project.internship_backend.dtos.StudentDTO;
import com.project.internship_backend.entities.Lecturer;
import com.project.internship_backend.entities.Mentor;
import com.project.internship_backend.entities.Student;
import com.project.internship_backend.entities.User;
import com.project.internship_backend.exceptions.DataNotFoundException;
import com.project.internship_backend.responses.ResponseObject;
import com.project.internship_backend.responses.student.*;
import com.project.internship_backend.services.student.IStudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/students")
public class StudentController {
    private final IStudentService studentService;
    private final SecurityUtils securityUtils;

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT') ")
    public ResponseEntity<ResponseObject> createStudent(
            @Valid @RequestBody StudentDTO studentDTO,
            BindingResult result ) throws DataNotFoundException {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(errorMessages.toString())
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .build());
        }
        Student student = studentService.createStudent(studentDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Create student successfully")
                .status(HttpStatus.OK)
                .data(student)
                .build());
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<StudentListResponse> getAllStudents(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        // Tạo Pageable từ thông tin trang và giới hạn
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                Sort.by("id").ascending()
        );
        Page<StudentResponse> studentResponsePage = studentService.getAllStudents(keyword, pageRequest);
        int totalPages = studentResponsePage.getTotalPages();
        List<StudentResponse> studentRespons = studentResponsePage.getContent();

        return ResponseEntity.ok(StudentListResponse.builder()
                .students(studentRespons)
                .totalPages(totalPages)
                .build());
    }


    @GetMapping("/{studentCode}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT') " +
            "or hasRole('ROLE_LECTURER') or hasRole('ROLE_MENTOR')" )
    public ResponseEntity<ResponseObject> getStudentByCode (
            @PathVariable("studentCode") Long studentCode
    ) throws DataNotFoundException{
        StudentDetailResponse existingStudent = studentService.getStudentByCode(studentCode);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(existingStudent)
                .message("Get student information successfully")
                .status(HttpStatus.OK)
                .build());
    }

    @GetMapping("/by-lecturer")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_LECTURER') ")
    public ResponseEntity<ResponseObject> getStudentsByLecturer(
            @RequestParam(defaultValue = "") String keyword) throws Exception {

        User user = securityUtils.getLoggedInUser();
        if (user == null) {
            throw new Exception("User is not logged in or account is expired");
        }

        // Lấy lecturerId từ đối tượng User
        Lecturer lecturer = user.getLecturer();
        if (lecturer == null) {
            throw new Exception("User is not a lecturer");
        }

        Long lecturerId = lecturer.getId();

        List<StudentDetailResponse> students = studentService.getStudentsByLecturer(lecturerId, keyword);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(students)
                .message("Get student successfully")
                .status(HttpStatus.OK)
                .build());
    }

    @GetMapping("/by-mentor")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MENTOR') ")
    public ResponseEntity<ResponseObject> getStudentsByMentor(
            @RequestParam(defaultValue = "") String keyword) throws Exception {

        User user = securityUtils.getLoggedInUser();
        if (user == null) {
            throw new Exception("User is not logged in or account is expired");
        }

        // Lấy lecturerId từ đối tượng User
        Mentor mentor = user.getMentor();
        if (mentor == null) {
            throw new Exception("User is not a mentor");
        }

        Long mentorId = mentor.getId();

        List<StudentDetailResponse> students = studentService.getStudentsByMentor(mentorId, keyword);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(students)
                .message("Get student successfully")
                .status(HttpStatus.OK)
                .build());
    }

    @PutMapping("/{studentCode}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> updateStudent(
            @PathVariable Long studentCode,
            @Valid @RequestBody StudentDTO studentDTO
    ) throws DataNotFoundException{
        studentService.updateStudent(studentCode, studentDTO);
        return ResponseEntity.ok(ResponseObject
                .builder()
                .data(studentService.getStudentByCode(studentCode))
                .message("Update student successfully")
                .build());
    }

    @PutMapping("/by-token")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> updateStudentByToken(
            @Valid @RequestBody StudentDTO studentDTO) {

        // Lấy thông tin người dùng đăng nhập qua SecurityUtils
        User loggedInUser = securityUtils.getLoggedInUser();

        // Kiểm tra nếu người dùng chưa đăng nhập hoặc tài khoản không hợp lệ
        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ResponseObject.builder()
                            .message("Unauthorized access or account is expired")
                            .status(HttpStatus.UNAUTHORIZED)
                            .build()
            );
        }

        // Kiểm tra nếu ID người dùng không hợp lệ
        Long userId = loggedInUser.getStudent().getUser().getId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("User ID not found")
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }

        try {
            // Cập nhật thông tin sinh viên
            Student updatedStudent = studentService.updateUserId(userId, studentDTO);

            return ResponseEntity.ok(ResponseObject.builder()
                    .data(updatedStudent)
                    .message("Update student successfully")
                    .status(HttpStatus.OK)
                    .build());
        } catch (Exception ex) {
            // Xử lý lỗi (nếu có)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder()
                            .message("An error occurred while updating student: " + ex.getMessage())
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .build()
            );
        }
    }

    @DeleteMapping("/{studentId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> deleteStudent(
            @PathVariable Long studentId) {
        studentService.deleteStudent(studentId);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Delete student successfully")
                        .build());
    }

    @GetMapping("mentor")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> getMentorOfStudent () throws Exception {
        User user = securityUtils.getLoggedInUser();
        if (user == null) {
            throw new Exception("User is not logged in or account is expired");
        }

        // Lấy lecturerId từ đối tượng User
        Student student = user.getStudent();
        if (student == null) {
            throw new Exception("User is not a student");
        }

        Long studentCode = student.getStudentCode();
        MentorOfStudentResponse existingMentorOfStudentResponse = studentService.getMentorOfStudent(studentCode);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(existingMentorOfStudentResponse)
                .message("Get student information successfully")
                .status(HttpStatus.OK)
                .build());
    }

    @GetMapping("lecturer")
    @PreAuthorize("hasRole('ROLE_STUDENT') ")
    public ResponseEntity<ResponseObject> getLecturerOfStudent () throws Exception {
        User user = securityUtils.getLoggedInUser();
        if (user == null) {
            throw new Exception("User is not logged in or account is expired");
        }

        // Lấy lecturerId từ đối tượng User
        Student student = user.getStudent();
        if (student == null) {
            throw new Exception("User is not a student");
        }
        Long studentCode = student.getStudentCode();
        LecturerOfStudentResponse existingLecturerOfStudentResponse = studentService.getLecturerOfStudent(studentCode);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(existingLecturerOfStudentResponse)
                .message("Get student information successfully")
                .status(HttpStatus.OK)
                .build());
    }

    @GetMapping("/by-token")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> getStudentByToken() {
        // Lấy thông tin người dùng đăng nhập qua SecurityUtils
        User loggedInUser = securityUtils.getLoggedInUser();

        // Kiểm tra nếu người dùng chưa đăng nhập hoặc tài khoản không hợp lệ
        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ResponseObject.builder()
                            .message("Unauthorized access or account is expired")
                            .status(HttpStatus.UNAUTHORIZED)
                            .build()
            );
        }

        // Lấy thông tin sinh viên từ người dùng đã đăng nhập
        Student student = loggedInUser.getStudent();
        if (student == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("Logged-in user is not a student")
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }

        try {
            // Truy xuất thông tin chi tiết sinh viên
            StudentDetailResponse studentDetailResponse = studentService.getStudentByCode(student.getStudentCode());

            return ResponseEntity.ok(ResponseObject.builder()
                    .data(studentDetailResponse)
                    .message("Get student information successfully")
                    .status(HttpStatus.OK)
                    .build());
        } catch (Exception ex) {
            // Xử lý lỗi nếu có
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder()
                            .message("An error occurred while retrieving student information: " + ex.getMessage())
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .build()
            );
        }
    }

}
