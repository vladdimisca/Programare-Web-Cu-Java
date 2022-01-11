package com.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.dto.UserDto;
import com.web.dto.UserInfoDto;
import com.web.mapper.UserInfoMapper;
import com.web.mapper.UserMapper;
import com.web.model.Address;
import com.web.model.User;
import com.web.model.UserInfo;
import com.web.model.enumeration.CivilStatus;
import com.web.model.enumeration.SexType;
import com.web.repository.UserRepository;
import com.web.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    private static final UUID ID = UUID.randomUUID();
    private static final String EMAIL = "test@gmail.com";
    private static final String PASSWORD = "random1234";
    private static final String NEW_EMAIL = "new@gmail.com";
    private static final String NEW_PASSWORD = "random4321";
    private static final Long INFO_ID = 1L;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private UserInfoMapper userInfoMapper;

    @Test
    @DisplayName("Create user - success")
    void createUser_success() throws Exception {
        UserDto userDto = getUserDto(EMAIL, PASSWORD);
        UserDto savedUserDto = getSavedUserDto();
        User user = getUser();
        User savedUser = getSavedUser();

        when(userMapper.mapToEntity(userDto)).thenReturn(user);
        when(userService.create(user)).thenReturn(savedUser);
        when(userMapper.mapToDto(savedUser)).thenReturn(savedUserDto);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(savedUserDto.id().toString())))
                .andExpect(jsonPath("$.email", is(savedUserDto.email())))
                .andExpect(jsonPath("$.password", nullValue()));
    }

    @Test
    @DisplayName("Update user - success")
    @WithMockUser
    void updateUser_success() throws Exception {
        UserDto userDto = getUserDto(NEW_EMAIL, NEW_PASSWORD);
        User user = getUpdatedUser();
        UserDto updatedUserDto = getUpdatedUserDto();

        when(userMapper.mapToEntity(userDto)).thenReturn(user);
        when(userService.update(ID, user)).thenReturn(user);
        when(userMapper.mapToDto(user)).thenReturn(updatedUserDto);

        mockMvc.perform(put("/api/users/" + ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedUserDto.id().toString())))
                .andExpect(jsonPath("$.email", is(updatedUserDto.email())))
                .andExpect(jsonPath("$.password", nullValue()));
    }

    @Test
    @DisplayName("Get user - success")
    @WithMockUser
    void getUser_success() throws Exception {
        UserDto userDto = getSavedUserDto();
        User user = getSavedUser();

        when(userService.getById(ID)).thenReturn(user);
        when(userMapper.mapToDto(user)).thenReturn(userDto);

        mockMvc.perform(get("/api/users/" + ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.id().toString())))
                .andExpect(jsonPath("$.email", is(userDto.email())))
                .andExpect(jsonPath("$.password", nullValue()));
    }

    @Test
    @DisplayName("Get all users - success")
    @WithMockUser(roles = {"ADMIN"})
    void getAllUsers_success() throws Exception {
        UserDto userDto = getSavedUserDto();
        User user = getSavedUser();

        when(userService.getAll(null, null)).thenReturn(List.of(user));
        when(userMapper.mapToDto(user)).thenReturn(userDto);

        mockMvc.perform(get("/api/users/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(userDto.id().toString())))
                .andExpect(jsonPath("$[0].email", is(userDto.email())))
                .andExpect(jsonPath("$[0].password", nullValue()));
    }

    @Test
    @DisplayName("Delete user - success")
    @WithMockUser
    void deleteUser_success() throws Exception {
        mockMvc.perform(delete("/api/users/" + ID))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteById(ID);
    }

    @Test
    @DisplayName("Create user - success")
    @WithMockUser
    void populateUserInfo_success() throws Exception {
        UserInfoDto userInfoDto = getUserInfoDto();
        UserInfo userInfo = getUserInfo();
        UserInfo savedUserInfo = getSavedUserInfo();

        when(userInfoMapper.mapToEntity(userInfoDto)).thenReturn(userInfo);
        when(userService.populateUserInfoById(ID, userInfo)).thenReturn(savedUserInfo);
        when(userInfoMapper.mapToDto(savedUserInfo)).thenReturn(userInfoDto);

        mockMvc.perform(post("/api/users/" + ID + "/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userInfoDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Update user - success")
    @WithMockUser
    void updateUserInfo_success() throws Exception {
        UserInfoDto userInfoDto = getUserInfoDto();
        UserInfo userInfo = getUserInfo();
        UserInfo savedUserInfo = getSavedUserInfo();

        when(userInfoMapper.mapToEntity(userInfoDto)).thenReturn(userInfo);
        when(userService.populateUserInfoById(ID, userInfo)).thenReturn(savedUserInfo);
        when(userInfoMapper.mapToDto(savedUserInfo)).thenReturn(userInfoDto);

        mockMvc.perform(put("/api/users/" + ID + "/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userInfoDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Delete user - success")
    @WithMockUser
    void deleteUserInfo_success() throws Exception {
        mockMvc.perform(delete("/api/users/" + ID + "/info"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUserInfoById(ID);
    }

    private UserDto getUserDto(String email, String password) {
        return new UserDto(null, email, password);
    }

    private UserDto getSavedUserDto() {
        return new UserDto(ID, EMAIL, null);
    }

    private UserDto getUpdatedUserDto() {
        return new UserDto(ID, NEW_EMAIL, null);
    }

    private User getUser() {
        User user = new User();
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);

        return user;
    }

    private User getSavedUser() {
        User user = getUser();
        user.setId(ID);

        return user;
    }

    private User getUpdatedUser() {
        User user = getSavedUser();
        user.setEmail(NEW_EMAIL);
        user.setPassword(NEW_PASSWORD);

        return user;
    }

    private UserInfoDto getUserInfoDto() {
        return new UserInfoDto(
                "test",
                "test1",
                "1930122123312",
                "abc",
                "0762332311",
                LocalDate.now(),
                CivilStatus.UNMARRIED,
                SexType.MALE,
                "bcd",
                "as",
                "abc",
                "npm",
                "12",
                null);
    }

    private UserInfo getUserInfo() {
        Address address = new Address();
        address.setCity("abc");
        address.setCountry("bcd");
        address.setNumber("12");
        address.setStreet("npm");
        address.setProvince("as");

        UserInfo userInfo = new UserInfo();
        userInfo.setFirstName("test");
        userInfo.setLastName("test1");
        userInfo.setSex(SexType.MALE);
        userInfo.setNationality("abc");
        userInfo.setCnp("1931205123312");
        userInfo.setCivilStatus(CivilStatus.UNMARRIED);
        userInfo.setPhoneNumber("0762332311");
        userInfo.setAddress(address);
        userInfo.setBirthDate(LocalDate.now());

        return userInfo;
    }

    private UserInfo getSavedUserInfo() {
        UserInfo userInfo = getUserInfo();
        userInfo.setId(INFO_ID);

        return userInfo;
    }
}