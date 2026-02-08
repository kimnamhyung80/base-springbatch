package com.framework.springbatch.batch.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 배치 작업 실행 요청 DTO
 */
@Getter
@Setter
public class BatchJobRunRequest {

    @NotBlank(message = "Job 이름은 필수입니다.")
    private String jobName;

    /**
     * Job 파라미터
     */
    private Map<String, String> parameters;

    /**
     * 비동기 실행 여부
     */
    private boolean async = false;
}
