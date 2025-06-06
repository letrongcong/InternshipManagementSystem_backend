package com.project.internship_backend.controllers;

import com.project.internship_backend.components.SecurityUtils;
import com.project.internship_backend.dtos.LecturerDTO;
import com.project.internship_backend.entities.Lecturer;
import com.project.internship_backend.entities.User;
import com.project.internship_backend.exceptions.DataNotFoundException;
import com.project.internship_backend.responses.ResponseObject;
import com.project.internship_backend.responses.lecturer.LecturerDetailResponse;
import com.project.internship_backend.responses.lecturer.LecturerListResponse;
import com.project.internship_backend.responses.lecturer.LecturerResponse;
import com.project.internship_backend.services.lecturer.ILecturerService;
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
@RequestMapping("api/lecturers")
public class LecturerController {
    private final ILecturerService lecturerService;
    private final SecurityUtils securityUtils;

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> createLecturer(
            @Valid @RequestBody LecturerDTO lecturerDTO,
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
        Lecturer lecturer = lecturerService.createLecturer(lecturerDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Create lecturer successfully")
                .status(HttpStatus.OK)
                .data(lecturer)
                .build());
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT') " )
    public ResponseEntity<LecturerListResponse> getAllLecturers(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        // Tạo Pageable từ thông tin trang và giới hạn
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                Sort.by("id").ascending()
        );
        Page<LecturerDetailResponse> lecturerDetailResponsePage = lecturerService.getAllLecturers(keyword, pageRequest);
        int totalPages = lecturerDetailResponsePage.getTotalPages();
        List<LecturerDetailResponse> lecturerDetailResponses = lecturerDetailResponsePage.getContent();

        return ResponseEntity.ok(LecturerListResponse.builder()
                .lecturers(lecturerDetailResponses)
                .totalPages(totalPages)
                .build());
    }

    @GetMapping("/{lecturerId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT') " +
            "or hasRole('ROLE_LECTURER') or hasRole('ROLE_MENTOR')" )
    public ResponseEntity<ResponseObject> getLecturerById (
            @PathVariable("lecturerId") Long lecturerId
    ) throws DataNotFoundException {
        LecturerDetailResponse existingLecturer = lecturerService.getLecturerById(lecturerId);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(existingLecturer)
                .message("Get lecturer successfully")
                .status(HttpStatus.OK)
                .build());
    }

    @GetMapping("/by-token")
    @PreAuthorize("hasRole('ROLE_LECTURER')")
    public ResponseEntity<ResponseObject> getLecturerInfo() throws DataNotFoundException {
        User loggedInUser = securityUtils.getLoggedInUser(); // Lấy người dùng đã đăng nhập

        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ResponseObject.builder()
                            .message("Unauthorized access")
                            .status(HttpStatus.UNAUTHORIZED)
                            .build()
            );
        }

        Long userId = loggedInUser.getId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("User ID not found")
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }

        LecturerDetailResponse lecturerDetail = lecturerService.getLecturerByToken(userId); // Lấy thông tin lecturer
        return ResponseEntity.ok(ResponseObject.builder()
                .data(lecturerDetail)
                .message("Get lecturer information successfully")
                .status(HttpStatus.OK)
                .build());
    }

    @PutMapping("/{lecturerId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_LECTURER')" )
    public ResponseEntity<ResponseObject> updateLecturer(
            @PathVariable Long lecturerId,
            @Valid @RequestBody LecturerDTO lecturerDTO
    ) throws DataNotFoundException {
        lecturerService.updateLecturer(lecturerId, lecturerDTO);
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Update lecturer successfully")
                .status(HttpStatus.OK)
                .data(lecturerService.getLecturerById(lecturerId))
                .build());
    }

    @PutMapping("/by-token")
    @PreAuthorize("hasRole('ROLE_LECTURER')")
    public ResponseEntity<ResponseObject> updateLecturerByToken(
            @Valid @RequestBody LecturerDTO lecturerDTO) throws Exception {

        User loggedInUser = securityUtils.getLoggedInUser(); // Gọi SecurityUtils

        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ResponseObject.builder()
                            .message("Unauthorized access")
                            .status(HttpStatus.UNAUTHORIZED)
                            .build()
            );
        }
        Long userId = loggedInUser.getId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("User ID not found")
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
        Lecturer updatedLecturer = lecturerService.updateLecturer(userId, lecturerDTO);
        return ResponseEntity.ok(ResponseObject
                .builder()
                .data(updatedLecturer)
                .message("Update lecturer successfully")
                .build());
    }

    @DeleteMapping("/{lecturerId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> deleteLecturer(
            @PathVariable Long lecturerId) {
        lecturerService.deleteLecturer(lecturerId);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Delete lecturer successfully")
                        .build());
    }
}
