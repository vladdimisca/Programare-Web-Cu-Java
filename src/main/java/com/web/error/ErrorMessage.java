package com.web.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {
    INTERNAL_SERVER_ERROR(1, "Internal server error. Oops, something went wrong!"),
    ALREADY_EXISTS(2, "{0} already exists!"),
    NOT_FOUND(3, "The {0} with id={1} was not found!"),
    FORBIDDEN(5, "Forbidden action. {0}!"),
    INVALID_CONTENT_TYPE(6, "Invalid content type for {0}, expected one of {1}!"),
    ADMISSION_FILE_ALREADY_SUBMITTED(7, "The admission file is already submitted!");

    private final int errorCode;
    private final String errorMessage;
}
