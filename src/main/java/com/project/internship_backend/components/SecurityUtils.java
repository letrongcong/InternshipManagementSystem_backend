package com.project.internship_backend.components;

import com.project.internship_backend.entities.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null &&
                authentication.getPrincipal() instanceof User selectedUser) {
            if(!selectedUser.isAccountNonExpired() ) {
                return null;
            }
            return (User) authentication.getPrincipal();
        }
        return null;
    }
}
