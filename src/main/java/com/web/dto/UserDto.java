package com.web.dto;

import com.web.model.enumeration.UserRole;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserDto(
        @Null
        UUID id,

        @Email
        @NotNull
        String email,

        @NotNull
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z]).{6,}",
                message = "must contain at least 6 characters including one digit and one lower case letter")
        String password,

        @Null
        UserRole role,

        @Null
        LocalDateTime createdAt,

        @Null
        UserInfoDto userInfo,

        @Null
        Long documentId,

        @Null
        Long admissionFileId
        ) {
        public UserDto(UUID id, String email, String password) {
                this(id, email, password, null, null, null, null, null);
        }
}
