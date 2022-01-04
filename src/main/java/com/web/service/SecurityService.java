package com.web.service;

import com.web.error.ErrorMessage;
import com.web.error.exception.InternalServerErrorException;
import com.web.error.exception.NotFoundException;
import com.web.filter.CustomAuthenticationToken;
import com.web.model.enumeration.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.web.util.SecurityConstants.USER_ID;

@Service
public class SecurityService {

    public void authorize(UUID userId) {
        if (!hasRequiredId(userId) && !hasCustomRole(UserRole.ROLE_ADMIN)) {
            throw new NotFoundException(ErrorMessage.NOT_FOUND, "user", userId);
        }
    }

    public void authorize(UUID userId, String name, Object id) {
        if (!hasRequiredId(userId) && !hasCustomRole(UserRole.ROLE_ADMIN)) {
            throw new NotFoundException(ErrorMessage.NOT_FOUND, name, id);
        }
    }

    public UUID getUserId() {
        return UUID.fromString(getCustomAuthentication().getClaims().get(USER_ID));
    }

    private boolean hasRequiredId(UUID userId) {
        return userId.toString().equals(getCustomAuthentication().getClaims().get(USER_ID));
    }

    public boolean hasCustomRole(UserRole userRole) {
        return getCustomAuthentication().getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(userRole.name()));
    }

    private CustomAuthenticationToken getCustomAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof CustomAuthenticationToken)) {
            throw new InternalServerErrorException(ErrorMessage.INTERNAL_SERVER_ERROR);
        }
        return (CustomAuthenticationToken) authentication;
    }
}
