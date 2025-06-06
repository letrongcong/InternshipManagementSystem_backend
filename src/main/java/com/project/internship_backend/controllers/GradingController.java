package com.project.internship_backend.controllers;

import com.project.internship_backend.components.SecurityUtils;
import com.project.internship_backend.dtos.GradingDTO;
import com.project.internship_backend.entities.Grading;
import com.project.internship_backend.entities.Lecturer;
import com.project.internship_backend.entities.User;
import com.project.internship_backend.responses.ResponseObject;
import com.project.internship_backend.services.grading.IGradingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/gradings")
public class GradingController {
    private final IGradingService gradingService;
    private final SecurityUtils securityUtils;

    @PostMapping("/{submissionId}")
    @PreAuthorize("hasRole('ROLE_LECTURER')")
    public ResponseEntity<ResponseObject> createGrading(
            @PathVariable Long submissionId,
            @Valid @RequestBody GradingDTO gradingDTO,
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

        // Sử dụng SecurityUtils để lấy thông tin người dùng hiện tại
        User user = securityUtils.getLoggedInUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ResponseObject.builder()
                            .message("Unauthorized access")
                            .status(HttpStatus.UNAUTHORIZED)
                            .build()
            );
        }

        // Lấy lecturerId từ User
        Lecturer lecturer = user.getLecturer();
        if (lecturer == null) {
            throw new Exception("User is not a lecturer");
        }

        Long lecturerId = lecturer.getId();
        Grading grading = gradingService.createGrading(lecturerId, submissionId, gradingDTO);

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Create grading successfully")
                .status(HttpStatus.OK)
                .data(grading)
                .build());
    }

    @GetMapping("/{submissionId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT') " +
            "or hasRole('ROLE_LECTURER') or hasRole('ROLE_MENTOR')")
    public ResponseEntity<ResponseObject> getGradingBySubmission(
            @PathVariable("submissionId") Long submissionId
    ) throws Exception {
        Grading existingGrading = gradingService.getGradingBySubmissionId(submissionId);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(existingGrading)
                .message("Get grading successfully")
                .status(HttpStatus.OK)
                .build());
    }

    @PutMapping("/{gradingId}")
    @PreAuthorize("hasRole('ROLE_LECTURER')")
    public ResponseEntity<ResponseObject> updateGrading(
            @PathVariable Long gradingId,
            @Valid @RequestBody GradingDTO gradingDTO
    ) throws Exception {
        gradingService.updateGrading(gradingId, gradingDTO);
        return ResponseEntity.ok(ResponseObject
                .builder()
                .data(gradingService.getGradingById(gradingId))
                .message("Update grading successfully")
                .status(HttpStatus.OK)
                .build());
    }
}
