package com.framework.springbatch.global.error;

import com.framework.springbatch.global.common.dto.ApiResponse;
import com.framework.springbatch.global.error.exception.BatchJobException;
import com.framework.springbatch.global.error.exception.BusinessException;
import com.framework.springbatch.global.error.exception.ResourceNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 전역 예외 처리 핸들러
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ═══════════════════════════════════════════════════════════════
    // 비즈니스 예외
    // ═══════════════════════════════════════════════════════════════

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        log.error("Business Exception: {}", e.getMessage());
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ApiResponse.error(e.getErrorCode()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException e) {
        log.error("Resource Not Found: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ErrorCode.RESOURCE_NOT_FOUND));
    }

    // ═══════════════════════════════════════════════════════════════
    // 배치 예외
    // ═══════════════════════════════════════════════════════════════

    @ExceptionHandler(BatchJobException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleBatchJobException(BatchJobException e) {
        log.error("Batch Job Exception: [{}] {}", e.getJobName(), e.getMessage());
        
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("jobName", e.getJobName());
        errorData.put("jobExecutionId", e.getJobExecutionId());
        
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ApiResponse.error(e.getErrorCode(), errorData));
    }

    @ExceptionHandler(JobInstanceAlreadyCompleteException.class)
    public ResponseEntity<ApiResponse<Void>> handleJobInstanceAlreadyComplete(JobInstanceAlreadyCompleteException e) {
        log.warn("Job Instance Already Complete: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ErrorCode.BATCH_JOB_ALREADY_RUNNING));
    }

    @ExceptionHandler(JobRestartException.class)
    public ResponseEntity<ApiResponse<Void>> handleJobRestartException(JobRestartException e) {
        log.error("Job Restart Exception: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ErrorCode.BATCH_RESTART_FAILED));
    }

    // ═══════════════════════════════════════════════════════════════
    // 인증/인가 예외
    // ═══════════════════════════════════════════════════════════════

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException e) {
        log.error("Authentication Exception: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ErrorCode.UNAUTHORIZED));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentialsException(BadCredentialsException e) {
        log.error("Bad Credentials: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ErrorCode.LOGIN_FAILED));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException e) {
        log.error("Access Denied: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(ErrorCode.ACCESS_DENIED));
    }

    // ═══════════════════════════════════════════════════════════════
    // Validation 예외
    // ═══════════════════════════════════════════════════════════════

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException e) {
        log.error("Validation Exception: {}", e.getMessage());
        
        Map<String, String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value",
                        (existing, replacement) -> existing
                ));
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ErrorCode.INVALID_INPUT_VALUE, errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleConstraintViolation(
            ConstraintViolationException e) {
        log.error("Constraint Violation: {}", e.getMessage());
        
        Map<String, String> errors = e.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        violation -> violation.getMessage(),
                        (existing, replacement) -> existing
                ));
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ErrorCode.INVALID_INPUT_VALUE, errors));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException e) {
        log.error("Type Mismatch: {} - {}", e.getName(), e.getValue());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ErrorCode.INVALID_TYPE_VALUE));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameter(
            MissingServletRequestParameterException e) {
        log.error("Missing Parameter: {}", e.getParameterName());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ErrorCode.INVALID_INPUT_VALUE));
    }

    // ═══════════════════════════════════════════════════════════════
    // HTTP 예외
    // ═══════════════════════════════════════════════════════════════

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException e) {
        log.error("Method Not Allowed: {}", e.getMethod());
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.error(ErrorCode.METHOD_NOT_ALLOWED));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoHandlerFound(NoHandlerFoundException e) {
        log.error("No Handler Found: {}", e.getRequestURL());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ErrorCode.RESOURCE_NOT_FOUND));
    }

    // ═══════════════════════════════════════════════════════════════
    // 기타 예외
    // ═══════════════════════════════════════════════════════════════

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Unexpected Exception: ", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
