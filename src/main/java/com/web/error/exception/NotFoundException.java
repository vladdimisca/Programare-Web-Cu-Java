package com.web.error.exception;

import com.web.error.ErrorMessage;
import org.springframework.http.HttpStatus;

public class NotFoundException extends AbstractApiException {
    public NotFoundException(ErrorMessage errorMessage, Object... params) {
        super(HttpStatus.NOT_FOUND, errorMessage, params);
    }
}
