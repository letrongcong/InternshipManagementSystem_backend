package com.project.internship_backend.services.user;

import com.project.internship_backend.dtos.UpdateUserDTO;
import com.project.internship_backend.dtos.UserDTO;
import com.project.internship_backend.dtos.UserLoginDTO;
import com.project.internship_backend.entities.User;
import com.project.internship_backend.exceptions.DataNotFoundException;
import com.project.internship_backend.responses.user.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface IUserService {
    User createUser(UserDTO userDTO) throws Exception;
    String login(UserLoginDTO userLoginDTO) throws Exception;
    User getUserDetailsFromToken(String token) throws Exception;
    User updateUser(Long userId, UpdateUserDTO updateUserDTO) throws Exception;
    void blockOrEnable(Long userId, Boolean active) throws DataNotFoundException;
    void grantRole(Long userId, Long roleId) throws DataNotFoundException;
    Page<UserResponse> getALlUsers(String keyword, PageRequest pageRequest);
    User getUserDetailById(Long userId) throws DataNotFoundException;
    void deleteUser(Long userId) throws DataNotFoundException;
    User getUserDetailsFromRefreshToken(String token) throws Exception;
    void changeProfileImage(Long userId, String imageName) throws Exception;
}
