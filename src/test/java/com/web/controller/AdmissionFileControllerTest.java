package com.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.dto.AdmissionFileDto;
import com.web.mapper.AdmissionFileMapper;
import com.web.model.AdmissionFile;
import com.web.model.User;
import com.web.model.enumeration.AdmissionFileStatus;
import com.web.repository.UserRepository;
import com.web.service.AdmissionFileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdmissionFileController.class)
class AdmissionFileControllerTest {

    private static final Long ID = 1L;
    private static final UUID USER_ID = UUID.randomUUID();

    @MockBean
    private AdmissionFileService admissionFileService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdmissionFileMapper admissionFileMapper;

    @Test
    @DisplayName("Submit admission file - success")
    @WithMockUser(roles = {"STUDENT"})
    void submitAdmissionFile_success() throws Exception {
        AdmissionFile admissionFile = getAdmissionFile();
        AdmissionFileDto admissionFileDto = getAdmissionFileDto();

        when(admissionFileService.submit()).thenReturn(admissionFile);
        when(admissionFileMapper.mapToDto(admissionFile)).thenReturn(admissionFileDto);

        mockMvc.perform(post("/api/admission-files"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(admissionFileDto.id()));
    }

    @Test
    @DisplayName("Resubmit admission file - success")
    @WithMockUser(roles = {"STUDENT"})
    void resubmitAdmissionFile_success() throws Exception {
        AdmissionFile admissionFile = getAdmissionFile();
        AdmissionFileDto admissionFileDto = getAdmissionFileDto();

        when(admissionFileService.resubmit(ID)).thenReturn(admissionFile);
        when(admissionFileMapper.mapToDto(admissionFile)).thenReturn(admissionFileDto);

        mockMvc.perform(put("/api/admission-files/" + ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(admissionFileDto.id()));
    }

    @Test
    @DisplayName("Get admission file - success")
    @WithMockUser(roles = {"STUDENT"})
    void getAdmissionFile_success() throws Exception {
        AdmissionFile admissionFile = getAdmissionFile();
        AdmissionFileDto admissionFileDto = getAdmissionFileDto();

        when(admissionFileService.getById(ID)).thenReturn(admissionFile);
        when(admissionFileMapper.mapToDto(admissionFile)).thenReturn(admissionFileDto);

        mockMvc.perform(get("/api/admission-files/" + ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(admissionFileDto.id()));
    }

    @Test
    @DisplayName("Get all admission files - success")
    @WithMockUser(roles = {"ADMIN"})
    void getAllAdmissionFiles_success() throws Exception {
        AdmissionFile admissionFile = getAdmissionFile();
        AdmissionFileDto admissionFileDto = getAdmissionFileDto();

        when(admissionFileService.getAll(null, null)).thenReturn(List.of(admissionFile));
        when(admissionFileMapper.mapToDto(admissionFile)).thenReturn(admissionFileDto);

        mockMvc.perform(get("/api/admission-files"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(admissionFileDto.id()));
    }

    @Test
    @DisplayName("Delete admission file - success")
    @WithMockUser(roles = {"STUDENT"})
    void deleteAdmissionFile_success() throws Exception {
        mockMvc.perform(delete("/api/admission-files/" + ID))
                .andExpect(status().isNoContent());

        verify(admissionFileService, times(1)).deleteById(ID);
    }

    @Test
    @DisplayName("Validate admission file - success")
    @WithMockUser(roles = {"ADMIN"})
    void validateAdmissionFile_success() throws Exception {
        AdmissionFile admissionFile = getAdmissionFile();
        AdmissionFileDto admissionFileDto = getAdmissionFileDto();

        when(admissionFileService.validate(ID, AdmissionFileStatus.VALID)).thenReturn(admissionFile);
        when(admissionFileMapper.mapToDto(admissionFile)).thenReturn(admissionFileDto);

        mockMvc.perform(patch("/api/admission-files/" + ID + "?status=VALID"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(admissionFileDto.id()));
    }

    private AdmissionFile getAdmissionFile() {
        User user = new User();
        user.setId(USER_ID);

        AdmissionFile admissionFile = new AdmissionFile();
        admissionFile.setId(ID);
        admissionFile.setStatus(AdmissionFileStatus.PENDING);
        admissionFile.setSubmittedAt(LocalDateTime.now());
        admissionFile.setUser(user);

        return admissionFile;
    }

    private AdmissionFileDto getAdmissionFileDto() {
        return new AdmissionFileDto(ID, LocalDateTime.now(), AdmissionFileStatus.PENDING, USER_ID);
    }
}