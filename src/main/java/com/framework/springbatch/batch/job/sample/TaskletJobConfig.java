package com.framework.springbatch.batch.job.sample;

import com.framework.springbatch.batch.listener.JobExecutionLogListener;
import com.framework.springbatch.batch.listener.StepExecutionLogListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 * Tasklet 기반 배치 Job 설정
 * ═══════════════════════════════════════════════════════════════════════════════
 * 
 * 단순 작업을 Tasklet으로 처리하는 예제
 * - 데이터 정리, 통계 집계, 파일 처리 등
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class TaskletJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final JdbcTemplate jdbcTemplate;
    
    private final JobExecutionLogListener jobExecutionLogListener;
    private final StepExecutionLogListener stepExecutionLogListener;

    /**
     * 데이터 정리 Job
     */
    @Bean
    public Job dataCleanupJob() {
        return new JobBuilder("dataCleanupJob", jobRepository)
                .listener(jobExecutionLogListener)
                .start(cleanupOldDataStep())
                .next(updateStatisticsStep())
                .next(archiveDataStep())
                .build();
    }

    /**
     * Step 1: 오래된 데이터 정리
     */
    @Bean
    public Step cleanupOldDataStep() {
        return new StepBuilder("cleanupOldDataStep", jobRepository)
                .tasklet(cleanupOldDataTasklet(), transactionManager)
                .listener(stepExecutionLogListener)
                .build();
    }

    /**
     * Step 2: 통계 업데이트
     */
    @Bean
    public Step updateStatisticsStep() {
        return new StepBuilder("updateStatisticsStep", jobRepository)
                .tasklet(updateStatisticsTasklet(), transactionManager)
                .listener(stepExecutionLogListener)
                .build();
    }

    /**
     * Step 3: 데이터 아카이빙
     */
    @Bean
    public Step archiveDataStep() {
        return new StepBuilder("archiveDataStep", jobRepository)
                .tasklet(archiveDataTasklet(), transactionManager)
                .listener(stepExecutionLogListener)
                .build();
    }

    /**
     * 오래된 데이터 정리 Tasklet
     */
    @Bean
    public Tasklet cleanupOldDataTasklet() {
        return (contribution, chunkContext) -> {
            log.info("Starting cleanup of old data...");
            
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
            
            int deletedCount = jdbcTemplate.update(
                "DELETE FROM SAMPLE_RESULT WHERE PROCESSED_AT < ?",
                cutoffDate
            );
            
            log.info("Cleaned up {} old records", deletedCount);
            
            // ExecutionContext에 결과 저장
            chunkContext.getStepContext()
                    .getStepExecution()
                    .getExecutionContext()
                    .putInt("deletedCount", deletedCount);
            
            return RepeatStatus.FINISHED;
        };
    }

    /**
     * 통계 업데이트 Tasklet
     */
    @Bean
    public Tasklet updateStatisticsTasklet() {
        return (contribution, chunkContext) -> {
            log.info("Updating statistics...");
            
            // 상태별 건수 조회
            jdbcTemplate.query(
                "SELECT STATUS, COUNT(*) as CNT FROM SAMPLE GROUP BY STATUS",
                (rs) -> {
                    String status = rs.getString("STATUS");
                    int count = rs.getInt("CNT");
                    log.info("Status: {} - Count: {}", status, count);
                }
            );
            
            return RepeatStatus.FINISHED;
        };
    }

    /**
     * 데이터 아카이빙 Tasklet
     */
    @Bean
    public Tasklet archiveDataTasklet() {
        return (contribution, chunkContext) -> {
            log.info("Archiving completed data...");
            
            // 처리 완료된 데이터 상태 변경
            int archivedCount = jdbcTemplate.update(
                "UPDATE SAMPLE SET STATUS = 'ARCHIVED' WHERE PROCESSED = TRUE AND STATUS = 'ACTIVE'"
            );
            
            log.info("Archived {} records", archivedCount);
            
            return RepeatStatus.FINISHED;
        };
    }
}
