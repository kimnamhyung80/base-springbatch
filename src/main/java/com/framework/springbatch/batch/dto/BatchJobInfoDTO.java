package com.framework.springbatch.batch.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 배치 작업 정보 DTO
 */
@Getter
@Builder
public class BatchJobInfoDTO {

    private final String jobName;
    private final Long lastExecutionId;
    private final String lastStatus;
    private final LocalDateTime lastStartTime;
    private final LocalDateTime lastEndTime;
    private final Boolean isRunning;
}
