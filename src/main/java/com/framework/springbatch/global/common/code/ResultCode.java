package com.framework.springbatch.global.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * API 응답 결과 코드
 */
@Getter
@RequiredArgsConstructor
public enum ResultCode {

    // ═══════════════════════════════════════════════════════════════
    // 성공 (2xx)
    // ═══════════════════════════════════════════════════════════════
    SUCCESS(200, "S001", "요청이 성공적으로 처리되었습니다."),
    CREATED(201, "S002", "리소스가 성공적으로 생성되었습니다."),
    ACCEPTED(202, "S003", "요청이 접수되었습니다."),
    NO_CONTENT(204, "S004", "처리가 완료되었습니다."),

    // ═══════════════════════════════════════════════════════════════
    // 배치 관련 (B0xx)
    // ═══════════════════════════════════════════════════════════════
    BATCH_STARTED(200, "B001", "배치 작업이 시작되었습니다."),
    BATCH_COMPLETED(200, "B002", "배치 작업이 완료되었습니다."),
    BATCH_STOPPED(200, "B003", "배치 작업이 중지되었습니다."),
    BATCH_ALREADY_RUNNING(200, "B004", "배치 작업이 이미 실행 중입니다."),

    // ═══════════════════════════════════════════════════════════════
    // 인증/인가 (A0xx)
    // ═══════════════════════════════════════════════════════════════
    UNAUTHORIZED(401, "A001", "인증이 필요합니다."),
    FORBIDDEN(403, "A002", "접근 권한이 없습니다."),
    TOKEN_EXPIRED(401, "A003", "토큰이 만료되었습니다."),
    TOKEN_INVALID(401, "A004", "유효하지 않은 토큰입니다.");

    private final int status;
    private final String code;
    private final String message;
}
