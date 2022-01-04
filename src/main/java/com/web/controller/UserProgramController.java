package com.web.controller;

import com.web.dto.UserProgramDto;
import com.web.mapper.UserProgramMapper;
import com.web.model.UserProgram;
import com.web.model.enumeration.UserRole;
import com.web.service.UserProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/users-programs")
public class UserProgramController {

    private final UserProgramService userProgramService;
    private final UserProgramMapper userProgramMapper;

    @Autowired
    public UserProgramController(UserProgramService userProgramService, UserProgramMapper userProgramMapper) {
        this.userProgramService = userProgramService;
        this.userProgramMapper = userProgramMapper;
    }

    @PostMapping
    public ResponseEntity<UserProgramDto> create(@Valid @RequestBody UserProgramDto userProgramDto) {
        UserProgram userProgram = userProgramService.create(userProgramMapper.mapToEntity(userProgramDto));
        return ResponseEntity
                .created(URI.create("/api/user-programs/" + userProgram.getId()))
                .body(userProgramMapper.mapToDto(userProgram));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserProgramDto> update(@PathVariable("id") Long id,
                                                 @Valid @RequestBody UserProgramDto userProgramDto) {
        UserProgram userProgram = userProgramService.updateById(id, userProgramMapper.mapToEntity(userProgramDto));
        return ResponseEntity.ok(userProgramMapper.mapToDto(userProgram));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProgramDto> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userProgramMapper.mapToDto(userProgramService.getById(id)));
    }

    @GetMapping
    public ResponseEntity<List<UserProgramDto>> getAll(@RequestParam(name = "userId", required = false) UUID userId,
                                                       @RequestParam(name = "programId", required = false) UUID programId) {
        List<UserProgram> userPrograms = userProgramService.getAll(userId, programId);
        return ResponseEntity.ok(userPrograms.stream().map(userProgramMapper::mapToDto).toList());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") Long id) {
        userProgramService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    @RolesAllowed(UserRole.Constants.ADMIN)
    public ResponseEntity<UserProgramDto> submitGrade(@PathVariable("id") Long id,
                                                      @RequestParam("grade") @Min(1) @Max(10) Integer grade) {
        return ResponseEntity.ok(userProgramMapper.mapToDto(userProgramService.submitGrade(id, grade)));
    }
}
