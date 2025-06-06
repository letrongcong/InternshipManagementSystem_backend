package com.project.internship_backend.services.token;

import com.project.internship_backend.entities.Token;
import com.project.internship_backend.entities.User;

public interface ITokenService {
    Token addToken(User user, String token, boolean isMobileDevice);
    Token refreshToken(String refreshToken, User user) throws Exception;
}
