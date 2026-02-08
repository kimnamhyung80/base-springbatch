package com.framework.springbatch.domain.sample.entity;

import com.framework.springbatch.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * 샘플 엔티티
 */
@Entity
@Table(name = "SAMPLE")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sample extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(length = 50)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal amount = BigDecimal.ZERO;

    @Column
    @Builder.Default
    private Boolean processed = false;
}
