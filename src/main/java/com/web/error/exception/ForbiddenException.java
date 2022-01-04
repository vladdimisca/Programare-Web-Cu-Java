package com.web.error.exception;

import com.web.error.ErrorMessage;
import org.springframework.http.HttpStatus;

public class ForbiddenException extends AbstractApiException {
    public ForbiddenException(ErrorMessage errorMessage, Object... params) {
        super(HttpStatus.FORBIDDEN, errorMessage, params);
    }
}
