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
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 * 파일 출력 배치 Job 설정
 * ═══════════════════════════════════════════════════════════════════════════════
 * 
 * DB 데이터를 CSV 파일로 추출하는 예제
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class FileExportJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final BatchProperties batchProperties;
    
    private final JobExecutionLogListener jobExecutionLogListener;
    private final StepExecutionLogListener stepExecutionLogListener;

    /**
     * 파일 출력 Job
     */
    @Bean
    public Job fileExportJob() {
        return new JobBuilder("fileExportJob", jobRepository)
                .listener(jobExecutionLogListener)
                .start(fileExportStep())
                .build();
    }

    /**
     * 파일 출력 Step
     */
    @Bean
    public Step fileExportStep() {
        return new StepBuilder("fileExportStep", jobRepository)
                .<Sample, Sample>chunk(batchProperties.getChunkSize(), transactionManager)
                .reader(fileExportReader())
                .writer(fileExportWriter(null))
                .listener(stepExecutionLogListener)
                .build();
    }

    /**
     * JPA Reader
     */
    @Bean
    public JpaPagingItemReader<Sample> fileExportReader() {
        return new JpaPagingItemReaderBuilder<Sample>()
                .name("fileExportReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(batchProperties.getPageSize())
                .queryString("SELECT s FROM Sample s WHERE s.status = 'ACTIVE' ORDER BY s.id")
                .build();
    }

    /**
     * CSV File Writer
     */
    @Bean
    @StepScope
    public FlatFileItemWriter<Sample> fileExportWriter(
            @Value("#{jobParameters['outputPath']}") String outputPath) {
        
        // 기본 출력 경로
        if (outputPath == null || outputPath.isEmpty()) {
            String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            outputPath = "./output/sample_export_" + date + ".csv";
        }

        // 필드 추출기
        BeanWrapperFieldExtractor<Sample> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"id", "name", "description", "status", "amount", "processed"});

        // 라인 구분자
        DelimitedLineAggregator<Sample> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");
        lineAggregator.setFieldExtractor(fieldExtractor);

        return new FlatFileItemWriterBuilder<Sample>()
                .name("fileExportWriter")
                .resource(new FileSystemResource(outputPath))
                .headerCallback(writer -> writer.write("ID,NAME,DESCRIPTION,STATUS,AMOUNT,PROCESSED"))
                .lineAggregator(lineAggregator)
                .build();
    }
}
