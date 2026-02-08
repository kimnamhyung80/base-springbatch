package com.framework.springbatch.batch.job.sample;

import com.framework.springbatch.batch.config.BatchProperties;
import com.framework.springbatch.batch.listener.JobExecutionLogListener;
import com.framework.springbatch.batch.listener.StepExecutionLogListener;
import com.framework.springbatch.domain.sample.entity.Sample;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 * 파티셔닝 배치 Job 설정
 * ═══════════════════════════════════════════════════════════════════════════════
 * 
 * 대용량 데이터를 여러 파티션으로 나누어 병렬 처리
 * - ID 범위 기반 파티셔닝
 * - 멀티 스레드 병렬 처리
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class PartitionJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final DataSource dataSource;
    private final BatchProperties batchProperties;
    
    @Qualifier("partitionTaskExecutor")
    private final TaskExecutor partitionTaskExecutor;
    
    private final JobExecutionLogListener jobExecutionLogListener;
    private final StepExecutionLogListener stepExecutionLogListener;

    /**
     * 파티션 배치 Job
     */
    @Bean
    public Job partitionJob() {
        return new JobBuilder("partitionJob", jobRepository)
                .listener(jobExecutionLogListener)
                .start(partitionMasterStep())
                .build();
    }

    /**
     * 파티션 마스터 Step
     */
    @Bean
    public Step partitionMasterStep() {
        return new StepBuilder("partitionMasterStep", jobRepository)
                .partitioner("partitionWorkerStep", samplePartitioner())
                .step(partitionWorkerStep())
                .gridSize(batchProperties.getGridSize())
                .taskExecutor(partitionTaskExecutor)
                .build();
    }

    /**
     * 파티션 워커 Step
     */
    @Bean
    public Step partitionWorkerStep() {
        return new StepBuilder("partitionWorkerStep", jobRepository)
                .<Sample, Sample>chunk(batchProperties.getChunkSize(), transactionManager)
                .reader(partitionReader(null, null))
                .writer(items -> {
                    for (Sample sample : items) {
                        sample.setProcessed(true);
                        log.debug("Partition processed: id={}", sample.getId());
                    }
                })
                .listener(stepExecutionLogListener)
                .build();
    }

    /**
     * ID 범위 기반 Partitioner
     */
    @Bean
    public Partitioner samplePartitioner() {
        return gridSize -> {
            Map<String, ExecutionContext> partitions = new HashMap<>();

            try (Connection conn = dataSource.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(
                     "SELECT MIN(ID) as minId, MAX(ID) as maxId FROM SAMPLE WHERE PROCESSED = FALSE")) {
                
                if (rs.next()) {
                    long minId = rs.getLong("minId");
                    long maxId = rs.getLong("maxId");
                    long range = (maxId - minId) / gridSize + 1;

                    for (int i = 0; i < gridSize; i++) {
                        ExecutionContext context = new ExecutionContext();
                        long startId = minId + (i * range);
                        long endId = (i == gridSize - 1) ? maxId : startId + range - 1;

                        context.putLong("minId", startId);
                        context.putLong("maxId", endId);
                        
                        partitions.put("partition" + i, context);
                        
                        log.info("Partition {}: minId={}, maxId={}", i, startId, endId);
                    }
                }
            } catch (Exception e) {
                log.error("Error creating partitions", e);
            }

            return partitions;
        };
    }

    /**
     * 파티션 Reader (StepScope)
     */
    @Bean
    @StepScope
    public JpaPagingItemReader<Sample> partitionReader(
            @Value("#{stepExecutionContext['minId']}") Long minId,
            @Value("#{stepExecutionContext['maxId']}") Long maxId) {
        
        return new JpaPagingItemReaderBuilder<Sample>()
                .name("partitionReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(batchProperties.getPageSize())
                .queryString("SELECT s FROM Sample s WHERE s.id BETWEEN :minId AND :maxId AND s.processed = false ORDER BY s.id")
                .parameterValues(Map.of("minId", minId != null ? minId : 0L, "maxId", maxId != null ? maxId : Long.MAX_VALUE))
                .build();
    }
}
