package com.web.service;

import com.web.error.ErrorMessage;
import com.web.error.exception.ConflictException;
import com.web.error.exception.NotFoundException;
import com.web.model.AdmissionFile;
import com.web.model.User;
import com.web.model.UserInfo;
import com.web.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final UUID ID = UUID.randomUUID();
    private static final String EMAIL = "test@gmail.com";
    private static final String PASSWORD = "dummy_password";
    private static final Long USER_INFO_ID = 1L;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Create user - success")
    void create_success() {
        User user = getUser();
        User savedUser = getSavedUser();

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(savedUser);

        User resultedUser = userService.create(user);

        assertNotNull(resultedUser);
        assertEquals(savedUser.getEmail(), resultedUser.getEmail());
        assertEquals(savedUser.getPassword(), resultedUser.getPassword());
        assertEquals(savedUser.getId(), resultedUser.getId());
        assertEquals(savedUser.getCreatedAt(), resultedUser.getCreatedAt());
    }

    @Test
    @DisplayName("Create user - existing email - failure")
    void create_existingEmail_failure() {
        User user = getUser();

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.create(user));
    }

    @Test
    @DisplayName("Get user by id - success")
    void getById_success() {
        User savedUser = getSavedUser();

        when(userRepository.findById(ID)).thenReturn(Optional.of(savedUser));

        User resultedUser = userService.getById(ID);

        assertNotNull(resultedUser);
        assertEquals(savedUser.getEmail(), resultedUser.getEmail());
        assertEquals(savedUser.getPassword(), resultedUser.getPassword());
        assertEquals(savedUser.getId(), resultedUser.getId());
        assertEquals(savedUser.getCreatedAt(), resultedUser.getCreatedAt());
    }

    @Test
    @DisplayName("Get user by email - success")
    void getByEmail_success() {
        User savedUser = getSavedUser();

        when(userRepository.findByEmail(savedUser.getEmail())).thenReturn(Optional.of(savedUser));

        User resultedUser = userService.getByEmail(EMAIL);

        assertNotNull(resultedUser);
        assertEquals(savedUser.getEmail(), resultedUser.getEmail());
        assertEquals(savedUser.getPassword(), resultedUser.getPassword());
        assertEquals(savedUser.getId(), resultedUser.getId());
        assertEquals(savedUser.getCreatedAt(), resultedUser.getCreatedAt());
    }

    @Test
    @DisplayName("Get user by id - not found - failure")
    void getById_notFound_failure() {
        when(userRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getById(ID));
    }

    @Test
    @DisplayName("Get user by id - not authorized - failure")
    void getById_notAuthorized_failure() {
        doThrow(new NotFoundException(ErrorMessage.NOT_FOUND)).when(securityService).authorize(any());
        assertThrows(NotFoundException.class, () -> userService.getById(ID));
    }

    @Test
    @DisplayName("Update user - success")
    void update_success() {
        User user = getSavedUser();
        User updatedUser = getUpdatedUser();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(updatedUser.getEmail())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(updatedUser);

        User resultedUser = userService.update(user.getId(), updatedUser);

        assertNotNull(resultedUser);
        assertEquals(updatedUser.getEmail(), resultedUser.getEmail());
        assertEquals(updatedUser.getPassword(), resultedUser.getPassword());
        assertEquals(updatedUser.getId(), resultedUser.getId());
    }

    @Test
    @DisplayName("Update user - existing email - failure")
    void update_existingEmail_failure() {
        User user = getSavedUser();
        User updatedUser = getUpdatedUser();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(updatedUser.getEmail())).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.update(user.getId(), updatedUser));
    }

    @Test
    @DisplayName("Update user - submitted admission file - failure")
    void update_submittedAdmissionFile_failure() {
        User user = getSavedUser();
        user.setAdmissionFile(new AdmissionFile());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        assertThrows(ConflictException.class, () -> userService.update(user.getId(), getSavedUser()));
    }

    @Test
    @DisplayName("Get all users - success")
    void getAll_success() {
        when(userRepository.findAllByEmailAndNationality(any(), any())).thenReturn(List.of(getSavedUser()));

        List<User> users = userService.getAll(EMAIL, null);

        assertNotNull(users);
        assertEquals(1, users.size());
    }

    @Test
    @DisplayName("Delete user - success")
    void delete_success() {
        User user = getSavedUser();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userService.deleteById(user.getId());

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    @DisplayName("Delete user - submitted admission file - failure")
    void delete_submittedAdmissionFile_failure() {
        User user = getSavedUser();
        user.setAdmissionFile(new AdmissionFile());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        assertThrows(ConflictException.class, () -> userService.deleteById(user.getId()));
    }

    @Test
    @DisplayName("Populate user info - success")
    void populateUserInfo_success() {
        User user = getSavedUser();
        User userWithInfo = getSavedUser();
        userWithInfo.setUserInfo(getSavedUserInfo());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(userWithInfo);

        UserInfo resultedUserInfo = userService.populateUserInfoById(user.getId(), getUserInfo());

        assertNotNull(resultedUserInfo);
        assertEquals(userWithInfo.getUserInfo().getId(), resultedUserInfo.getId());
        assertEquals(userWithInfo.getUserInfo().getFirstName(), resultedUserInfo.getFirstName());
        assertEquals(userWithInfo.getUserInfo().getLastName(), resultedUserInfo.getLastName());
        assertEquals(userWithInfo.getUserInfo().getPhoneNumber(), resultedUserInfo.getPhoneNumber());
    }

    @Test
    @DisplayName("Populate user info - existing user info - failure")
    void populateUserInfo_existingUserInfo_failure() {
        User userWithInfo = getSavedUser();
        userWithInfo.setUserInfo(getSavedUserInfo());

        when(userRepository.findById(userWithInfo.getId())).thenReturn(Optional.of(userWithInfo));

        assertThrows(ConflictException.class, () -> userService.populateUserInfoById(userWithInfo.getId(), getUserInfo()));
    }

    @Test
    @DisplayName("Update user info - success")
    void updateUserInfo_success() {
        User userWithInfo = getSavedUser();
        userWithInfo.setUserInfo(getSavedUserInfo());

        User updatedUserWithInfo = getSavedUser();
        updatedUserWithInfo.setUserInfo(getUpdatedUserInfo());

        when(userRepository.findById(userWithInfo.getId())).thenReturn(Optional.of(userWithInfo));
        when(userRepository.save(userWithInfo)).thenReturn(updatedUserWithInfo);

        UserInfo resultedUserInfo = userService.updateUserInfoById(userWithInfo.getId(), userWithInfo.getUserInfo());

        assertNotNull(resultedUserInfo);
        assertEquals(updatedUserWithInfo.getUserInfo().getId(), resultedUserInfo.getId());
        assertEquals(updatedUserWithInfo.getUserInfo().getFirstName(), resultedUserInfo.getFirstName());
        assertEquals(updatedUserWithInfo.getUserInfo().getLastName(), resultedUserInfo.getLastName());
        assertEquals(updatedUserWithInfo.getUserInfo().getPhoneNumber(), resultedUserInfo.getPhoneNumber());
    }

    @Test
    @DisplayName("Update user info - submitted admission file - failure")
    void updateUserInfo_submittedAdmissionFile_failure() {
        User userWithInfo = getSavedUser();
        userWithInfo.setUserInfo(getSavedUserInfo());
        userWithInfo.setAdmissionFile(new AdmissionFile());

        when(userRepository.findById(userWithInfo.getId())).thenReturn(Optional.of(userWithInfo));

        assertThrows(ConflictException.class, () ->
                userService.updateUserInfoById(userWithInfo.getId(), userWithInfo.getUserInfo()));
    }

    @Test
    @DisplayName("Update user info - user without info - failure")
    void updateUserInfo_missingInfo_failure() {
        User userWithoutInfo = getSavedUser();

        when(userRepository.findById(ID)).thenReturn(Optional.of(userWithoutInfo));

        assertThrows(NotFoundException.class, () -> userService.updateUserInfoById(ID, getUserInfo()));
    }

    @Test
    @DisplayName("Delete user info - success")
    void deleteUserInfo_success() {
        User userWithInfo = getSavedUser();
        userWithInfo.setUserInfo(getSavedUserInfo());

        when(userRepository.findById(userWithInfo.getId())).thenReturn(Optional.of(userWithInfo));

        userService.deleteUserInfoById(userWithInfo.getId());

        verify(userRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Delete user info - submitted admission file - failure")
    void deleteUserInfo_submittedAdmissionFile_failure() {
        User userWithInfo = getSavedUser();
        userWithInfo.setUserInfo(getSavedUserInfo());
        userWithInfo.setAdmissionFile(new AdmissionFile());

        when(userRepository.findById(userWithInfo.getId())).thenReturn(Optional.of(userWithInfo));

        assertThrows(ConflictException.class, () -> userService.deleteUserInfoById(userWithInfo.getId()));
    }

    private User getUser() {
        User user = new User();
        user.setEmail(EMAIL);
        user.setPassword("abc");

        return user;
    }

    private User getSavedUser() {
        User savedUser = new User();
        savedUser.setEmail(EMAIL);
        savedUser.setPassword(PASSWORD);
        savedUser.setId(ID);
        savedUser.setCreatedAt(LocalDateTime.now());

        return savedUser;
    }

    private User getUpdatedUser() {
        User updatedUser = new User();
        updatedUser.setEmail("new@gmail.com");
        updatedUser.setPassword("new_pass");
        updatedUser.setId(ID);

        return updatedUser;
    }

    private UserInfo getUserInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.setFirstName("Test");
        userInfo.setLastName("Test");
        userInfo.setPhoneNumber("3423358812");

        return userInfo;
    }

    private UserInfo getSavedUserInfo() {
        UserInfo userInfo = getUserInfo();
        userInfo.setId(USER_INFO_ID);

        return userInfo;
    }

    private UserInfo getUpdatedUserInfo() {
        UserInfo userInfo = getSavedUserInfo();
        userInfo.setFirstName("new");

        return userInfo;
    }
}