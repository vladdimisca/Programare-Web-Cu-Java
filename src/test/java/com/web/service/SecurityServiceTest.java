package com.web.service;

import com.web.error.exception.InternalServerErrorException;
import com.web.error.exception.NotFoundException;
import com.web.filter.CustomAuthenticationToken;
import com.web.model.enumeration.UserRole;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {

    private static final UUID USER_ID = UUID.randomUUID();

    @InjectMocks
    private SecurityService securityService;

    @BeforeAll
    static void setup() {
        Principal principal = () -> "Test";
        List<GrantedAuthority> authorities = List.of(UserRole.ROLE_STUDENT::name);
        Map<String, String> claims = new HashMap<>();
        claims.put("USER_ID", USER_ID.toString());

        var authentication = new CustomAuthenticationToken(principal, claims, authorities);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Authorize - success")
    void authorize_success() {
        assertDoesNotThrow(() -> securityService.authorize(USER_ID));
        assertDoesNotThrow(() -> securityService.authorize(USER_ID, "name", 1L));
    }

    @Test
    @DisplayName("Authorize - failure")
    void authorize_failure() {
        assertThrows(NotFoundException.class, () -> securityService.authorize(UUID.randomUUID()));
        assertThrows(NotFoundException.class, () -> securityService.authorize(UUID.randomUUID(), "name", 1L));
    }

    @Test
    @DisplayName("Get user id - success")
    void getUserId_success() {
        assertEquals(USER_ID, securityService.getUserId());
    }

    @Test
    @DisplayName("Has custom role - success")
    void hasCustomRole_success() {
        assertTrue(securityService.hasCustomRole(UserRole.ROLE_STUDENT));
    }

    @Test
    @DisplayName("Has custom role - null authentication - failure")
    void hasCustomRole_nullAuthentication_success() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        SecurityContextHolder.setContext(mock(SecurityContext.class));

        assertThrows(InternalServerErrorException.class, () -> securityService.hasCustomRole(UserRole.ROLE_STUDENT));

        SecurityContextHolder.setContext(securityContext);
    }
}