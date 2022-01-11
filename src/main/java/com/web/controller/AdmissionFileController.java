package com.web.controller;

import com.web.dto.AdmissionFileDto;
import com.web.mapper.AdmissionFileMapper;
import com.web.model.AdmissionFile;
import com.web.model.enumeration.AdmissionFileStatus;
import com.web.model.enumeration.UserRole;
import com.web.service.AdmissionFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admission-files")
public class AdmissionFileController {

    private final AdmissionFileService admissionFileService;
    private final AdmissionFileMapper admissionFileMapper;

    @Autowired
    public AdmissionFileController(AdmissionFileService admissionFileService, AdmissionFileMapper admissionFileMapper) {
        this.admissionFileService = admissionFileService;
        this.admissionFileMapper = admissionFileMapper;
    }

    @PostMapping
    @RolesAllowed(UserRole.Constants.STUDENT)
    public ResponseEntity<AdmissionFileDto> submit() {
        AdmissionFile admissionFile = admissionFileService.submit();
        return ResponseEntity
                .created(URI.create("/api/admission-files/" + admissionFile.getId()))
                .body(admissionFileMapper.mapToDto(admissionFile));
    }

    @PutMapping("/{id}")
    @RolesAllowed(UserRole.Constants.STUDENT)
    public ResponseEntity<AdmissionFileDto> resubmit(@PathVariable("id") Long id) {
        AdmissionFile admissionFile = admissionFileService.resubmit(id);
        return ResponseEntity.ok(admissionFileMapper.mapToDto(admissionFile));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdmissionFileDto> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(admissionFileMapper.mapToDto(admissionFileService.getById(id)));
    }

    @GetMapping
    @RolesAllowed(UserRole.Constants.ADMIN)
    public ResponseEntity<List<AdmissionFileDto>> getAll(
                                         @RequestParam(value = "userId", required = false) UUID userId,
                                         @RequestParam(value = "status", required = false) AdmissionFileStatus status) {
        List<AdmissionFile> admissionFiles = admissionFileService.getAll(userId, status);
        return ResponseEntity.ok(admissionFiles.stream().map(admissionFileMapper::mapToDto).toList());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") Long id) {
        admissionFileService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    @RolesAllowed(UserRole.Constants.ADMIN)
    public ResponseEntity<AdmissionFileDto> validate(@PathVariable("id") Long id,
                                                     @RequestParam("status") AdmissionFileStatus status) {
        return ResponseEntity.ok(admissionFileMapper.mapToDto(admissionFileService.validate(id, status)));
    }
}
