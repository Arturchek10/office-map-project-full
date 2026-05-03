package com.t1.map_service.exception.handler;

import com.t1.map_service.exception.*;
import com.t1.map_service.exception.dto.ErrorResponse;
import com.t1.map_service.exception.dto.SubError;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(
            ResponseStatusException ex,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(ex.getStatusCode())
            .body(ErrorResponse.builder()
                .status(ex.getStatusCode().value())
                .message(ex.getReason() != null ? ex.getReason() : "Request failed")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .subErrors(Collections.emptyList())
                .build());
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<SubError> subErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> SubError.builder()
                        .object(error.getObjectName())
                        .field(error.getField())
                        .rejectedValue(error.getRejectedValue())
                        .message(error.getDefaultMessage())
                        .build())
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message("Validation failed")
                        .timestamp(LocalDateTime.now())
                        .path(request.getRequestURI())
                        .subErrors(subErrors)
                        .build());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            HttpServletRequest request
    ) {
        String message = String.format(
                "Content-Type '%s' is not supported. Use one of: %s",
                ex.getContentType(),
                ex.getSupportedMediaTypes()
        );

        return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                        .message(message)
                        .timestamp(LocalDateTime.now())
                        .path(request.getRequestURI())
                        .subErrors(List.of())
                        .build());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .path(request.getRequestURI())
                        .subErrors(Collections.emptyList())
                        .build());
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEntityAlreadyExistsException(EntityAlreadyExistsException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.CONFLICT.value())
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .path(request.getRequestURI())
                        .subErrors(Collections.emptyList())
                        .build());
    }

    @ExceptionHandler(UnsupportedStrategyTypeException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedStrategyTypeException(UnsupportedStrategyTypeException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .path(request.getRequestURI())
                        .subErrors(Collections.emptyList())
                        .build());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message("Invalid JSON: %s".formatted(ex.getMostSpecificCause().getMessage()))
                        .timestamp(LocalDateTime.now())
                        .path(request.getRequestURI())
                        .subErrors(Collections.emptyList())
                        .build());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message("Invalid path variable %s: %s".formatted(ex.getName(), ex.getValue()))
                        .timestamp(LocalDateTime.now())
                        .path(request.getRequestURI())
                        .subErrors(Collections.emptyList())
                        .build());
    }

    @ExceptionHandler(ClassCastException.class)
    public ResponseEntity<ErrorResponse> handleClassCastException(ClassCastException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .path(request.getRequestURI())
                        .subErrors(Collections.emptyList())
                        .build());
    }

    @ExceptionHandler(MinioException.class)
    public ResponseEntity<ErrorResponse> handleMinioException(MinioException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .path(request.getRequestURI())
                        .subErrors(Collections.emptyList())
                        .build());
    }

    @ExceptionHandler(MinioStorageException.class)
    public ResponseEntity<ErrorResponse> handleMinioStorageException(MinioStorageException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.BAD_GATEWAY.value())
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .path(request.getRequestURI())
                        .subErrors(Collections.emptyList())
                        .build());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex,
                                                            HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.CONFLICT.value())
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .path(request.getRequestURI())
                        .subErrors(Collections.emptyList())
                        .build());
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationCredentialsNotFound(
            AuthenticationCredentialsNotFoundException ex,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .message("Пользователь не авторизован")
                        .timestamp(LocalDateTime.now())
                        .path(request.getRequestURI())
                        .subErrors(Collections.emptyList())
                        .build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.FORBIDDEN.value())
                        .message("Недостаточно прав для выполнения операции")
                        .timestamp(LocalDateTime.now())
                        .path(request.getRequestURI())
                        .subErrors(Collections.emptyList())
                        .build());
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {
        log.error("logInternal: {} - {} - {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), request.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message("Internal Server Error")
                        .timestamp(LocalDateTime.now())
                        .path(request.getRequestURI())
                        .subErrors(Collections.emptyList())
                        .build());
    }

    @ExceptionHandler(BookingConflictException.class)
    public ResponseEntity<ErrorResponse> handleBookingConflict(
            BookingConflictException ex,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.CONFLICT.value())
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .path(request.getRequestURI())
                        .subErrors(Collections.emptyList())
                        .build());
    }
}
