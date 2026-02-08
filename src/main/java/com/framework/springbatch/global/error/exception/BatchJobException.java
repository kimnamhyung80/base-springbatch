package com.framework.springbatch.global.error.exception;

import com.framework.springbatch.global.error.ErrorCode;

/**
 * 배치 작업 관련 예외
 */
public class BatchJobException extends BusinessException {

    private final String jobName;
    private final Long jobExecutionId;

    public BatchJobException(ErrorCode errorCode) {
        super(errorCode);
        this.jobName = null;
        this.jobExecutionId = null;
    }

    public BatchJobException(ErrorCode errorCode, String jobName) {
        super(errorCode, String.format("[%s] %s", jobName, errorCode.getMessage()));
        this.jobName = jobName;
        this.jobExecutionId = null;
    }

    public BatchJobException(ErrorCode errorCode, String jobName, Long jobExecutionId) {
        super(errorCode, String.format("[%s:%d] %s", jobName, jobExecutionId, errorCode.getMessage()));
        this.jobName = jobName;
        this.jobExecutionId = jobExecutionId;
    }

    public BatchJobException(ErrorCode errorCode, String jobName, Throwable cause) {
        super(errorCode, cause);
        this.jobName = jobName;
        this.jobExecutionId = null;
    }

    public String getJobName() {
        return jobName;
    }

    public Long getJobExecutionId() {
        return jobExecutionId;
    }
}
