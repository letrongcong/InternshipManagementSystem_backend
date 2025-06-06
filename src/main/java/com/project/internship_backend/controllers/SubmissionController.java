package com.project.internship_backend.controllers;

import com.project.internship_backend.components.SecurityUtils;
import com.project.internship_backend.dtos.SubmissionDTO;
import com.project.internship_backend.entities.Student;
import com.project.internship_backend.entities.Submission;
import com.project.internship_backend.entities.User;
import com.project.internship_backend.exceptions.DataNotFoundException;
import com.project.internship_backend.responses.ResponseObject;
import com.project.internship_backend.responses.submission.*;
import com.project.internship_backend.services.submission.ISubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/submissions")
public class SubmissionController {
    private final ISubmissionService submissionService;
    private final SecurityUtils securityUtils;

    @PostMapping("/{reportId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') ")
    public ResponseEntity<ResponseObject> createSubmission(
            @PathVariable Long reportId,
            @Valid @ModelAttribute SubmissionDTO submissionDTO,
            BindingResult result ) throws Exception {
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
        // Lưu file và lấy đường dẫn
        String file = storeFile(submissionDTO.getFile());
        User user = securityUtils.getLoggedInUser();
        if (user == null) {
            throw new Exception("User is not logged in or account is expired");
        }

        // Lấy studentId từ đối tượng User
        Student student = user.getStudent();
        if (student == null) {
            throw new Exception("User is not a student");
        }

        Long studentCode = student.getStudentCode();

        // Tạo submission với đường dẫn file
        Submission submission = submissionService.createSubmission(file, submissionDTO.getNote(), reportId, studentCode);

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Create submission successfully")
                .status(HttpStatus.OK)
//                .data(submission)
                .build());
    }


    private String storeFile(MultipartFile file) throws IOException {
        if (!isWordFile(file) || file.getOriginalFilename() == null) {
            throw new IOException("Invalid Word file format");
        }
        // Lấy tên file gốc
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        // Thêm UUID để đảm bảo tính duy nhất
        String uniqueFileName = UUID.randomUUID() + "_" + fileName;
        // Đường dẫn đến thư mục lưu file
        java.nio.file.Path uploadDir = Paths.get("uploads");
        // Kiểm tra và tạo thư mục nếu chưa tồn tại
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        java.nio.file.Path destination = Paths.get(uploadDir.toString(), uniqueFileName);
        // Sao chép file vào thư mục đích
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFileName;
    }

    //application/vnd.openxmlformats-officedocument.wordprocessingml.document: Kiểu MIME của tệp .docx (Microsoft Word hiện đại).
    //application/msword: Kiểu MIME của tệp .doc (Microsoft Word cũ).
    private boolean isWordFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null &&
                (contentType.equals("application/msword") ||
                        contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
    }


    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT') " +
            "or hasRole('ROLE_LECTURER') or hasRole('ROLE_MENTOR')" )
    public ResponseEntity<SubmissionDetailListResponse> getAllSubmissions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        // Tạo Pageable từ thông tin trang và giới hạn
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                Sort.by("id").ascending()
        );
        Page<SubmissionDetailResponse> submissionDetailResponsePage = submissionService.getAllSubmissions(pageRequest);
        int totalPages = submissionDetailResponsePage.getTotalPages();
        List<SubmissionDetailResponse> submissionDetailResponses = submissionDetailResponsePage.getContent();

        return ResponseEntity.ok(SubmissionDetailListResponse.builder()
                .submissionDetailResponses(submissionDetailResponses)
                .totalPages(totalPages)
                .build());
    }

    @GetMapping("/{studentCode}/{reportId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT') " +
            "or hasRole('ROLE_LECTURER') or hasRole('ROLE_MENTOR')" )
    public ResponseEntity<SubmissionDetailListResponse> getByStudentCodeAndReportId (
            @PathVariable("studentCode") Long studentCode,
            @PathVariable("reportId") Long reportId,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) throws DataNotFoundException{
        // Tạo Pageable từ thông tin trang và giới hạn
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                Sort.by("id").ascending()
        );
        Page<SubmissionDetailResponse> submissionDetailResponsePage = submissionService.
                getAllSubmissionsByStudentCodeAndReportId(studentCode, reportId, keyword, pageRequest);
        int totalPages = submissionDetailResponsePage.getTotalPages();
        List<SubmissionDetailResponse> submissionDetailResponses = submissionDetailResponsePage.getContent();

        return ResponseEntity.ok(SubmissionDetailListResponse.builder()
                .submissionDetailResponses(submissionDetailResponses)
                .totalPages(totalPages)
                .build());
    }

    @GetMapping("/{reportId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT') " +
            "or hasRole('ROLE_LECTURER') or hasRole('ROLE_MENTOR')" )
    public ResponseEntity<SubmissionDetailListResponse> getStudentByCode (
            @PathVariable("reportId") Long reportId,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) throws DataNotFoundException{
        // Tạo Pageable từ thông tin trang và giới hạn
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                Sort.by("id").ascending()
        );
        Page<SubmissionDetailResponse> submissionDetailResponsePage = submissionService.getAllSubmissionsByReport(reportId, keyword, pageRequest);
        int totalPages = submissionDetailResponsePage.getTotalPages();
        List<SubmissionDetailResponse> submissionDetailResponses = submissionDetailResponsePage.getContent();

        return ResponseEntity.ok(SubmissionDetailListResponse.builder()
                .submissionDetailResponses(submissionDetailResponses)
                .totalPages(totalPages)
                .build());
    }

    @GetMapping("word/{wordName:.+}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT') " +
            "or hasRole('ROLE_LECTURER') or hasRole('ROLE_MENTOR')" )
    public ResponseEntity<?> downloadWord(@PathVariable String wordName) {
        try {
            // Đường dẫn tới tệp Word
            java.nio.file.Path wordPath = Paths.get("uploads", wordName);
            System.out.println("Word Path: " + wordPath.toAbsolutePath());

            // Kiểm tra tệp có tồn tại và đọc được không
            if (Files.exists(wordPath)) {
                // Tạo UrlResource từ đường dẫn
                UrlResource resource = new UrlResource(wordPath.toUri());

                if (resource.exists() && resource.isReadable()) {
                    // Lấy MediaType tương ứng với file
                    MediaType mediaType = getMediaType(wordName);
                    System.out.println("Media Type: " + mediaType);

                    // Trả về ResponseEntity với header tải xuống, tên tệp là tên gốc
                    return ResponseEntity.ok()
                            .contentType(mediaType)
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + wordName + "\"")
                            .body(resource);
                } else {
                    // Trả về 404 nếu tệp không tồn tại
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Tệp " + wordName + " không tồn tại hoặc không thể đọc được.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Tệp không tồn tại.");
            }
        } catch (Exception e) {
            // Trả về 500 nếu xảy ra lỗi
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Không thể tải xuống tệp: " + wordName + ". Lỗi: " + e.getMessage());
        }
    }

    private MediaType getMediaType(String fileName) {
        if (fileName.toLowerCase().endsWith(".doc")) {
            return MediaType.valueOf("application/msword");
        } else if (fileName.toLowerCase().endsWith(".docx")) {
            return MediaType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        }
        return MediaType.APPLICATION_OCTET_STREAM; // Mặc định nếu không phải file Word
    }


    @GetMapping("student-code/{studentCode}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT') " +
            "or hasRole('ROLE_LECTURER') or hasRole('ROLE_MENTOR')" )
    public ResponseEntity<SubmissionDetailListResponse> getSubmissionByStudent (
            @PathVariable("studentCode") Long studentCode,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) throws DataNotFoundException{
        // Tạo Pageable từ thông tin trang và giới hạn
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                Sort.by("id").ascending()
        );
        Page<SubmissionDetailResponse> submissionDetailResponsePage = submissionService.getAllSubmissionsByStudent(studentCode, keyword, pageRequest);
        int totalPages = submissionDetailResponsePage.getTotalPages();
        List<SubmissionDetailResponse> submissionDetailResponses = submissionDetailResponsePage.getContent();

        return ResponseEntity.ok(SubmissionDetailListResponse.builder()
                .submissionDetailResponses(submissionDetailResponses)
                .totalPages(totalPages)
                .build());
    }
}
