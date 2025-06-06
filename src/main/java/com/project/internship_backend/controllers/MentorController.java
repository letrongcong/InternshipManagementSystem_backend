package com.project.internship_backend.controllers;

import com.project.internship_backend.components.SecurityUtils;
import com.project.internship_backend.dtos.MentorDTO;
import com.project.internship_backend.entities.Mentor;
import com.project.internship_backend.entities.User;
import com.project.internship_backend.responses.ResponseObject;
import com.project.internship_backend.responses.mentor.MentorDetailResponse;
import com.project.internship_backend.responses.mentor.MentorListResponse;
import com.project.internship_backend.responses.mentor.MentorResponse;
import com.project.internship_backend.services.mentor.IMentorService;
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
@RequestMapping("api/mentors")
public class MentorController {
    private final IMentorService mentorService;
    private final SecurityUtils securityUtils;

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MENTOR')" )
    public ResponseEntity<ResponseObject> createMentor(
            @Valid @RequestBody MentorDTO mentorDTO,
            BindingResult result
    ) throws Exception {
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
        Mentor mentor =
                mentorService.createMentor(mentorDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Create mentor successfully")
                .status(HttpStatus.OK)
                .data(mentor)
                .build());
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT') " +
            "or hasRole('ROLE_LECTURER') or hasRole('ROLE_MENTOR')" )
    public ResponseEntity<MentorListResponse> getAllMentors(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        // Tạo Pageable từ thông tin trang và giới hạn
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                Sort.by("id").ascending()
        );
        Page<MentorDetailResponse> mentorDetailResponsePage = mentorService.getAllMentors(keyword, pageRequest);
        int totalPages = mentorDetailResponsePage.getTotalPages();
        List<MentorDetailResponse> mentorDetailResponses = mentorDetailResponsePage.getContent();

        return ResponseEntity.ok(MentorListResponse.builder()
                .mentors(mentorDetailResponses)
                .totalPages(totalPages)
                .build());
    }

    @GetMapping("/{mentorId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT') " +
            "or hasRole('ROLE_LECTURER') or hasRole('ROLE_MENTOR')" )
    public ResponseEntity<ResponseObject> getMentorById(
            @PathVariable("mentorId") Long mentorId
    ) throws Exception {
        MentorDetailResponse existingMentor =
                mentorService.getMentorById(mentorId);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(existingMentor)
                .message("Get mentor successfully")
                .status(HttpStatus.OK)
                .build());
    }

    @GetMapping("/by-token")
    @PreAuthorize("hasRole('ROLE_MENTOR')")
    public ResponseEntity<ResponseObject> getMentorInfo() throws Exception {
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

        // Lấy thông tin mentor
        MentorDetailResponse mentorDetail = mentorService.getMentorByToken(userId);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(mentorDetail)
                .message("Get mentor information successfully")
                .status(HttpStatus.OK)
                .build());
    }

    @PutMapping("/{mentorId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MENTOR')" )
    public ResponseEntity<ResponseObject> updateMentorById(
            @PathVariable Long mentorId,
            @Valid @RequestBody MentorDTO mentorDTO
    ) throws Exception {
        mentorService.updateMentor(mentorId, mentorDTO);
        return ResponseEntity.ok(ResponseObject
                .builder()
                .data(mentorService.getMentorById(mentorId))
                .message("Update mentor successfully")
                .build());
    }

    @PutMapping("/by-token")
    @PreAuthorize("hasRole('ROLE_MENTOR')")
    public ResponseEntity<ResponseObject> updateMentorByToken(
            @Valid @RequestBody MentorDTO mentorDTO) throws Exception {

        // Sử dụng SecurityUtils để lấy thông tin user đăng nhập
        User loggedInUser = securityUtils.getLoggedInUser();

        if (loggedInUser == null || loggedInUser.getId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ResponseObject.builder()
                            .message("Unauthorized access or user ID not found")
                            .status(HttpStatus.UNAUTHORIZED)
                            .build()
            );
        }

        Mentor updatedMentor = mentorService.updateMentor(loggedInUser.getId(), mentorDTO);

        return ResponseEntity.ok(ResponseObject
                .builder()
                .data(updatedMentor)
                .message("Update mentor successfully")
                .build());
    }

    @DeleteMapping("/{mentorId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') ")
    public ResponseEntity<ResponseObject> deleteMentor(
            @PathVariable Long mentorId) {
        mentorService.deleteMentor(mentorId);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Delete mentor successfully")
                        .build());
    }
}
