package com.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.dto.UserProgramDto;
import com.web.mapper.UserProgramMapper;
import com.web.model.ProgramOfStudy;
import com.web.model.User;
import com.web.model.UserProgram;
import com.web.repository.UserRepository;
import com.web.service.UserProgramService;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserProgramController.class)
class UserProgramControllerTest {

    private static final Long ID = 1L;
    private static final UUID PROGRAM_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @MockBean
    private UserProgramService userProgramService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserProgramMapper userProgramMapper;

    @Test
    @DisplayName("Create user-program pair - success")
    @WithMockUser(roles = {"STUDENT"})
    void createUserProgramPair_success() throws Exception {
        UserProgramDto userProgramDto = getUserProgramDto();
        UserProgramDto savedUserProgramDto = getSavedUserProgramDto();
        UserProgram userProgram = getUserProgram();
        UserProgram savedUserProgram = getSavedUserProgram();

        when(userProgramMapper.mapToEntity(userProgramDto)).thenReturn(userProgram);
        when(userProgramService.create(userProgram)).thenReturn(savedUserProgram);
        when(userProgramMapper.mapToDto(savedUserProgram)).thenReturn(savedUserProgramDto);

        mockMvc.perform(post("/api/users-programs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userProgramDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(savedUserProgramDto.id()));
    }

    @Test
    @DisplayName("Update user-program pair - success")
    @WithMockUser(roles = {"STUDENT"})
    void updateUserProgramPair_success() throws Exception {
        UserProgramDto userProgramDto = getUserProgramDto();
        UserProgramDto updatedUserProgramDto = getSavedUserProgramDto();
        UserProgram userProgram = getUserProgram();
        UserProgram updatedUserProgram = getSavedUserProgram();

        when(userProgramMapper.mapToEntity(userProgramDto)).thenReturn(userProgram);
        when(userProgramService.updateById(ID, userProgram)).thenReturn(updatedUserProgram);
        when(userProgramMapper.mapToDto(updatedUserProgram)).thenReturn(updatedUserProgramDto);

        mockMvc.perform(put("/api/users-programs/" + ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userProgramDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedUserProgramDto.id()));
    }

    @Test
    @DisplayName("Get user-program pair - success")
    @WithMockUser(roles = {"STUDENT"})
    void getUserProgramPair_success() throws Exception {
        UserProgramDto userProgramDto = getSavedUserProgramDto();
        UserProgram userProgram = getSavedUserProgram();

        when(userProgramService.getById(ID)).thenReturn(userProgram);
        when(userProgramMapper.mapToDto(userProgram)).thenReturn(userProgramDto);

        mockMvc.perform(get("/api/users-programs/" + ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userProgramDto.id()));
    }

    @Test
    @DisplayName("Get all user-program pairs - success")
    @WithMockUser(roles = {"STUDENT"})
    void getAllUserProgramPairs_success() throws Exception {
        UserProgramDto userProgramDto = getSavedUserProgramDto();
        UserProgram userProgram = getSavedUserProgram();

        when(userProgramService.getAll(null, null)).thenReturn(List.of(userProgram));
        when(userProgramMapper.mapToDto(userProgram)).thenReturn(userProgramDto);

        mockMvc.perform(get("/api/users-programs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(userProgramDto.id()));
    }
    @Test
    @DisplayName("Delete user-program pair - success")
    @WithMockUser(roles = {"STUDENT"})
    void deleteUserProgramPair_success() throws Exception {
        mockMvc.perform(delete("/api/users-programs/" + ID))
                .andExpect(status().isNoContent());

        verify(userProgramService, times(1)).deleteById(ID);
    }

    @Test
    @DisplayName("Submit grade - success")
    @WithMockUser(roles = {"ADMIN"})
    void submitGrade_success() throws Exception {
        UserProgramDto userProgramDto = getSavedUserProgramDto();
        UserProgram userProgram = getSavedUserProgram();

        when(userProgramService.submitGrade(ID, 10)).thenReturn(userProgram);
        when(userProgramMapper.mapToDto(userProgram)).thenReturn(userProgramDto);

        mockMvc.perform(patch("/api/users-programs/" + ID + "?grade=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userProgramDto.id()));
    }

    private UserProgramDto getUserProgramDto() {
        return new UserProgramDto(null, null, PROGRAM_ID, null);
    }

    private UserProgramDto getSavedUserProgramDto() {
        return new UserProgramDto(ID, USER_ID, PROGRAM_ID, null);
    }

    private UserProgram getUserProgram() {
        ProgramOfStudy program = new ProgramOfStudy();
        program.setId(PROGRAM_ID);

        UserProgram userProgram = new UserProgram();
        userProgram.setProgramOfStudy(program);

        return userProgram;
    }

    private UserProgram getSavedUserProgram() {
        User user = new User();
        user.setId(USER_ID);

        UserProgram userProgram = getUserProgram();
        userProgram.setId(ID);
        userProgram.setUser(user);

        return userProgram;
    }
}