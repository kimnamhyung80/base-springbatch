package com.framework.springbatch.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 에러 코드 정의
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ═══════════════════════════════════════════════════════════════
    // 공통 에러 (C0xx)
    // ═══════════════════════════════════════════════════════════════
    INTERNAL_SERVER_ERROR(500, "C001", "서버 내부 오류가 발생했습니다."),
    INVALID_INPUT_VALUE(400, "C002", "입력값이 올바르지 않습니다."),
    INVALID_TYPE_VALUE(400, "C003", "입력값의 타입이 올바르지 않습니다."),
    METHOD_NOT_ALLOWED(405, "C004", "허용되지 않은 HTTP 메서드입니다."),
    RESOURCE_NOT_FOUND(404, "C005", "요청한 리소스를 찾을 수 없습니다."),
    ENTITY_NOT_FOUND(404, "C006", "요청한 엔티티를 찾을 수 없습니다."),
    DUPLICATE_RESOURCE(409, "C007", "이미 존재하는 리소스입니다."),

    // ═══════════════════════════════════════════════════════════════
    // 인증/인가 에러 (A0xx)
    // ═══════════════════════════════════════════════════════════════
    UNAUTHORIZED(401, "A001", "인증이 필요합니다."),
    ACCESS_DENIED(403, "A002", "접근 권한이 없습니다."),
    TOKEN_EXPIRED(401, "A003", "토큰이 만료되었습니다."),
    TOKEN_INVALID(401, "A004", "유효하지 않은 토큰입니다."),
    LOGIN_FAILED(401, "A005", "아이디 또는 비밀번호가 올바르지 않습니다."),
    ACCOUNT_DISABLED(401, "A006", "비활성화된 계정입니다."),
    ACCOUNT_LOCKED(401, "A007", "잠긴 계정입니다."),

    // ═══════════════════════════════════════════════════════════════
    // 배치 에러 (B0xx)
    // ═══════════════════════════════════════════════════════════════
    BATCH_JOB_NOT_FOUND(404, "B001", "배치 작업을 찾을 수 없습니다."),
    BATCH_JOB_ALREADY_RUNNING(409, "B002", "배치 작업이 이미 실행 중입니다."),
    BATCH_JOB_FAILED(500, "B003", "배치 작업 실행 중 오류가 발생했습니다."),
    BATCH_JOB_STOPPED(500, "B004", "배치 작업이 중지되었습니다."),
    BATCH_STEP_FAILED(500, "B005", "배치 스텝 실행 중 오류가 발생했습니다."),
    BATCH_INVALID_PARAMETER(400, "B006", "잘못된 배치 파라미터입니다."),
    BATCH_LOCK_FAILED(409, "B007", "배치 락 획득에 실패했습니다."),
    BATCH_RESTART_FAILED(500, "B008", "배치 재시작에 실패했습니다."),

    // ═══════════════════════════════════════════════════════════════
    // 비즈니스 에러 (E0xx)
    // ═══════════════════════════════════════════════════════════════
    INVALID_INPUT(400, "E001", "입력값이 올바르지 않습니다."),
    DATA_NOT_FOUND(404, "E002", "데이터를 찾을 수 없습니다."),
    DATA_INTEGRITY_VIOLATION(409, "E003", "데이터 무결성 위반입니다."),
    OPERATION_NOT_PERMITTED(403, "E004", "허용되지 않은 작업입니다.");

    private final int status;
    private final String code;
    private final String message;
}
