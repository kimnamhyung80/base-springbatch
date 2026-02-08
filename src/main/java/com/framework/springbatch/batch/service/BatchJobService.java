package com.framework.springbatch.batch.service;

import com.framework.springbatch.batch.dto.BatchJobExecutionDTO;
import com.framework.springbatch.batch.dto.BatchJobInfoDTO;
import com.framework.springbatch.global.error.ErrorCode;
import com.framework.springbatch.global.error.exception.BatchJobException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 배치 작업 실행 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BatchJobService {

    private final ApplicationContext applicationContext;
    private final JobExplorer jobExplorer;
    private final JobRepository jobRepository;
    private final JobOperator jobOperator;
    private final JobLauncher jobLauncher;
    @Qualifier("asyncJobLauncher")
    private final JobLauncher asyncJobLauncher;
    private final BatchLockService batchLockService;

    /**
     * 배치 작업 동기 실행
     */
    public BatchJobExecutionDTO runJob(String jobName, Map<String, String> parameters) {
        return executeJob(jobName, parameters, false);
    }

    /**
     * 배치 작업 비동기 실행
     */
    public BatchJobExecutionDTO runJobAsync(String jobName, Map<String, String> parameters) {
        return executeJob(jobName, parameters, true);
    }

    /**
     * 배치 작업 실행
     */
    private BatchJobExecutionDTO executeJob(String jobName, Map<String, String> parameters, boolean async) {
        // 1. Job 조회
        Job job = getJob(jobName);

        // 2. 분산 락 획득
        if (!batchLockService.tryLock(jobName)) {
            throw new BatchJobException(ErrorCode.BATCH_LOCK_FAILED, jobName);
        }

        try {
            // 3. Job Parameters 생성
            JobParameters jobParameters = createJobParameters(parameters);

            // 4. Job 실행
            JobLauncher launcher = async ? asyncJobLauncher : jobLauncher;
            JobExecution jobExecution = launcher.run(job, jobParameters);

            log.info("Job {} started with execution id: {}", jobName, jobExecution.getId());

            return toDTO(jobExecution);
        } catch (Exception e) {
            batchLockService.unlock(jobName);
            log.error("Failed to execute job: {}", jobName, e);
            throw new BatchJobException(ErrorCode.BATCH_JOB_FAILED, jobName, e);
        }
    }

    /**
     * 배치 작업 중지
     */
    public boolean stopJob(Long executionId) {
        try {
            return jobOperator.stop(executionId);
        } catch (Exception e) {
            log.error("Failed to stop job execution: {}", executionId, e);
            throw new BatchJobException(ErrorCode.BATCH_JOB_STOPPED, "executionId:" + executionId, e);
        }
    }

    /**
     * 배치 작업 재시작
     */
    public BatchJobExecutionDTO restartJob(Long executionId) {
        try {
            Long newExecutionId = jobOperator.restart(executionId);
            JobExecution jobExecution = jobExplorer.getJobExecution(newExecutionId);
            return toDTO(jobExecution);
        } catch (Exception e) {
            log.error("Failed to restart job execution: {}", executionId, e);
            throw new BatchJobException(ErrorCode.BATCH_RESTART_FAILED, "executionId:" + executionId, e);
        }
    }

    /**
     * 배치 작업 포기 (Abandon)
     */
    public BatchJobExecutionDTO abandonJob(Long executionId) {
        try {
            JobExecution jobExecution = jobExplorer.getJobExecution(executionId);
            if (jobExecution == null) {
                throw new BatchJobException(ErrorCode.BATCH_JOB_NOT_FOUND, "executionId:" + executionId);
            }
            
            jobExecution.setStatus(BatchStatus.ABANDONED);
            jobExecution.setEndTime(LocalDateTime.now());
            jobRepository.update(jobExecution);
            
            return toDTO(jobExecution);
        } catch (BatchJobException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to abandon job execution: {}", executionId, e);
            throw new BatchJobException(ErrorCode.BATCH_JOB_FAILED, "executionId:" + executionId, e);
        }
    }

    /**
     * 등록된 모든 Job 목록 조회
     */
    public List<BatchJobInfoDTO> getAllJobs() {
        return jobExplorer.getJobNames().stream()
                .map(this::getJobInfo)
                .collect(Collectors.toList());
    }

    /**
     * Job 정보 조회
     */
    public BatchJobInfoDTO getJobInfo(String jobName) {
        List<JobInstance> jobInstances = jobExplorer.getJobInstances(jobName, 0, 1);
        JobExecution lastExecution = null;
        
        if (!jobInstances.isEmpty()) {
            List<JobExecution> executions = jobExplorer.getJobExecutions(jobInstances.get(0));
            if (!executions.isEmpty()) {
                lastExecution = executions.get(0);
            }
        }

        return BatchJobInfoDTO.builder()
                .jobName(jobName)
                .lastExecutionId(lastExecution != null ? lastExecution.getId() : null)
                .lastStatus(lastExecution != null ? lastExecution.getStatus().name() : null)
                .lastStartTime(lastExecution != null ? lastExecution.getStartTime() : null)
                .lastEndTime(lastExecution != null ? lastExecution.getEndTime() : null)
                .isRunning(batchLockService.isLocked(jobName))
                .build();
    }

    /**
     * Job Execution 조회
     */
    public BatchJobExecutionDTO getJobExecution(Long executionId) {
        JobExecution jobExecution = jobExplorer.getJobExecution(executionId);
        if (jobExecution == null) {
            throw new BatchJobException(ErrorCode.BATCH_JOB_NOT_FOUND, "executionId:" + executionId);
        }
        return toDTO(jobExecution);
    }

    /**
     * Job Execution 이력 조회
     */
    public List<BatchJobExecutionDTO> getJobExecutionHistory(String jobName, int page, int size) {
        List<JobInstance> jobInstances = jobExplorer.getJobInstances(jobName, 0, 100);
        
        return jobInstances.stream()
                .flatMap(instance -> jobExplorer.getJobExecutions(instance).stream())
                .sorted((e1, e2) -> e2.getStartTime().compareTo(e1.getStartTime()))
                .skip((long) page * size)
                .limit(size)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Job 조회
     */
    private Job getJob(String jobName) {
        try {
            return applicationContext.getBean(jobName, Job.class);
        } catch (Exception e) {
            throw new BatchJobException(ErrorCode.BATCH_JOB_NOT_FOUND, jobName);
        }
    }

    /**
     * Job Parameters 생성
     */
    private JobParameters createJobParameters(Map<String, String> parameters) {
        JobParametersBuilder builder = new JobParametersBuilder();
        
        // 기본 파라미터: 실행 시간 (중복 실행 방지)
        builder.addLocalDateTime("runTime", LocalDateTime.now());
        
        // 사용자 파라미터 추가
        if (parameters != null) {
            parameters.forEach(builder::addString);
        }
        
        return builder.toJobParameters();
    }

    /**
     * JobExecution -> DTO 변환
     */
    private BatchJobExecutionDTO toDTO(JobExecution execution) {
        return BatchJobExecutionDTO.builder()
                .executionId(execution.getId())
                .jobName(execution.getJobInstance().getJobName())
                .jobInstanceId(execution.getJobInstance().getInstanceId())
                .status(execution.getStatus().name())
                .exitCode(execution.getExitStatus().getExitCode())
                .exitDescription(execution.getExitStatus().getExitDescription())
                .startTime(execution.getStartTime())
                .endTime(execution.getEndTime())
                .createTime(execution.getCreateTime())
                .lastUpdated(execution.getLastUpdated())
                .parameters(execution.getJobParameters().getParameters().entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> e.getValue().getValue() != null ? e.getValue().getValue().toString() : null
                        )))
                .stepExecutions(execution.getStepExecutions().stream()
                        .map(this::toStepDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * StepExecution -> DTO 변환
     */
    private BatchJobExecutionDTO.StepExecutionDTO toStepDTO(StepExecution step) {
        return BatchJobExecutionDTO.StepExecutionDTO.builder()
                .stepName(step.getStepName())
                .status(step.getStatus().name())
                .readCount(step.getReadCount())
                .writeCount(step.getWriteCount())
                .commitCount(step.getCommitCount())
                .rollbackCount(step.getRollbackCount())
                .skipCount(step.getSkipCount())
                .startTime(step.getStartTime())
                .endTime(step.getEndTime())
                .build();
    }
}
