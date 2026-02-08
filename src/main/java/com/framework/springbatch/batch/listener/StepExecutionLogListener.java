package com.framework.springbatch.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Step 실행 리스너
 */
@Slf4j
@Component
public class StepExecutionLogListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("───────────────────────────────────────────────────────────────");
        log.info("Step Started: {}", stepExecution.getStepName());
        log.info("Start Time: {}", LocalDateTime.now());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        Duration duration = Duration.between(
                stepExecution.getStartTime(),
                stepExecution.getEndTime() != null ? stepExecution.getEndTime() : LocalDateTime.now()
        );

        log.info("Step Finished: {} - Status: {}", 
                stepExecution.getStepName(), 
                stepExecution.getStatus());
        log.info("Step Duration: {} ms", duration.toMillis());
        log.info("Read: {}, Write: {}, Skip: {}", 
                stepExecution.getReadCount(),
                stepExecution.getWriteCount(),
                stepExecution.getSkipCount());
        log.info("───────────────────────────────────────────────────────────────");

        return stepExecution.getExitStatus();
    }
}
