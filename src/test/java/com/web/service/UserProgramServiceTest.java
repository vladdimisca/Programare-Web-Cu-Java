package com.web.service;

import com.web.error.exception.ConflictException;
import com.web.error.exception.ForbiddenException;
import com.web.error.exception.NotFoundException;
import com.web.model.AdmissionFile;
import com.web.model.ProgramOfStudy;
import com.web.model.User;
import com.web.model.UserProgram;
import com.web.model.enumeration.FinancingType;
import com.web.model.enumeration.UserRole;
import com.web.repository.UserProgramRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProgramServiceTest {

    private static final Long ID = 1L;
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PROGRAM_ID = UUID.randomUUID();

    @Mock
    private UserService userService;

    @Mock
    private ProgramOfStudyService programOfStudyService;

    @Mock
    private SecurityService securityService;

    @Mock
    private UserProgramRepository userProgramRepository;

    @InjectMocks
    private UserProgramService userProgramService;

    @Test
    @DisplayName("Create user-program pair - success")
    void create_success() {
        User user = getUser();
        UserProgram userProgram = getUserProgram();
        UserProgram savedUserProgram = getSavedUserProgram();

        when(securityService.getUserId()).thenReturn(USER_ID);
        when(userService.getById(USER_ID)).thenReturn(user);
        when(programOfStudyService.getById(PROGRAM_ID)).thenReturn(userProgram.getProgramOfStudy());
        when(userProgramRepository.existsByUserAndProgramOfStudy(user, userProgram.getProgramOfStudy()))
                .thenReturn(false);
        when(userProgramRepository.save(userProgram)).thenReturn(savedUserProgram);

        UserProgram resultedUserProgram = userProgramService.create(userProgram);

        assertNotNull(resultedUserProgram);
        assertEquals(savedUserProgram.getId(), resultedUserProgram.getId());
        assertEquals(savedUserProgram.getUser(), resultedUserProgram.getUser());
        assertEquals(savedUserProgram.getProgramOfStudy(), resultedUserProgram.getProgramOfStudy());
    }

    @Test
    @DisplayName("Create user-program pair - existing pair - failure")
    void create_existingPair_failure() {
        User user = getUser();
        UserProgram userProgram = getUserProgram();

        when(securityService.getUserId()).thenReturn(USER_ID);
        when(userService.getById(USER_ID)).thenReturn(user);
        when(programOfStudyService.getById(PROGRAM_ID)).thenReturn(userProgram.getProgramOfStudy());
        when(userProgramRepository.existsByUserAndProgramOfStudy(user, userProgram.getProgramOfStudy())).thenReturn(true);

        assertThrows(ConflictException.class, () -> userProgramService.create(userProgram));
    }

    @Test
    @DisplayName("Create user-program pair - forbidden role - failure")
    void create_forbiddenRole_failure() {
        User user = getUser();
        user.setRole(UserRole.ROLE_ADMIN);
        UserProgram userProgram = getUserProgram();
        userProgram.setUser(user);

        when(securityService.getUserId()).thenReturn(USER_ID);
        when(userService.getById(USER_ID)).thenReturn(user);
        when(programOfStudyService.getById(PROGRAM_ID)).thenReturn(userProgram.getProgramOfStudy());

        assertThrows(ForbiddenException.class, () -> userProgramService.create(userProgram));
    }

    @Test
    @DisplayName("Create user-program pair - submitted admission file - failure")
    void create_submittedAdmissionFile_failure() {
        User user = getUser();
        user.setAdmissionFile(new AdmissionFile());
        UserProgram userProgram = getUserProgram();
        userProgram.setUser(user);

        when(securityService.getUserId()).thenReturn(USER_ID);
        when(userService.getById(USER_ID)).thenReturn(user);

        assertThrows(ConflictException.class, () -> userProgramService.create(userProgram));
    }

    @Test
    @DisplayName("Update user-program pair - success")
    void update_success() {
        UserProgram userProgram = getSavedUserProgram();
        UserProgram updatedUserProgram = getSavedUserProgram();
        ProgramOfStudy programOfStudy = new ProgramOfStudy();
        programOfStudy.setName("new name");
        programOfStudy.setId(UUID.randomUUID());
        updatedUserProgram.setProgramOfStudy(programOfStudy);

        when(userProgramRepository.findById(ID)).thenReturn(Optional.of(userProgram));
        when(programOfStudyService.getById(programOfStudy.getId())).thenReturn(updatedUserProgram.getProgramOfStudy());
        when(securityService.getUserId()).thenReturn(USER_ID);
        when(userProgramRepository.existsByUserAndProgramOfStudy(any(), eq(updatedUserProgram.getProgramOfStudy())))
                .thenReturn(false);
        when(userProgramRepository.save(userProgram)).thenReturn(updatedUserProgram);

        UserProgram resultedUserProgram = userProgramService.updateById(ID, updatedUserProgram);

        assertNotNull(resultedUserProgram);
        assertEquals(updatedUserProgram.getId(), resultedUserProgram.getId());
        assertEquals(updatedUserProgram.getUser(), resultedUserProgram.getUser());
        assertEquals(updatedUserProgram.getProgramOfStudy(), resultedUserProgram.getProgramOfStudy());
    }

    @Test
    @DisplayName("Update user-program pair - submitted admission file - failure")
    void update_submittedAdmissionFile_failure() {
        UserProgram userProgram = getSavedUserProgram();
        userProgram.getUser().setAdmissionFile(new AdmissionFile());

        when(userProgramRepository.findById(ID)).thenReturn(Optional.of(userProgram));

        assertThrows(ConflictException.class, () -> userProgramService.updateById(ID, getSavedUserProgram()));
    }

    @Test
    @DisplayName("Get user-program pair by id - success")
    void getById_success() {
        UserProgram userProgram = getSavedUserProgram();

        when(userProgramRepository.findById(ID)).thenReturn(Optional.of(userProgram));

        UserProgram resultedUserProgram = userProgramService.getById(ID);

        assertNotNull(resultedUserProgram);
        assertEquals(userProgram.getId(), resultedUserProgram.getId());
        assertEquals(userProgram.getUser(), resultedUserProgram.getUser());
        assertEquals(userProgram.getProgramOfStudy(), resultedUserProgram.getProgramOfStudy());
    }

    @Test
    @DisplayName("Get user-program pair by id - not found - failure")
    void getById_notFound_failure() {
        when(userProgramRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userProgramService.getById(ID));
    }

    @Test
    @DisplayName("Get all user-program pairs - success")
    void getAll_success() {
        when(userProgramRepository.findAllByUserAndProgram(any(), any())).thenReturn(List.of(getSavedUserProgram()));
        when(securityService.hasCustomRole(UserRole.ROLE_STUDENT)).thenReturn(true);

        List<UserProgram> resultedUserPrograms = userProgramService.getAll(USER_ID, PROGRAM_ID);

        assertNotNull(resultedUserPrograms);
        assertEquals(1, resultedUserPrograms.size());
    }

    @Test
    @DisplayName("Submit grade - success")
    void submitGrade_success() {
        Integer grade = 10;
        UserProgram userProgram = getSavedUserProgram();
        UserProgram gradedUserProgram = getSavedUserProgram();
        gradedUserProgram.setGrade(grade);

        when(userProgramRepository.findById(ID)).thenReturn(Optional.of(userProgram));
        when(userProgramRepository.save(userProgram)).thenReturn(gradedUserProgram);

        UserProgram resultedUserProgram = userProgramService.submitGrade(ID, grade);

        assertNotNull(resultedUserProgram);
        assertEquals(gradedUserProgram.getId(), resultedUserProgram.getId());
        assertEquals(gradedUserProgram.getUser(), resultedUserProgram.getUser());
        assertEquals(gradedUserProgram.getProgramOfStudy(), resultedUserProgram.getProgramOfStudy());
    }

    @Test
    @DisplayName("Delete user-program pair by id - success")
    void delete_success() {
        UserProgram userProgram = getSavedUserProgram();

        when(userProgramRepository.findById(ID)).thenReturn(Optional.of(userProgram));

        userProgramService.deleteById(ID);

        verify(userProgramRepository, times(1)).delete(userProgram);
    }

    @Test
    @DisplayName("Delete user-program pair by id - submitted admission file - failure")
    void delete_submittedAdmissionFile_failure() {
        UserProgram userProgram = getSavedUserProgram();
        userProgram.getUser().setAdmissionFile(new AdmissionFile());

        when(userProgramRepository.findById(ID)).thenReturn(Optional.of(userProgram));

        assertThrows(ConflictException.class, () -> userProgramService.deleteById(ID));
    }

    private User getUser() {
        User user = new User();
        user.setEmail("dummy@gmail.com");
        user.setRole(UserRole.ROLE_STUDENT);
        user.setId(USER_ID);

        return user;
    }

    private ProgramOfStudy getProgramOfStudy() {
        ProgramOfStudy program = new ProgramOfStudy();
        program.setName("dummy name");
        program.setFinancingType(FinancingType.BUDGET);
        program.setId(PROGRAM_ID);

        return program;
    }

    private UserProgram getUserProgram() {
        ProgramOfStudy program = getProgramOfStudy();

        UserProgram userProgram = new UserProgram();
        userProgram.setProgramOfStudy(program);

        return userProgram;
    }

    private UserProgram getSavedUserProgram() {
        UserProgram userProgram = getUserProgram();
        userProgram.setUser(getUser());
        userProgram.setId(ID);

        return userProgram;
    }
}