package com.web.service;

import com.web.error.ErrorMessage;
import com.web.error.exception.ForbiddenException;
import com.web.error.exception.ConflictException;
import com.web.error.exception.NotFoundException;
import com.web.model.User;
import com.web.model.UserProgram;
import com.web.model.enumeration.UserRole;
import com.web.repository.UserProgramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class UserProgramService {

    private final UserProgramRepository userProgramRepository;
    private final UserService userService;
    private final ProgramOfStudyService programOfStudyService;
    private final SecurityService securityService;

    @Autowired
    public UserProgramService(UserProgramRepository userProgramRepository,
                              UserService userService,
                              ProgramOfStudyService programOfStudyService,
                              SecurityService securityService) {
        this.userProgramRepository = userProgramRepository;
        this.userService = userService;
        this.programOfStudyService = programOfStudyService;
        this.securityService = securityService;
    }

    @Transactional
    public UserProgram create(UserProgram userProgram) {
        UUID userId = securityService.getUserId();
        securityService.authorize(userId);

        userProgram.setUser(userService.getById(userId));
        userProgram.setProgramOfStudy(programOfStudyService.getById(userProgram.getProgramOfStudy().getId()));

        checkNotSubmitted(userProgram);
        checkRole(userProgram.getUser().getRole());
        checkStudentProgramPair(userProgram);

        return userProgramRepository.save(userProgram);
    }

    @Transactional
    public UserProgram updateById(Long id, UserProgram userProgram) {
        UserProgram existingUserProgram = getById(id);

        checkNotSubmitted(existingUserProgram);
        if (!userProgram.getProgramOfStudy().getId().equals(existingUserProgram.getProgramOfStudy().getId())) {
            userProgram.setUser(User.builder().id(securityService.getUserId()).build());
            checkStudentProgramPair(userProgram);
        }

        existingUserProgram.setProgramOfStudy(programOfStudyService.getById(userProgram.getProgramOfStudy().getId()));
        checkRole(existingUserProgram.getUser().getRole());

        return userProgramRepository.save(existingUserProgram);
    }

    public UserProgram getById(Long id) {
        UserProgram userProgram = userProgramRepository.findById(id).orElseThrow(() ->
                new NotFoundException(ErrorMessage.NOT_FOUND, "student-program pair", id));

        securityService.authorize(userProgram.getUser().getId(), "student-program pair", id);
        return userProgram;
    }

    public List<UserProgram> getAll(UUID userId, UUID programId) {
        if (securityService.hasCustomRole(UserRole.ROLE_STUDENT)) {
            userId = securityService.getUserId();
        }
        return userProgramRepository.findAllByUserAndProgram(userId, programId);
    }

    @Transactional
    public void deleteById(Long id) {
        UserProgram userProgram = getById(id);
        checkNotSubmitted(userProgram);

        userProgramRepository.delete(userProgram);
    }

    @Transactional
    public UserProgram submitGrade(Long id, Integer grade) {
        UserProgram userProgram = getById(id);
        userProgram.setGrade(grade);

        return userProgramRepository.save(userProgram);
    }

    private void checkRole(UserRole userRole) {
        if (!userRole.equals(UserRole.ROLE_STUDENT)) {
            throw new ForbiddenException(ErrorMessage.FORBIDDEN, "Only students can apply to programs of study");
        }
    }

    private void checkStudentProgramPair(UserProgram userProgram) {
        if (userProgramRepository.existsByUserAndProgramOfStudy(userProgram.getUser(), userProgram.getProgramOfStudy())) {
            throw new ConflictException(ErrorMessage.ALREADY_EXISTS, "Student-program pair");
        }
    }

    private void checkNotSubmitted(UserProgram userProgram) {
        if (userProgram.getUser().getAdmissionFile() != null) {
            throw new ConflictException(ErrorMessage.ADMISSION_FILE_ALREADY_SUBMITTED);
        }
    }

}
