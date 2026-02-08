package com.framework.springbatch.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Job 실행 리스너
 * - Job 시작/종료 시 로깅 및 MDC 설정
 */
@Slf4j
@Component
public class JobExecutionLogListener implements JobExecutionListener {

    private static final String JOB_NAME_KEY = "jobName";
    private static final String JOB_EXECUTION_ID_KEY = "jobExecutionId";

    @Override
    public void beforeJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();
        Long executionId = jobExecution.getId();

        // MDC 설정 (로깅에 활용)
        MDC.put(JOB_NAME_KEY, jobName);
        MDC.put(JOB_EXECUTION_ID_KEY, String.valueOf(executionId));

        log.info("═══════════════════════════════════════════════════════════════");
        log.info("Job Started: {}", jobName);
        log.info("Job Execution ID: {}", executionId);
        log.info("Job Parameters: {}", jobExecution.getJobParameters());
        log.info("Start Time: {}", LocalDateTime.now());
        log.info("═══════════════════════════════════════════════════════════════");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();
        Long executionId = jobExecution.getId();

        Duration duration = Duration.between(
                jobExecution.getStartTime(),
                jobExecution.getEndTime() != null ? jobExecution.getEndTime() : LocalDateTime.now()
        );

        log.info("═══════════════════════════════════════════════════════════════");
        log.info("Job Finished: {}", jobName);
        log.info("Job Execution ID: {}", executionId);
        log.info("Status: {}", jobExecution.getStatus());
        log.info("Exit Status: {}", jobExecution.getExitStatus().getExitCode());
        log.info("Duration: {} seconds", duration.getSeconds());
        
        // Step 실행 정보 출력
        jobExecution.getStepExecutions().forEach(stepExecution -> {
            log.info("───────────────────────────────────────────────────────────────");
            log.info("Step: {}", stepExecution.getStepName());
            log.info("  Read Count: {}", stepExecution.getReadCount());
            log.info("  Write Count: {}", stepExecution.getWriteCount());
            log.info("  Skip Count: {} (Read: {}, Process: {}, Write: {})",
                    stepExecution.getSkipCount(),
                    stepExecution.getReadSkipCount(),
                    stepExecution.getProcessSkipCount(),
                    stepExecution.getWriteSkipCount());
            log.info("  Commit Count: {}", stepExecution.getCommitCount());
            log.info("  Rollback Count: {}", stepExecution.getRollbackCount());
        });

        if (jobExecution.getAllFailureExceptions().size() > 0) {
            log.error("Job Failures:");
            jobExecution.getAllFailureExceptions().forEach(throwable -> 
                log.error("  - {}: {}", throwable.getClass().getSimpleName(), throwable.getMessage())
            );
        }

        log.info("═══════════════════════════════════════════════════════════════");

        // MDC 정리
        MDC.remove(JOB_NAME_KEY);
        MDC.remove(JOB_EXECUTION_ID_KEY);
    }
}
