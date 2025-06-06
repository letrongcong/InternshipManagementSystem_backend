package com.project.internship_backend.controllers;

import com.project.internship_backend.dtos.CompanyDTO;
import com.project.internship_backend.entities.Company;
import com.project.internship_backend.entities.User;
import com.project.internship_backend.responses.ResponseObject;
import com.project.internship_backend.responses.company.CompanyListResponse;
import com.project.internship_backend.responses.company.CompanyResponse;
import com.project.internship_backend.services.company.ICompanyService;
import com.project.internship_backend.utils.FileUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/companies")
public class CompanyController {
    private final ICompanyService companyService;
    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> createCompany(
            @Valid @RequestBody CompanyDTO companyDTO,
            BindingResult result
    ) {
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
        Company company = companyService.createCompany(companyDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Create company successfully")
                .status(HttpStatus.OK)
                .data(company)
                .build());
    }

    @PostMapping(value = "/upload-logo-image/{companyId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> uploadLogoImage(
            @PathVariable("companyId") Long companyId,
            @RequestParam("logo") MultipartFile uploadProfile
    ) throws Exception {
        Company existCompany = companyService.getCompanyById(companyId);
        if (uploadProfile == null || uploadProfile.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message("Image file is required.")
                            .build()
            );
        }

        if (uploadProfile.getSize() > 10 * 1024 * 1024) { // 10MB
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body(ResponseObject.builder()
                            .message("Image file size exceeds the allowed limit of 10MB.")
                            .status(HttpStatus.PAYLOAD_TOO_LARGE)
                            .build());
        }

        // Check file type
        if (!FileUtils.isImageFile(uploadProfile)) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body(ResponseObject.builder()
                            .message("Uploaded file must be an image.")
                            .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .build());
        }

        // Store file and get filename
        String oldFileName = existCompany.getLogo();
        String imageName = FileUtils.storeFile(uploadProfile);

        companyService.changeLogoImage(existCompany.getId(), imageName);
        // Delete old file if exists
        if (!StringUtils.isEmpty(oldFileName)) {
            FileUtils.deleteFile(oldFileName);
        }

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Upload profile image successfully")
                .status(HttpStatus.CREATED)
                .data(imageName) // Return the filename or image URL
                .build());
    }

    @GetMapping("/profile-images/{imageName}")
    @PreAuthorize("hasRole('ROLE_MENTOR') or hasRole('ROLE_ADMIN')" +
            "or hasRole('ROLE_STUDENT') or hasRole('ROLE_LECTURER')")
    public ResponseEntity<?> viewImage(@PathVariable String imageName) {
        try {
            java.nio.file.Path imagePath = Paths.get("uploads/"+imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(new UrlResource(Paths.get("uploads/default-profile-image.jpeg").toUri()));
                //return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT') " +
            "or hasRole('ROLE_LECTURER') or hasRole('ROLE_MENTOR')" )
    public ResponseEntity<CompanyListResponse> getAllCompanies(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                Sort.by("id").ascending()
        );
        Page<CompanyResponse> companyResponsePage = companyService.getAllCompanies(keyword, pageRequest);
        int totalPages = companyResponsePage.getTotalPages();
        List<CompanyResponse> companyResponses = companyResponsePage.getContent();

        return ResponseEntity.ok(CompanyListResponse.builder()
                .companies(companyResponses)
                .totalPages(totalPages)
                .build());
    }

    @GetMapping("/{companyId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT') " +
            "or hasRole('ROLE_LECTURER') or hasRole('ROLE_MENTOR')" )
    public ResponseEntity<ResponseObject> getCompanyById(
            @PathVariable("companyId") Long companyId
    ) {
        Company existingCompany = companyService.getCompanyById(companyId);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(existingCompany)
                .message("Get company successfully")
                .status(HttpStatus.OK)
                .build());
    }

    @PutMapping("/{companyId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> updateCompany(
            @PathVariable Long companyId,
            @Valid @RequestBody CompanyDTO companyDTO
            ) {
        companyService.updateCompany(companyId, companyDTO);
        return ResponseEntity.ok(ResponseObject
                .builder()
                .data(companyService.getCompanyById(companyId))
                .message("Update company successfully")
                .build());
    }

    @DeleteMapping("/{companyId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> deleteCompany(
            @PathVariable Long companyId) {
        companyService.deleteCompany(companyId);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Delete company successfully")
                        .build());
    }
}
