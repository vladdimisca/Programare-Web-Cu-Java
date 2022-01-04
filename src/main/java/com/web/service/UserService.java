package com.web.service;

import com.web.error.ErrorMessage;
import com.web.error.exception.ConflictException;
import com.web.error.exception.NotFoundException;
import com.web.model.User;
import com.web.model.UserInfo;
import com.web.model.enumeration.UserRole;
import com.web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final SecurityService securityService;

    @Autowired
    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder,
                       SecurityService securityService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.securityService = securityService;
    }

    @Transactional
    public User create(User user) {
        checkEmailNotUsed(user.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setRole(UserRole.ROLE_STUDENT);
        user.setDocument(null);
        user.setAdmissionFile(null);

        return userRepository.save(user);
    }

    @Transactional
    public User update(UUID id, User user) {
        User existingUser = getById(id);

        checkNotSubmitted(existingUser);
        if (!user.getEmail().equals(existingUser.getEmail())) {
            checkEmailNotUsed(user.getEmail());
        }

        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        return userRepository.save(existingUser);
    }

    public User getById(UUID id) {
        securityService.authorize(id);
        return userRepository.findById(id).orElseThrow(() ->
                new NotFoundException(ErrorMessage.NOT_FOUND, "user", id));
    }

    public List<User> getAll(String email, String nationality) {
        return userRepository.findAllByEmailAndNationality(email, nationality);
    }

    @Transactional
    public void deleteById(UUID id) {
        User user = getById(id);
        checkNotSubmitted(user);

        userRepository.delete(user);
    }

    @Transactional
    public UserInfo populateUserInfoById(UUID userId, UserInfo userInfo) {
        User user = getById(userId);
        if (user.getUserInfo() != null) {
            throw new ConflictException(ErrorMessage.ALREADY_EXISTS, "User information");
        }

        user.setUserInfo(userInfo);

        return userRepository.save(user).getUserInfo();
    }

    @Transactional
    public UserInfo updateUserInfoById(UUID userId, UserInfo userInfo) {
        User user = setUserInfoById(userId, userInfo);
        checkNotSubmitted(user);
        return userRepository.save(user).getUserInfo();
    }

    @Transactional
    public void deleteUserInfoById(UUID userId) {
        User user = setUserInfoById(userId, null);
        checkNotSubmitted(user);

        userRepository.save(user);
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    private User setUserInfoById(UUID userId, UserInfo userInfo) {
        User user = getById(userId);
        if (user.getUserInfo() == null) {
            throw new NotFoundException(ErrorMessage.NOT_FOUND, "information for the user", userId);
        }

        user.setUserInfo(userInfo);
        return user;
    }

    private void checkEmailNotUsed(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException(ErrorMessage.ALREADY_EXISTS, "Email");
        }
    }

    private void checkNotSubmitted(User user) {
        if (user.getAdmissionFile() != null) {
            throw new ConflictException(ErrorMessage.ADMISSION_FILE_ALREADY_SUBMITTED);
        }
    }

}
