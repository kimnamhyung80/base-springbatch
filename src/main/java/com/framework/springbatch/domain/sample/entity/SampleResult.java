package com.framework.springbatch.domain.sample.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 샘플 처리 결과 엔티티
 */
@Entity
@Table(name = "SAMPLE_RESULT")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SampleResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "SAMPLE_ID", nullable = false)
    private Long sampleId;

    @Column(name = "JOB_EXECUTION_ID")
    private Long jobExecutionId;

    @Column(name = "RESULT_STATUS", length = 50)
    private String resultStatus;

    @Column(name = "RESULT_MESSAGE", length = 1000)
    private String resultMessage;

    @Column(name = "PROCESSED_AT")
    @Builder.Default
    private LocalDateTime processedAt = LocalDateTime.now();
}
