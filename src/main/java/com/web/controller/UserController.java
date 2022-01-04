package com.web.controller;

import com.web.dto.UserDto;
import com.web.dto.UserInfoDto;
import com.web.mapper.UserInfoMapper;
import com.web.mapper.UserMapper;
import com.web.model.User;
import com.web.model.UserInfo;
import com.web.model.enumeration.UserRole;
import com.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserMapper userMapper;
    private final UserInfoMapper userInfoMapper;
    private final UserService userService;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper, UserInfoMapper userInfoMapper) {
        this.userMapper = userMapper;
        this.userService = userService;
        this.userInfoMapper = userInfoMapper;
    }

    @PostMapping
    @PermitAll
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserDto userDto) {
        User user = userMapper.mapToEntity(userDto);
        return ResponseEntity
                .created(URI.create("/api/users/" + user.getId()))
                .body(userMapper.mapToDto(userService.create(user)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable("id") UUID id, @Valid @RequestBody UserDto userDto) {
        User user = userService.update(id, userMapper.mapToEntity(userDto));
        return ResponseEntity.ok(userMapper.mapToDto(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(userMapper.mapToDto(userService.getById(id)));
    }

    @GetMapping
    @RolesAllowed(UserRole.Constants.ADMIN)
    public ResponseEntity<List<UserDto>> getAll(@RequestParam(value = "email", required = false) String email,
                                                @RequestParam(value = "nationality", required = false) String nationality) {
        List<User> users = userService.getAll(email, nationality);
        return ResponseEntity.ok(users.stream().map(userMapper::mapToDto).toList());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") UUID id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/info")
    public ResponseEntity<UserInfoDto> populateUserInfo(@PathVariable("id") UUID id,
                                                        @Valid @RequestBody UserInfoDto userInfoDto) {
        UserInfo userInfo = userService.populateUserInfoById(id, userInfoMapper.mapToEntity(userInfoDto));
        return ResponseEntity
                .created(URI.create("/api/users/" + id))
                .body(userInfoMapper.mapToDto(userInfo));
    }

    @PutMapping("/{id}/info")
    public ResponseEntity<UserInfoDto> updateUserInfo(@PathVariable("id") UUID id,
                                                    @Valid @RequestBody UserInfoDto userInfoDto) {
        UserInfo userInfo = userService.updateUserInfoById(id, userInfoMapper.mapToEntity(userInfoDto));
        return ResponseEntity.ok().body(userInfoMapper.mapToDto(userInfo));
    }

    @DeleteMapping("/{id}/info")
    public ResponseEntity<?> deleteInfo(@PathVariable("id") UUID id) {
        userService.deleteUserInfoById(id);
        return ResponseEntity.noContent().build();
    }
}
