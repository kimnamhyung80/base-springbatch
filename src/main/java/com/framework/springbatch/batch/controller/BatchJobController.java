package com.framework.springbatch.batch.controller;

import com.framework.springbatch.batch.dto.BatchJobExecutionDTO;
import com.framework.springbatch.batch.dto.BatchJobInfoDTO;
import com.framework.springbatch.batch.dto.BatchJobRunRequest;
import com.framework.springbatch.batch.service.BatchJobService;
import com.framework.springbatch.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 배치 작업 관리 컨트롤러
 */
@Tag(name = "Batch Jobs", description = "배치 작업 관리 API")
@RestController
@RequestMapping("/v1/batch/jobs")
@RequiredArgsConstructor
public class BatchJobController {

    private final BatchJobService batchJobService;

    @Operation(summary = "모든 배치 작업 목록 조회", description = "등록된 모든 배치 작업 목록을 조회합니다.")
    @GetMapping
    public ApiResponse<List<BatchJobInfoDTO>> getAllJobs() {
        return ApiResponse.success(batchJobService.getAllJobs());
    }

    @Operation(summary = "배치 작업 정보 조회", description = "특정 배치 작업의 정보를 조회합니다.")
    @GetMapping("/{jobName}")
    public ApiResponse<BatchJobInfoDTO> getJobInfo(
            @Parameter(description = "Job 이름") @PathVariable String jobName) {
        return ApiResponse.success(batchJobService.getJobInfo(jobName));
    }

    @Operation(summary = "배치 작업 실행", description = "배치 작업을 실행합니다.")
    @PostMapping("/run")
    public ApiResponse<BatchJobExecutionDTO> runJob(
            @Valid @RequestBody BatchJobRunRequest request) {
        BatchJobExecutionDTO result;
        if (request.isAsync()) {
            result = batchJobService.runJobAsync(request.getJobName(), request.getParameters());
        } else {
            result = batchJobService.runJob(request.getJobName(), request.getParameters());
        }
        return ApiResponse.success(result, "배치 작업이 시작되었습니다.");
    }

    @Operation(summary = "배치 작업 중지", description = "실행 중인 배치 작업을 중지합니다.")
    @PostMapping("/executions/{executionId}/stop")
    public ApiResponse<Boolean> stopJob(
            @Parameter(description = "실행 ID") @PathVariable Long executionId) {
        boolean stopped = batchJobService.stopJob(executionId);
        return ApiResponse.success(stopped, "배치 작업 중지 요청이 처리되었습니다.");
    }

    @Operation(summary = "배치 작업 재시작", description = "실패한 배치 작업을 재시작합니다.")
    @PostMapping("/executions/{executionId}/restart")
    public ApiResponse<BatchJobExecutionDTO> restartJob(
            @Parameter(description = "실행 ID") @PathVariable Long executionId) {
        return ApiResponse.success(batchJobService.restartJob(executionId), "배치 작업이 재시작되었습니다.");
    }

    @Operation(summary = "배치 작업 포기", description = "배치 작업을 포기(Abandon) 상태로 변경합니다.")
    @PostMapping("/executions/{executionId}/abandon")
    public ApiResponse<BatchJobExecutionDTO> abandonJob(
            @Parameter(description = "실행 ID") @PathVariable Long executionId) {
        return ApiResponse.success(batchJobService.abandonJob(executionId), "배치 작업이 포기되었습니다.");
    }

    @Operation(summary = "배치 실행 정보 조회", description = "특정 배치 실행의 상세 정보를 조회합니다.")
    @GetMapping("/executions/{executionId}")
    public ApiResponse<BatchJobExecutionDTO> getJobExecution(
            @Parameter(description = "실행 ID") @PathVariable Long executionId) {
        return ApiResponse.success(batchJobService.getJobExecution(executionId));
    }

    @Operation(summary = "배치 실행 이력 조회", description = "특정 배치 작업의 실행 이력을 조회합니다.")
    @GetMapping("/{jobName}/executions")
    public ApiResponse<List<BatchJobExecutionDTO>> getJobExecutionHistory(
            @Parameter(description = "Job 이름") @PathVariable String jobName,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(batchJobService.getJobExecutionHistory(jobName, page, size));
    }
}
