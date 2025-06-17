package com.nls.paymentservice.infrastructure.config;

import com.nls.common.dto.response.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    // 1. Entity Not Found (e.g., JPA couldn't find record by ID)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleEntityNotFound(EntityNotFoundException ex) {
        log.error("Entity not found: {}", ex.getMessage());
        return ResponseEntity.status(404).body(ApiResponse.notFound("Resource not found", null));
    }

    // 2. Validation Errors (e.g., @Valid failed)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldError().getDefaultMessage();
        log.warn("Validation failed: {}", errorMessage);
        return ResponseEntity.badRequest().body(ApiResponse.badRequest(errorMessage));
    }

    // 3. Bad JSON or Wrong Body Format
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequest(HttpMessageNotReadableException ex) {
        log.warn("Invalid request format: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ApiResponse.badRequest("Malformed JSON or invalid request body"));
    }

    // 4. Missing Parameters (e.g., missing query param)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Object>> handleMissingParam(MissingServletRequestParameterException ex) {
        String message = String.format("Missing request parameter: %s", ex.getParameterName());
        log.warn(message);
        return ResponseEntity.badRequest().body(ApiResponse.badRequest(message));
    }

    // 5. Access Denied (e.g., unauthorized access)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity.status(401).body(ApiResponse.unauthorized("You are not authorized to perform this action"));
    }

    // 6. Fallback for Unhandled Exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        log.error("Unhandled exception: ", ex);
        return ResponseEntity.status(500).body(ApiResponse.internalError());
    }


}
