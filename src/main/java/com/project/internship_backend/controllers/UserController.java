package com.project.internship_backend.controllers;

import com.project.internship_backend.components.SecurityUtils;
import com.project.internship_backend.dtos.RefreshTokenDTO;
import com.project.internship_backend.dtos.UpdateUserDTO;
import com.project.internship_backend.dtos.UserDTO;
import com.project.internship_backend.dtos.UserLoginDTO;
import com.project.internship_backend.entities.Token;
import com.project.internship_backend.entities.User;
import com.project.internship_backend.exceptions.DataNotFoundException;
import com.project.internship_backend.responses.LoginResponse;
import com.project.internship_backend.responses.ResponseObject;
import com.project.internship_backend.responses.user.UserListResponse;
import com.project.internship_backend.responses.user.UserResponse;
import com.project.internship_backend.services.token.ITokenService;
import com.project.internship_backend.services.user.IUserService;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/users")
public class UserController {
    private final IUserService userService;
    private final ITokenService tokenService;
    private final SecurityUtils securityUtils;

    @PostMapping("/register")
    public ResponseEntity<ResponseObject> createUser(
            @RequestBody @Valid UserDTO userDTO,
            BindingResult result
    ) throws Exception {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();

            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message(errorMessages.toString())
                    .build());
        }

        if (!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
            //registerResponse.setMessage();
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message("Password phải trùng")
                    .build());
        }
        User user = userService.createUser(userDTO);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.CREATED)
                .data(UserResponse.fromUser(user))
                .message("Account registration successful")
                .build());
    }

    @PostMapping(value = "/upload-profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MENTOR')" +
            "or hasRole('ROLE_STUDENT') or hasRole('ROLE_LECTURER')")
    public ResponseEntity<ResponseObject> uploadProfileImage(
            @RequestParam("profile_image") MultipartFile profileImage
    ) throws Exception {
        User loginUser = securityUtils.getLoggedInUser();
        if (profileImage == null || profileImage.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message("Image file is required.")
                            .build()
            );
        }

        if (profileImage.getSize() > 10 * 1024 * 1024) { // 10MB
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body(ResponseObject.builder()
                            .message("Image file size exceeds the allowed limit of 10MB.")
                            .status(HttpStatus.PAYLOAD_TOO_LARGE)
                            .build());
        }

        // Check file type
        if (!FileUtils.isImageFile(profileImage)) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body(ResponseObject.builder()
                            .message("Uploaded file must be an image.")
                            .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .build());
        }

        // Store file and get filename
        String oldFileName = loginUser.getProfileImage();
        String imageName = FileUtils.storeFile(profileImage);

        userService.changeProfileImage(loginUser.getId(), imageName);
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

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody UserLoginDTO userLoginDTO
    ) throws Exception {
        // Kiểm tra thông tin đăng nhập và sinh token
        String token = userService.login(userLoginDTO);
        User userDetail = userService.getUserDetailsFromToken(token);
        // Trả về token trong response
        return ResponseEntity.ok(LoginResponse.builder()
                .message("Login thành công")
                .token(token)
                        .user_id(userDetail.getId())
                        .username(userDetail.getUsername())
                        .roles(userDetail.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .build());
    }


    @GetMapping("/details")
    public ResponseEntity<UserResponse> getUserDetails(@RequestHeader("Authorization") String token) {
        try {
            String extractedToken = token.substring(7); // Loại bỏ "Bearer " từ chuỗi token
            User user = userService.getUserDetailsFromToken(extractedToken);
            return ResponseEntity.ok(UserResponse.fromUser(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @PutMapping("/details/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT') or hasRole('ROLE_LECTURER') or hasRole('ROLE_MENTOR')")
    public ResponseEntity<UserResponse> updateUserDetails(
            @PathVariable Long userId,
            @RequestBody UpdateUserDTO updateUserDTO
    ) {
        try {
            User updatedUser = userService.updateUser(userId, updateUserDTO);
            return ResponseEntity.ok(UserResponse.fromUser(updatedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/details/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')" )
    public ResponseEntity<ResponseObject> getUserDetailById(
            @PathVariable Long userId
    ) throws DataNotFoundException {
        userService.getUserDetailById(userId);
        return ResponseEntity.ok(ResponseObject
                .builder()
                .data(userService.getUserDetailById(userId))
                .message("get student successfully")
                .build());
    }

    @PutMapping("/block/{userId}/{active}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> blockOrEnable(
            @Valid @PathVariable long userId,
            @Valid @PathVariable int active
    ) throws Exception {
        userService.blockOrEnable(userId, active > 0);
        String message = active > 0 ? "Successfully enabled the user." : "Successfully blocked the user.";
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(message)
                .status(HttpStatus.OK)
                .data(null)
                .build());
    }

    @PutMapping("/grantRole/{userId}/{roleId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> grantRole(
            @Valid @PathVariable long userId,
            @Valid @PathVariable long roleId // Đảm bảo tên đúng là roleId
    ) throws Exception {
        userService.grantRole(userId, roleId);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Phân quyền cho người dùng thành công")
                .status(HttpStatus.OK)
                .data(null)
                .build());
    }


    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT') " )
    public ResponseEntity<UserListResponse> getAllUsers(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int limit
    ) {
        // Tạo Pageable từ thông tin trang và giới hạn
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                Sort.by("id").ascending()
        );
        Page<UserResponse> UserResponsePage = userService.getALlUsers(keyword, pageRequest);
        int totalPages = UserResponsePage.getTotalPages();
        List<UserResponse> usersResponse = UserResponsePage.getContent();

        return ResponseEntity.ok(UserListResponse.builder()
                .users(usersResponse)
                .totalPages(totalPages)
                .build());
    }

    @DeleteMapping("/details/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> deleteUser(
            @PathVariable Long userId
    ) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message("Xóa người dùng thành công.")
                    .data(null)
                    .build());
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseObject.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message("Không tìm thấy người dùng với ID: " + userId)
                    .data(null)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("Đã xảy ra lỗi khi xóa người dùng.")
                    .data(null)
                    .build());
        }
    }

    @PutMapping("/details")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT') or hasRole('ROLE_LECTURER') or hasRole('ROLE_MENTOR')")
    public ResponseEntity<ResponseObject> updateUserDetails(
            @RequestBody UpdateUserDTO updatedUserDTO
    ) throws Exception {
        // Lấy thông tin User từ SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if (!(principal instanceof User)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ResponseObject.builder()
                            .message("Unauthorized access")
                            .status(HttpStatus.UNAUTHORIZED)
                            .build()
            );
        }
        User user = (User) principal;
        // Lấy userId từ đối tượng User
        Long userId = user.getId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("User ID not found")
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
        // Cập nhật thông tin user
        User updatedUser = userService.updateUser(userId, updatedUserDTO);

        return ResponseEntity.ok().body(
                ResponseObject.builder()
                        .message("Update user detail successfully")
                        .data(UserResponse.fromUser(updatedUser))
                        .status(HttpStatus.OK)
                        .build()
        );
    }
    @PostMapping("/logout")
    public ResponseEntity<ResponseObject> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            // Xóa thông tin xác thực khỏi SecurityContext
            SecurityContextHolder.clearContext();
            // Bạn có thể thêm logic để lưu token vào blacklist nếu cần
        }
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Logout thành công")
                .data(null)
                .build());
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<ResponseObject> refreshToken(
            @Valid @RequestBody RefreshTokenDTO refreshTokenDTO
    ) throws Exception {
        User userDetail = userService.getUserDetailsFromRefreshToken(refreshTokenDTO.getRefreshToken());
        Token jwtToken = tokenService.refreshToken(refreshTokenDTO.getRefreshToken(), userDetail);
        LoginResponse loginResponse = LoginResponse.builder()
                .message("Refresh token successfully")
                .token(jwtToken.getToken())
                .tokenType(jwtToken.getTokenType())
                .refreshToken(jwtToken.getRefreshToken())
                .username(userDetail.getUsername())
                .roles(userDetail.getAuthorities().stream().map(item -> item.getAuthority()).toList())
                .user_id(userDetail.getId()).build();
        return ResponseEntity.ok().body(
                ResponseObject.builder()
                        .data(loginResponse)
                        .message(loginResponse.getMessage())
                        .status(HttpStatus.OK)
                        .build());

    }
}

