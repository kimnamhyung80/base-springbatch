package com.framework.springbatch.global.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.framework.springbatch.global.error.ErrorCode;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 표준 API 응답 포맷
 *
 * @param <T> 응답 데이터 타입
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final int status;
    private final String code;
    private final String message;
    private final T data;
    private final LocalDateTime timestamp;

    /**
     * 성공 응답 (데이터 포함)
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .status(200)
                .code("SUCCESS")
                .message("요청이 성공적으로 처리되었습니다.")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 성공 응답 (메시지 커스텀)
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .status(200)
                .code("SUCCESS")
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 성공 응답 (데이터 없음)
     */
    public static ApiResponse<Void> success() {
        return ApiResponse.<Void>builder()
                .status(200)
                .code("SUCCESS")
                .message("요청이 성공적으로 처리되었습니다.")
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 생성 성공 응답 (201)
     */
    public static <T> ApiResponse<T> created(T data) {
        return ApiResponse.<T>builder()
                .status(201)
                .code("CREATED")
                .message("리소스가 성공적으로 생성되었습니다.")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 에러 응답 (ErrorCode 기반)
     */
    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return ApiResponse.<T>builder()
                .status(errorCode.getStatus())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 에러 응답 (데이터 포함)
     */
    public static <T> ApiResponse<T> error(ErrorCode errorCode, T data) {
        return ApiResponse.<T>builder()
                .status(errorCode.getStatus())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 커스텀 에러 응답
     */
    public static <T> ApiResponse<T> error(int status, String code, String message) {
        return ApiResponse.<T>builder()
                .status(status)
                .code(code)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
