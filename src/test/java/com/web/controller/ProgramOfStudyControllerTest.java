package com.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.dto.ProgramOfStudyDto;
import com.web.mapper.ProgramOfStudyMapper;
import com.web.model.ProgramOfStudy;
import com.web.model.enumeration.FinancingType;
import com.web.model.enumeration.ProgramType;
import com.web.repository.UserRepository;
import com.web.service.ProgramOfStudyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProgramOfStudyController.class)
class ProgramOfStudyControllerTest {

    private static final UUID ID = UUID.randomUUID();

    @MockBean
    private ProgramOfStudyService programOfStudyService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProgramOfStudyMapper programOfStudyMapper;

    @Test
    @DisplayName("Create program - success")
    @WithMockUser(roles = {"ADMIN"})
    void createProgram_success() throws Exception {
        ProgramOfStudyDto programOfStudyDto = getProgramOfStudyDto(null);
        ProgramOfStudyDto savedProgramOfStudyDto = getProgramOfStudyDto(ID);
        ProgramOfStudy programOfStudy = getProgramOfStudy();
        ProgramOfStudy savedProgramOfStudy = getSavedProgramOfStudy();

        when(programOfStudyMapper.mapToEntity(programOfStudyDto)).thenReturn(programOfStudy);
        when(programOfStudyService.create(programOfStudy)).thenReturn(savedProgramOfStudy);
        when(programOfStudyMapper.mapToDto(savedProgramOfStudy)).thenReturn(savedProgramOfStudyDto);

        mockMvc.perform(post("/api/programs-of-study")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(programOfStudyDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(savedProgramOfStudyDto.id().toString())));
    }

    @Test
    @DisplayName("Update program - success")
    @WithMockUser(roles = {"ADMIN"})
    void updateProgram_success() throws Exception {
        ProgramOfStudyDto programOfStudyDto = getProgramOfStudyDto(null);
        ProgramOfStudyDto updatedProgramOfStudyDto = getProgramOfStudyDto(ID);
        ProgramOfStudy programOfStudy = getProgramOfStudy();
        ProgramOfStudy updatedProgramOfStudy = getSavedProgramOfStudy();

        when(programOfStudyMapper.mapToEntity(programOfStudyDto)).thenReturn(programOfStudy);
        when(programOfStudyService.update(ID, programOfStudy)).thenReturn(updatedProgramOfStudy);
        when(programOfStudyMapper.mapToDto(updatedProgramOfStudy)).thenReturn(updatedProgramOfStudyDto);

        mockMvc.perform(put("/api/programs-of-study/" + ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(programOfStudyDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedProgramOfStudyDto.id().toString())));
    }

    @Test
    @DisplayName("Get program - success")
    @WithMockUser(roles = {"ADMIN"})
    void getProgram_success() throws Exception {
        ProgramOfStudy programOfStudy = getSavedProgramOfStudy();
        ProgramOfStudyDto programOfStudyDto = getProgramOfStudyDto(ID);

        when(programOfStudyService.getById(ID)).thenReturn(programOfStudy);
        when(programOfStudyMapper.mapToDto(programOfStudy)).thenReturn(programOfStudyDto);

        mockMvc.perform(get("/api/programs-of-study/" + ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(programOfStudyDto.id().toString())));
    }

    @Test
    @DisplayName("Get all programs - success")
    @WithMockUser(roles = {"ADMIN"})
    void getAllPrograms_success() throws Exception {
        ProgramOfStudy programOfStudy = getSavedProgramOfStudy();
        ProgramOfStudyDto programOfStudyDto = getProgramOfStudyDto(ID);

        when(programOfStudyService.getAll(null)).thenReturn(List.of(programOfStudy));
        when(programOfStudyMapper.mapToDto(programOfStudy)).thenReturn(programOfStudyDto);

        mockMvc.perform(get("/api/programs-of-study"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(programOfStudyDto.id().toString())));
    }

    @Test
    @DisplayName("Delete program - success")
    @WithMockUser(roles = {"ADMIN"})
    void deleteProgram_success() throws Exception {
        mockMvc.perform(delete("/api/programs-of-study/" + ID))
                .andExpect(status().isNoContent());

        verify(programOfStudyService, times(1)).deleteById(ID);
    }

    private ProgramOfStudyDto getProgramOfStudyDto(UUID uuid) {
        return new ProgramOfStudyDto(
                uuid,
                "dummy",
                ProgramType.BACHELORS_DEGREE,
                4,
                100,
                FinancingType.BUDGET
        );
    }

    private ProgramOfStudy getProgramOfStudy() {
        ProgramOfStudy programOfStudy = new ProgramOfStudy();
        programOfStudy.setName("dummy");
        programOfStudy.setNumberOfStudents(100);
        programOfStudy.setNumberOfYears(4);
        programOfStudy.setFinancingType(FinancingType.BUDGET);
        programOfStudy.setType(ProgramType.BACHELORS_DEGREE);

        return programOfStudy;
    }

    private ProgramOfStudy getSavedProgramOfStudy() {
        ProgramOfStudy program = getProgramOfStudy();
        program.setId(ID);

        return program;
    }
}