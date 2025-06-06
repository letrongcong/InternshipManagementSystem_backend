package com.project.internship_backend.controllers;

import com.project.internship_backend.components.SecurityUtils;
import com.project.internship_backend.dtos.ReportDTO;
import com.project.internship_backend.entities.*;
import com.project.internship_backend.exceptions.DataNotFoundException;
import com.project.internship_backend.responses.ResponseObject;
import com.project.internship_backend.responses.report.*;
import com.project.internship_backend.services.lecturer.ILecturerService;
import com.project.internship_backend.services.report.IReportService;
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

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/reports")
public class ReportController {
    private final IReportService reportService;
    private final SecurityUtils securityUtils;
    private final ILecturerService lecturerService;

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_LECTURER')")
    public ResponseEntity<ResponseObject> createReport(
            @Valid @RequestBody ReportDTO reportDTO,
            BindingResult result) throws Exception {
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

        // Lấy thông tin User từ SecurityUtils
        User user = securityUtils.getLoggedInUser();
        if (user == null) {
            throw new Exception("User is not logged in or account is expired");
        }

        Lecturer lecturer = user.getLecturer();
        if (lecturer == null) {
            throw new Exception("User is not a lecturer");
        }

        Long lecturerId = lecturer.getId();
        Report report = reportService.createReport(reportDTO, lecturerId);

        // Lưu ReportStudent
        List<ReportStudent> reportStudents = new ArrayList<>();
        for (Long studentId : reportDTO.getStudentCodes()) {
            ReportStudent reportStudent = reportService.createReportStudent(report.getId(), studentId);
            reportStudents.add(reportStudent);
        }
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Create weekly report successfully")
                .status(HttpStatus.OK)
                .data(report)
                .build());
    }

    @GetMapping("/{reportId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_LECTURER')")
    public ResponseEntity<ResponseObject> getReportById(
            @PathVariable("reportId") Long reportId
    ) throws Exception {
        ReportResponse existReport =
                reportService.getReportById(reportId);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(existReport)
                .message("Get report successfully")
                .status(HttpStatus.OK)
                .build());
    }

    @GetMapping("/by-student")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_LECTURER') " +
            "or hasRole('ROLE_STUDENT') or hasRole('ROLE_MENTOR')")
    public ResponseEntity<ReportDetailListResponse> getReportsByStudentToken(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) throws Exception {
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                Sort.by("id").ascending()
        );

        User user = securityUtils.getLoggedInUser();
        if (user == null) {
            throw new Exception("User is not logged in or account is expired");
        }

        Student student = user.getStudent();
        if (student == null) {
            throw new Exception("User is not a student");
        }

        Long studentCode = student.getStudentCode();

        Page<ReportDetailResponse> reportsByStudentCodeResponsePage =
                reportService.getReportsByStudentCode(keyword, pageRequest, studentCode);
        int totalPages = reportsByStudentCodeResponsePage.getTotalPages();
        List<ReportDetailResponse> reportByStudentResponses = reportsByStudentCodeResponsePage.getContent();

        return ResponseEntity.ok(ReportDetailListResponse.builder()
                .reportByStudentResponses(reportByStudentResponses)
                .totalPages(totalPages)
                .build());
    }

    @GetMapping("/by-student/{studentCode}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_LECTURER') " +
            "or hasRole('ROLE_STUDENT') or hasRole('ROLE_MENTOR')")
    public ResponseEntity<ReportDetailListResponse> getReportsByStudentCode(
            @PathVariable Long studentCode,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) throws DataNotFoundException {
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                Sort.by("id").ascending()
        );

        Page<ReportDetailResponse> reportsByStudentCodeResponsePage =
                reportService.getReportsByStudentCode(keyword, pageRequest, studentCode);
        int totalPages = reportsByStudentCodeResponsePage.getTotalPages();
        List<ReportDetailResponse> reportByStudentResponses = reportsByStudentCodeResponsePage.getContent();

        return ResponseEntity.ok(ReportDetailListResponse.builder()
                .reportByStudentResponses(reportByStudentResponses)
                .totalPages(totalPages)
                .build());
    }

    @GetMapping("/by-lecturer")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_LECTURER')")
    public ResponseEntity<ReportListResponse> getReportByLecturer(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) throws Exception {
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                Sort.by("id").ascending()
        );

        User user = securityUtils.getLoggedInUser();
        if (user == null) {
            throw new Exception("User is not logged in or account is expired");
        }

        Lecturer lecturer = user.getLecturer();
        if (lecturer == null) {
            throw new Exception("User is not a lecturer");
        }

        Long lecturerId = lecturer.getId();

        Page<ReportResponse> reportResponsePage = reportService.getReportByLecturer(keyword, pageRequest, lecturerId);
        int totalPages = reportResponsePage.getTotalPages();
        List<ReportResponse> reportResponses = reportResponsePage.getContent();

        return ResponseEntity.ok(ReportListResponse.builder()
                .reports(reportResponses)
                .totalPages(totalPages)
                .build());
    }

    @PutMapping("/by-token/{reportId}")
    @PreAuthorize("hasRole('ROLE_LECTURER')")
    public ResponseEntity<ResponseObject> updateReportByLecturer(
            @PathVariable Long reportId,
            @Valid @RequestBody ReportDTO reportDTO
    ) throws Exception {

        User loggedInUser = securityUtils.getLoggedInUser(); // Gọi SecurityUtils

        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ResponseObject.builder()
                            .message("Unauthorized access")
                            .status(HttpStatus.UNAUTHORIZED)
                            .build()
            );
        }
        Long lecturerId = loggedInUser.getLecturer().getId();
        if (lecturerId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("User ID not found")
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
        Report updatedReport = reportService.updateReportByLecturer(lecturerId, reportId, reportDTO );
        return ResponseEntity.ok(ResponseObject
                .builder()
                .data(updatedReport)
                .message("Update lecturer successfully")
                .build());
    }

//    @PutMapping("/{reportId}")
//    @PreAuthorize("hasRole('ROLE_LECTURER')")
//    public ResponseEntity<ResponseObject> updateReportByLecturer(
//            @PathVariable Long reportId,
//            @Valid @RequestBody ReportDTO reportDTO) throws Exception {
//
//        User loggedInUser = securityUtils.getLoggedInUser();
//
//        if (loggedInUser == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
//                    ResponseObject.builder()
//                            .message("Unauthorized access")
//                            .status(HttpStatus.UNAUTHORIZED)
//                            .build()
//            );
//        }
//
//        Report updatedReport = reportService.updateReportByLecturer(reportId, reportDTO);
//        return ResponseEntity.ok(ResponseObject
//                .builder()
//                .data(updatedReport)
//                .message("Update report successfully")
//                .build());
//    }
}

