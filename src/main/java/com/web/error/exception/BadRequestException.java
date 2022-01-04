package com.web.error.exception;

import com.web.error.ErrorMessage;
import org.springframework.http.HttpStatus;

public class BadRequestException extends AbstractApiException {
    public BadRequestException(ErrorMessage errorMessage, Object... params) {
        super(HttpStatus.BAD_REQUEST, errorMessage, params);
    }
}
