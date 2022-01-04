package com.web.service;

import com.web.error.ErrorMessage;
import com.web.error.exception.ConflictException;
import com.web.error.exception.ForbiddenException;
import com.web.error.exception.NotFoundException;
import com.web.model.AdmissionFile;
import com.web.model.User;
import com.web.model.enumeration.AdmissionFileStatus;
import com.web.repository.AdmissionFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AdmissionFileService {

    private final AdmissionFileRepository admissionFileRepository;
    private final UserService userService;
    private final SecurityService securityService;

    @Autowired
    public AdmissionFileService(AdmissionFileRepository admissionFileRepository,
                                UserService userService,
                                SecurityService securityService) {
        this.admissionFileRepository = admissionFileRepository;
        this.userService = userService;
        this.securityService = securityService;
    }

    @Transactional
    public AdmissionFile submit() {
        User user = userService.getById(securityService.getUserId());

        if (user.getAdmissionFile() != null) {
            throw new ConflictException(ErrorMessage.ALREADY_EXISTS, "Admission file");
        }

        checkIfUploadIsAllowed(user);

        AdmissionFile admissionFile = AdmissionFile.builder()
                .status(AdmissionFileStatus.PENDING)
                .submittedAt(LocalDateTime.now())
                .user(user)
                .build();

        return admissionFileRepository.save(admissionFile);
    }

    public AdmissionFile resubmit(Long id) {
        User user = userService.getById(securityService.getUserId());
        AdmissionFile admissionFile = getById(id);

        if (admissionFile.getStatus().equals(AdmissionFileStatus.VALID)) {
            throw new ForbiddenException(ErrorMessage.FORBIDDEN, "It is not possible to resubmit a valid admission file");
        }

        checkIfUploadIsAllowed(user);

        admissionFile.setStatus(AdmissionFileStatus.PENDING);
        admissionFile.setSubmittedAt(LocalDateTime.now());

        return admissionFileRepository.save(admissionFile);
    }

    public AdmissionFile getById(Long id) {
        AdmissionFile admissionFile = admissionFileRepository.findById(id).orElseThrow(() ->
                new NotFoundException(ErrorMessage.NOT_FOUND, "admission file", id));

        securityService.authorize(admissionFile.getUser().getId());
        return admissionFile;
    }

    public List<AdmissionFile> getAll(UUID userId, AdmissionFileStatus status) {
        return admissionFileRepository.findAllByUserIdAndStatus(userId, status);
    }

    @Transactional
    public void deleteById(Long id) {
        admissionFileRepository.delete(getById(id));
    }

    @Transactional
    public AdmissionFile validate(Long id, AdmissionFileStatus status) {
        AdmissionFile admissionFile = getById(id);
        admissionFile.setStatus(status);

        return admissionFileRepository.save(admissionFile);
    }

    private void checkIfUploadIsAllowed(User user) {
        if (user.getUserInfo() == null || user.getDocument() == null || user.getUserPrograms().isEmpty()) {
            throw new ForbiddenException(ErrorMessage.FORBIDDEN, "You need to upload the personal info/documents" +
                    " and apply to at least one program of study before submitting the admission file");
        }
    }
}
