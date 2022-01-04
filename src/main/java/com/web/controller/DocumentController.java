package com.web.controller;

import com.web.dto.DocumentDto;
import com.web.mapper.DocumentMapper;
import com.web.model.Document;
import com.web.model.enumeration.UserRole;
import com.web.service.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final DocumentMapper documentMapper;

    public DocumentController(DocumentService documentService, DocumentMapper documentMapper) {
        this.documentService = documentService;
        this.documentMapper = documentMapper;
    }

    @RolesAllowed({UserRole.Constants.STUDENT})
    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<DocumentDto> create(@RequestPart("identity-card") MultipartFile identityCard,
                                            @RequestPart("medical-certificate") MultipartFile medicalCertificate,
                                            @RequestPart("diploma") MultipartFile diploma) {
        Document document = documentService.create(identityCard, medicalCertificate, diploma);
        return ResponseEntity
                .created(URI.create("/api/documents/" + document.getId()))
                .body(documentMapper.mapToDto(document));
    }

    @RolesAllowed({UserRole.Constants.STUDENT})
    @PutMapping(value = "/{id}", consumes = { "multipart/form-data" })
    public ResponseEntity<DocumentDto> update(@PathVariable("id") Long id,
                                              @RequestPart("identity-card") MultipartFile identityCard,
                                              @RequestPart("medical-certificate") MultipartFile medicalCertificate,
                                              @RequestPart("diploma") MultipartFile diploma) {
        Document document = documentService.update(id, identityCard, medicalCertificate, diploma);
        return ResponseEntity.ok(documentMapper.mapToDto(document));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentDto> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(documentMapper.mapToDto(documentService.getById(id)));
    }

    @GetMapping
    @RolesAllowed(UserRole.Constants.ADMIN)
    public ResponseEntity<List<DocumentDto>> getAll() {
        return ResponseEntity.ok(documentService.getAll().stream().map(documentMapper::mapToDto).toList());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") Long id) {
        documentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
