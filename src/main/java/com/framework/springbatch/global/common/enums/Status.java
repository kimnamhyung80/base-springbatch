package com.framework.springbatch.global.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 공통 상태 Enum
 */
@Getter
@RequiredArgsConstructor
public enum Status {

    ACTIVE("활성"),
    INACTIVE("비활성"),
    PENDING("대기"),
    PROCESSING("처리중"),
    COMPLETED("완료"),
    FAILED("실패"),
    CANCELLED("취소"),
    DELETED("삭제");

    private final String description;
}
