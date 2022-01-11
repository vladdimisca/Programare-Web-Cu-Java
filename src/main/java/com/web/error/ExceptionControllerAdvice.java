package com.web.error;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.web.error.exception.AbstractApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

@ControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleCustomException(AbstractApiException exception) {
        return ResponseEntity
                .status(exception.getHttpStatus())
                .body(getSingleErrorResponse(exception.getErrorCode(), exception.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleNotAllowed(HttpClientErrorException.MethodNotAllowed exception) {
        return ResponseEntity
                .status(exception.getStatusCode())
                .body(getSingleErrorResponse(ErrorMessage.NOT_FOUND.getErrorCode(), exception.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        List<ErrorEntity> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(e -> new ErrorEntity(-1, capitalizeFirst(e.getField()) + " " + e.getDefaultMessage()))
                .toList();

        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(getMultipleErrorsResponse(errors));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException exception) {
        List<ErrorEntity> errors = exception.getConstraintViolations().stream()
                .map(e -> new ErrorEntity(-1, e.getPropertyPath() + " " + e.getMessage()))
                .toList();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(getMultipleErrorsResponse(errors));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException exception) {
        String message = "The " + exception.getName() + " has an invalid value of " + exception.getValue() + "!";
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(getSingleErrorResponse(-1, message));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException exception) {
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(getSingleErrorResponse(-1, exception.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(MissingServletRequestParameterException exception) {
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(getSingleErrorResponse(-1, exception.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        var e = (InvalidFormatException) exception.getCause();
        String message = e.getTargetType().getEnumConstants() != null ?
                getEnumErrorMessage(
                        e.getPath().get(0).getFieldName(),
                        Arrays.stream(e.getTargetType().getEnumConstants()).map(Object::toString).toList(),
                        e.getValue()) :
                getDefaultFieldError(e.getPath().get(0).getFieldName(), e.getValue());
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(getSingleErrorResponse(-1, message));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleDefaultException(Throwable exception) {
        exception.printStackTrace(); // Fake a logger

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(getSingleErrorResponse(
                        ErrorMessage.INTERNAL_SERVER_ERROR.getErrorCode(),
                        ErrorMessage.INTERNAL_SERVER_ERROR.getErrorMessage()));
    }

    private ErrorResponse getSingleErrorResponse(int errorCode, String errorMessage) {
        return new ErrorResponse(List.of(new ErrorEntity(errorCode, errorMessage)));
    }

    private ErrorResponse getMultipleErrorsResponse(List<ErrorEntity> errors) {
        return new ErrorResponse(errors);
    }

    private String capitalizeFirst(String str) {
        return str.isBlank() ? "" : str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private String getEnumErrorMessage(String field, List<String> expected, Object value) {
        return MessageFormat
                .format("Cannot deserialize the value of {0}. Expected one of {1}, but found {2}!",
                        field,
                        expected,
                        value);
    }

    private String getDefaultFieldError(String field, Object value) {
        return MessageFormat.format("Cannot deserialize the value of {0}, provided value: {1}!", field, value);
    }
}
