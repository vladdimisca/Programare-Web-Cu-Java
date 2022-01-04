package com.web.model.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {
    ROLE_STUDENT(Constants.STUDENT),
    ROLE_ADMIN(Constants.ADMIN);

    private String value;

    public static class Constants {
        public static final String ADMIN = "ROLE_ADMIN";
        public static final String STUDENT = "ROLE_STUDENT";
    }
}
