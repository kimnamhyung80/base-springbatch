package com.framework.springbatch.domain.sample.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 샘플 DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SampleDTO {

    private Long id;
    private String name;
    private String description;
    private String status;
    private BigDecimal amount;
    private Boolean processed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
