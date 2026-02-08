package com.framework.springbatch.batch.job.sample;

import com.framework.springbatch.batch.config.BatchProperties;
import com.framework.springbatch.batch.listener.ChunkLogListener;
import com.framework.springbatch.batch.listener.JobExecutionLogListener;
import com.framework.springbatch.batch.listener.SkipLogListener;
import com.framework.springbatch.batch.listener.StepExecutionLogListener;
import com.framework.springbatch.domain.sample.entity.Sample;
import com.framework.springbatch.domain.sample.entity.SampleResult;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 * 샘플 배치 Job 설정
 * ═══════════════════════════════════════════════════════════════════════════════
 * 
 * Chunk 기반 배치 처리 예제
 * - Reader: JPA Paging Reader
 * - Processor: 비즈니스 로직 처리
 * - Writer: JPA Writer
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SampleJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final BatchProperties batchProperties;
    
    // Listeners
    private final JobExecutionLogListener jobExecutionLogListener;
    private final StepExecutionLogListener stepExecutionLogListener;
    private final ChunkLogListener<Sample, SampleResult> chunkLogListener;
    private final SkipLogListener<Sample, SampleResult> skipLogListener;

    /**
     * 샘플 배치 Job
     */
    @Bean
    public Job sampleJob() {
        return new JobBuilder("sampleJob", jobRepository)
                .listener(jobExecutionLogListener)
                .start(sampleStep())
                .build();
    }

    /**
     * 샘플 배치 Step
     */
    @Bean
    public Step sampleStep() {
        return new StepBuilder("sampleStep", jobRepository)
                .<Sample, SampleResult>chunk(batchProperties.getChunkSize(), transactionManager)
                .reader(sampleReader())
                .processor(sampleProcessor())
                .writer(sampleResultWriter())
                .faultTolerant()
                .skipLimit(batchProperties.getSkipLimit())
                .skip(Exception.class)
                .retryLimit(batchProperties.getRetryLimit())
                .retry(Exception.class)
                .listener(stepExecutionLogListener)
                .listener((ItemReadListener<Sample>) chunkLogListener)
                .listener((ItemProcessListener<Sample, SampleResult>) chunkLogListener)
                .listener((ItemWriteListener<SampleResult>) chunkLogListener)
                .listener(skipLogListener)
                .build();
    }

    /**
     * JPA Paging Reader
     * - 미처리 상태의 Active 샘플을 페이징으로 조회
     */
    @Bean
    public JpaPagingItemReader<Sample> sampleReader() {
        return new JpaPagingItemReaderBuilder<Sample>()
                .name("sampleReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(batchProperties.getPageSize())
                .queryString("SELECT s FROM Sample s WHERE s.processed = false AND s.status = 'ACTIVE' ORDER BY s.id")
                .build();
    }

    /**
     * Item Processor
     * - Sample -> SampleResult 변환 및 비즈니스 로직 처리
     */
    @Bean
    public ItemProcessor<Sample, SampleResult> sampleProcessor() {
        return sample -> {
            log.debug("Processing sample: id={}, name={}", sample.getId(), sample.getName());
            
            // 비즈니스 로직 처리 (예: 금액 계산, 유효성 검증 등)
            boolean isValid = sample.getAmount() != null && sample.getAmount().doubleValue() >= 0;
            
            // 처리 결과 생성
            SampleResult result = SampleResult.builder()
                    .sampleId(sample.getId())
                    .resultStatus(isValid ? "SUCCESS" : "FAILED")
                    .resultMessage(isValid ? "처리 완료" : "유효하지 않은 금액")
                    .processedAt(LocalDateTime.now())
                    .build();
            
            // 원본 데이터 처리 완료 표시
            sample.setProcessed(true);
            
            return result;
        };
    }

    /**
     * JPA Item Writer
     */
    @Bean
    public JpaItemWriter<SampleResult> sampleResultWriter() {
        return new JpaItemWriterBuilder<SampleResult>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
