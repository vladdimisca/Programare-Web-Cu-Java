package com.web.service;

import com.web.error.exception.ConflictException;
import com.web.error.exception.ForbiddenException;
import com.web.error.exception.NotFoundException;
import com.web.model.*;
import com.web.model.enumeration.AdmissionFileStatus;
import com.web.model.enumeration.UserRole;
import com.web.repository.AdmissionFileRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdmissionFileServiceTest {

    private static final Long ID = 1L;
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private AdmissionFileRepository admissionFileRepository;

    @Mock
    private UserService userService;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private AdmissionFileService admissionFileService;

    @Test
    @DisplayName("Submit admission file - success")
    void submit_success() {
        User user = getUser();
        AdmissionFile admissionFile = getAdmissionFile();

        when(securityService.getUserId()).thenReturn(USER_ID);
        when(userService.getById(USER_ID)).thenReturn(user);
        when(admissionFileRepository.save(any())).thenReturn(admissionFile);

        AdmissionFile resultedAdmissionFile = admissionFileService.submit();

        assertNotNull(resultedAdmissionFile);
        assertEquals(admissionFile.getId(), resultedAdmissionFile.getId());
        assertEquals(admissionFile.getStatus(), resultedAdmissionFile.getStatus());
        assertEquals(admissionFile.getSubmittedAt(), resultedAdmissionFile.getSubmittedAt());
    }

    @Test
    @DisplayName("Submit admission file - already submitted - failure")
    void submit_alreadySubmitted_failure() {
        User user = getUser();
        AdmissionFile admissionFile = getAdmissionFile();
        user.setAdmissionFile(admissionFile);

        when(securityService.getUserId()).thenReturn(USER_ID);
        when(userService.getById(USER_ID)).thenReturn(user);

        assertThrows(ConflictException.class, () -> admissionFileService.submit());
    }

    @Test
    @DisplayName("Submit admission file - not allowed - failure")
    void submit_notAllowed_failure() {
        User user = getUser();
        user.setDocument(null);

        when(securityService.getUserId()).thenReturn(USER_ID);
        when(userService.getById(USER_ID)).thenReturn(user);

        assertThrows(ForbiddenException.class, () -> admissionFileService.submit());
    }

    @Test
    @DisplayName("Resubmit admission file - success")
    void resubmit_success() {
        User user = getUser();
        AdmissionFile admissionFile = getAdmissionFile();
        admissionFile.setStatus(AdmissionFileStatus.INVALID);
        admissionFile.setUser(user);
        AdmissionFile resubmittedFile = getAdmissionFile();

        when(securityService.getUserId()).thenReturn(USER_ID);
        when(userService.getById(USER_ID)).thenReturn(user);
        when(admissionFileRepository.findById(ID)).thenReturn(Optional.of(admissionFile));
        when(admissionFileRepository.save(admissionFile)).thenReturn(resubmittedFile);

        AdmissionFile resultedAdmissionFile = admissionFileService.resubmit(ID);

        assertNotNull(resultedAdmissionFile);
        assertEquals(admissionFile.getId(), resultedAdmissionFile.getId());
        assertEquals(admissionFile.getStatus(), resultedAdmissionFile.getStatus());
        assertNotEquals(admissionFile.getSubmittedAt(), resultedAdmissionFile.getSubmittedAt());
    }

    @Test
    @DisplayName("Resubmit admission file - already valid - success")
    void resubmit_alreadyValid_failure() {
        User user = getUser();
        AdmissionFile admissionFile = getAdmissionFile();
        admissionFile.setStatus(AdmissionFileStatus.VALID);
        admissionFile.setUser(user);

        when(securityService.getUserId()).thenReturn(USER_ID);
        when(userService.getById(USER_ID)).thenReturn(user);
        when(admissionFileRepository.findById(ID)).thenReturn(Optional.of(admissionFile));

        assertThrows(ForbiddenException.class, () -> admissionFileService.resubmit(ID));
    }

    @Test
    @DisplayName("Get admission file by id - success")
    void getById_success() {
        AdmissionFile admissionFile = getAdmissionFile();
        admissionFile.setUser(getUser());

        when(admissionFileRepository.findById(ID)).thenReturn(Optional.of(admissionFile));

        AdmissionFile resultedAdmissionFile = admissionFileService.getById(ID);

        assertNotNull(resultedAdmissionFile);
        assertEquals(admissionFile.getId(), resultedAdmissionFile.getId());
        assertEquals(admissionFile.getStatus(), resultedAdmissionFile.getStatus());
        assertEquals(admissionFile.getSubmittedAt(), resultedAdmissionFile.getSubmittedAt());
    }

    @Test
    @DisplayName("Get admission file by id - not found - failure")
    void getById_notFound_failure() {
        when(admissionFileRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> admissionFileService.getById(ID));
    }

    @Test
    @DisplayName("Get admission files - success")
    void getAll_success() {
        when(admissionFileRepository.findAllByUserIdAndStatus(eq(USER_ID), any())).thenReturn(List.of(getAdmissionFile()));

        List<AdmissionFile> admissionFiles = admissionFileService.getAll(USER_ID, AdmissionFileStatus.PENDING);

        assertNotNull(admissionFiles);
        assertEquals(1, admissionFiles.size());
    }

    @Test
    @DisplayName("Delete admission file - success")
    void delete_success() {
        AdmissionFile admissionFile = getAdmissionFile();
        admissionFile.setUser(getUser());

        when(admissionFileRepository.findById(ID)).thenReturn(Optional.of(admissionFile));

        admissionFileService.deleteById(ID);

        verify(admissionFileRepository, times(1)).delete(admissionFile);
    }

    @Test
    @DisplayName("Validate admission file - success")
    void validate_success() {
        AdmissionFile admissionFile = getAdmissionFile();
        admissionFile.setUser(getUser());
        AdmissionFile validatedAdmissionFile = getAdmissionFile();
        admissionFile.setStatus(AdmissionFileStatus.VALID);

        when(admissionFileRepository.findById(ID)).thenReturn(Optional.of(admissionFile));
        when(admissionFileRepository.save(admissionFile)).thenReturn(validatedAdmissionFile);

        AdmissionFile resultedAdmissionFile = admissionFileService.validate(ID, AdmissionFileStatus.VALID);

        assertNotNull(resultedAdmissionFile);
        assertEquals(validatedAdmissionFile.getId(), resultedAdmissionFile.getId());
        assertEquals(validatedAdmissionFile.getStatus(), resultedAdmissionFile.getStatus());
        assertEquals(validatedAdmissionFile.getSubmittedAt(), resultedAdmissionFile.getSubmittedAt());
    }

    private User getUser() {
        User user = new User();
        user.setEmail("dummy@gmail.com");
        user.setRole(UserRole.ROLE_STUDENT);
        user.setId(USER_ID);

        user.setDocument(new Document());
        user.setUserInfo(new UserInfo());
        user.setUserPrograms(List.of(new UserProgram()));

        return user;
    }

    private AdmissionFile getAdmissionFile() {
        AdmissionFile admissionFile = new AdmissionFile();
        admissionFile.setStatus(AdmissionFileStatus.PENDING);
        admissionFile.setSubmittedAt(LocalDateTime.now());
        admissionFile.setId(ID);

        return admissionFile;
    }
}