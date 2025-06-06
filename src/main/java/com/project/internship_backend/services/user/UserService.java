package com.project.internship_backend.services.user;

import com.project.internship_backend.components.JwtTokenUtils;
import com.project.internship_backend.dtos.UpdateUserDTO;
import com.project.internship_backend.dtos.UserDTO;
import com.project.internship_backend.dtos.UserLoginDTO;
import com.project.internship_backend.entities.Role;
import com.project.internship_backend.entities.Token;
import com.project.internship_backend.entities.User;
import com.project.internship_backend.exceptions.DataNotFoundException;
import com.project.internship_backend.exceptions.ExpiredTokenException;
import com.project.internship_backend.exceptions.PermissionDenyException;
import com.project.internship_backend.repositories.RoleRepository;
import com.project.internship_backend.repositories.TokenRepository;
import com.project.internship_backend.repositories.UserRepository;
import com.project.internship_backend.responses.user.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final JwtTokenUtils jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public User createUser( UserDTO userDTO) throws Exception {
        String phoneNumber = userDTO.getPhoneNumber();
        //Kiểm tra xem phone number này đã tồn tại chưa
        if(userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new DataIntegrityViolationException("This phone number already exist");
        }
        String email = userDTO.getEmail();
        //Kiểm tra xem email này đã tồn tại chưa
        if(userRepository.existsByEmail(email)) {
            throw new DataIntegrityViolationException("This email already exist");
        }
        Role role = roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() -> new DataNotFoundException("Role not found"));

        if(role.getName().toUpperCase().equals(Role.ADMIN)) {
            throw new PermissionDenyException("You cannot register a admin account");
        }
        //convert CustomerDTO => Customer
        User newUser = User.builder()
                .fullName(userDTO.getFullName())
                .email(userDTO.getEmail())
                .phoneNumber(userDTO.getPhoneNumber())
                .gender(userDTO.getGender())
                .dateOfBirth(userDTO.getDateOfBirth())
                .desiredRole(userDTO.getDesiredRole())
                .password(userDTO.getPassword())
                .build();

        newUser.setRole(role);

        String password = userDTO.getPassword();
        String  encodedPassword = passwordEncoder.encode(password);
        newUser.setPassword(encodedPassword);

        return userRepository.save(newUser);
    }

    @Override
    public String login(UserLoginDTO userLoginDTO) throws Exception {
        Optional<User> optionalCustomer = userRepository.findByEmail(userLoginDTO.getEmail());
        if (optionalCustomer.isEmpty()) {
            throw new DataNotFoundException("Sai email");
        }
        User existingUser = optionalCustomer.get();
        if (!existingUser.isActive()) {
            throw new DataNotFoundException("Tài khoản chưa được kích hoạt");
        }
        // Kiểm tra mật khẩu
        if (!passwordEncoder.matches(userLoginDTO.getPassword(), existingUser.getPassword())) {
            throw new DataNotFoundException("Sai mật khẩu");
        }
        // Tạo token xác thực
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userLoginDTO.getEmail(), userLoginDTO.getPassword(), existingUser.getAuthorities()
        );
        // Xác thực với Spring Security
        authenticationManager.authenticate(authenticationToken);
        // Trả về JWT token
        return jwtTokenUtil.generateToken(existingUser);
    }


    @Override
    public User getUserDetailsFromToken(String token) throws Exception {
        if(jwtTokenUtil.isTokenExpired(token)) {
            throw new ExpiredTokenException("Token is expired");
        }
        String subject = jwtTokenUtil.getSubject(token);
        Optional<User> user;
        user = userRepository.findByEmail(subject);
        return user.orElseThrow(() -> new Exception("User not found"));
    }

    @Transactional
    @Override
    public User updateUser(Long userId, UpdateUserDTO updateUserDTO) throws Exception {
        // Find the existing user by userId
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

// Check if the phone number is being changed and if it already exists for another user
        String newEmail = updateUserDTO.getEmail();
        if (!existingUser.getEmail().equals(newEmail) &&
                userRepository.existsByEmail(newEmail)) {
            throw new DataIntegrityViolationException("Email already exists");
        }

        // Check if the phone number is being changed and if it already exists for another user
        String newPhoneNumber = updateUserDTO.getPhoneNumber();
        if (!existingUser.getPhoneNumber().equals(newPhoneNumber) &&
                userRepository.existsByPhoneNumber(newPhoneNumber)) {
            throw new DataIntegrityViolationException("Phone number already exists");
        }

        // Update user information based on the DTO
        if (updateUserDTO.getFullName() != null) {
            existingUser.setFullName(updateUserDTO.getFullName());
        }

        if (updateUserDTO.getProfileImage() != null) {
            existingUser.setProfileImage(updateUserDTO.getProfileImage());
        }

        if (newPhoneNumber != null) {
            existingUser.setPhoneNumber(newPhoneNumber);
        }

        if (newEmail != null) {
            existingUser.setEmail(newEmail);
        }

        if (updateUserDTO.getGender() != null) {
            existingUser.setGender(updateUserDTO.getGender());
        }

        if (updateUserDTO.getDateOfBirth() != null) {
            existingUser.setDateOfBirth(updateUserDTO.getDateOfBirth());
        }

        // Update the password if it is provided in the DTO
        if (updateUserDTO.getPassword() != null
                && !updateUserDTO.getPassword().isEmpty()) {
            if(!updateUserDTO.getPassword().equals(updateUserDTO.getRetypePassword())) {
                throw new DataNotFoundException("Password and retype password not the same");
            }
            String newPassword = updateUserDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(newPassword);
            existingUser.setPassword(encodedPassword);
        }
        //existingUser.setRole(updatedRole);
        // Save the updated user
        return userRepository.save(existingUser);
    }



    @Override
    @Transactional
    public void blockOrEnable(Long userId, Boolean active) throws DataNotFoundException {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        existingUser.setActive(active);
        userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public void grantRole(Long userId, Long roleId) throws DataNotFoundException {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        Role existingRole = roleRepository.findById(roleId)
                .orElseThrow(() -> new DataNotFoundException("Role not found"));
        existingUser.setRole(existingRole);
        userRepository.save(existingUser);
    }

    @Override
    public Page<UserResponse> getALlUsers(String keyword, PageRequest pageRequest) {
        Page<User> usersPage;
        usersPage = userRepository.findAllUsers(keyword, pageRequest);
        return usersPage.map(UserResponse::fromUser);
    }

    @Override
    public User getUserDetailById(Long userId) throws DataNotFoundException {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isPresent()) {
            return optionalUser.get();
        }
        throw new DataNotFoundException("Cannot find report with id =" + userId);
    }

    @Override
    public void deleteUser(Long userId) throws DataNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy người dùng với ID: " + userId));
        userRepository.delete(user);
    }

    @Override
    public User getUserDetailsFromRefreshToken(String refreshToken) throws Exception {
        Token existingToken = tokenRepository.findByRefreshToken(refreshToken);
        return getUserDetailsFromToken(existingToken.getToken());
    }

    @Override
    @Transactional
    public void changeProfileImage(Long userId, String imageName) throws Exception {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        existingUser.setProfileImage(imageName);
        userRepository.save(existingUser);
    }

}
