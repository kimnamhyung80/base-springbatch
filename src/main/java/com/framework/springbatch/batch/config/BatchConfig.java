package com.framework.springbatch.batch.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.ReferenceJobFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import jakarta.annotation.PostConstruct;

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 * Spring Batch 핵심 설정
 * ═══════════════════════════════════════════════════════════════════════════════
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchConfig implements ApplicationContextAware {

    private final JobRepository jobRepository;
    private final JobExplorer jobExplorer;
    private final JobRegistry jobRegistry;
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 모든 Job Bean을 JobRegistry에 등록
     */
    @PostConstruct
    public void registerJobs() {
        applicationContext.getBeansOfType(Job.class).forEach((name, job) -> {
            try {
                jobRegistry.register(new ReferenceJobFactory(job));
                log.info("Registered job: {}", job.getName());
            } catch (Exception e) {
                log.warn("Failed to register job: {}", job.getName(), e);
            }
        });
    }

    /**
     * 비동기 Job Launcher
     * - 배치 작업을 비동기로 실행
     */
    @Bean
    public JobLauncher asyncJobLauncher() throws Exception {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor("async-batch-"));
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    /**
     * Job Operator
     * - Job 시작, 중지, 재시작 등의 조작 담당
     */
    @Bean
    public JobOperator jobOperator(JobLauncher jobLauncher) throws Exception {
        SimpleJobOperator jobOperator = new SimpleJobOperator();
        jobOperator.setJobRepository(jobRepository);
        jobOperator.setJobExplorer(jobExplorer);
        jobOperator.setJobRegistry(jobRegistry);
        jobOperator.setJobLauncher(jobLauncher);
        jobOperator.afterPropertiesSet();
        return jobOperator;
    }

    /**
     * Chunk 처리용 TaskExecutor
     * - 병렬 청크 처리를 위한 스레드 풀
     */
    @Bean(name = "batchTaskExecutor")
    public TaskExecutor batchTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("batch-chunk-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }

    /**
     * Partitioner용 TaskExecutor
     * - 파티셔닝 병렬 처리를 위한 스레드 풀
     */
    @Bean(name = "partitionTaskExecutor")
    public TaskExecutor partitionTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("partition-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(120);
        executor.initialize();
        return executor;
    }
}
