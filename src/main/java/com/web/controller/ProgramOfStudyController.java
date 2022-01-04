package com.web.controller;

import com.web.dto.ProgramOfStudyDto;
import com.web.mapper.ProgramOfStudyMapper;
import com.web.model.ProgramOfStudy;
import com.web.model.enumeration.ProgramType;
import com.web.model.enumeration.UserRole;
import com.web.service.ProgramOfStudyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/programs-of-study")
public class ProgramOfStudyController {

    private final ProgramOfStudyService programOfStudyService;
    private final ProgramOfStudyMapper programOfStudyMapper;

    @Autowired
    public ProgramOfStudyController(ProgramOfStudyService programOfStudyService, ProgramOfStudyMapper programOfStudyMapper) {
        this.programOfStudyService = programOfStudyService;
        this.programOfStudyMapper = programOfStudyMapper;
    }

    @PostMapping
    @RolesAllowed(UserRole.Constants.ADMIN)
    public ResponseEntity<ProgramOfStudyDto> create(@Valid @RequestBody ProgramOfStudyDto programOfStudyDto) {
        ProgramOfStudy programOfStudy = programOfStudyService.create(programOfStudyMapper.mapToEntity(programOfStudyDto));
        return ResponseEntity
                .created(URI.create("/api/programs-of-study/" + programOfStudy.getId()))
                .body(programOfStudyMapper.mapToDto(programOfStudy));
    }

    @PutMapping("/{id}")
    @RolesAllowed(UserRole.Constants.ADMIN)
    public ResponseEntity<ProgramOfStudyDto> update(@PathVariable("id") UUID id,
                                                    @Valid @RequestBody ProgramOfStudyDto programOfStudyDto) {
        ProgramOfStudy programOfStudy = programOfStudyService
                .update(id, programOfStudyMapper.mapToEntity(programOfStudyDto));

        return ResponseEntity.ok(programOfStudyMapper.mapToDto(programOfStudy));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProgramOfStudyDto> getById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(programOfStudyMapper.mapToDto(programOfStudyService.getById(id)));
    }

    @GetMapping
    public ResponseEntity<List<ProgramOfStudyDto>> getAll(@RequestParam(name = "type", required = false) ProgramType type) {
        List<ProgramOfStudy> programsOfStudy = programOfStudyService.getAll(type);
        return ResponseEntity.ok(programsOfStudy.stream().map(programOfStudyMapper::mapToDto).toList());
    }

    @DeleteMapping("/{id}")
    @RolesAllowed(UserRole.Constants.ADMIN)
    public ResponseEntity<?> deleteById(@PathVariable("id") UUID id) {
        programOfStudyService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
