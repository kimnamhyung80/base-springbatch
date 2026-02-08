package com.framework.springbatch.batch.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 배치 작업 실행 정보 DTO
 */
@Getter
@Builder
public class BatchJobExecutionDTO {

    private final Long executionId;
    private final String jobName;
    private final Long jobInstanceId;
    private final String status;
    private final String exitCode;
    private final String exitDescription;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final LocalDateTime createTime;
    private final LocalDateTime lastUpdated;
    private final Map<String, String> parameters;
    private final List<StepExecutionDTO> stepExecutions;

    @Getter
    @Builder
    public static class StepExecutionDTO {
        private final String stepName;
        private final String status;
        private final long readCount;
        private final long writeCount;
        private final long commitCount;
        private final long rollbackCount;
        private final long skipCount;
        private final LocalDateTime startTime;
        private final LocalDateTime endTime;
    }
}
