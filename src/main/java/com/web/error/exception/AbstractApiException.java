package com.web.error.exception;

import com.web.error.ErrorMessage;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.text.MessageFormat;

@Getter
public abstract class AbstractApiException extends RuntimeException {
    private final int errorCode;
    private final HttpStatus httpStatus;

    public AbstractApiException(HttpStatus httpStatus, ErrorMessage errorMessage, Object... params) {
        super(formatMessage(errorMessage.getErrorMessage(), params));
        this.errorCode = errorMessage.getErrorCode();
        this.httpStatus = httpStatus;
    }

    private static String formatMessage(String message, Object... messageArgs) {
        return MessageFormat.format(message, messageArgs);
    }
}
