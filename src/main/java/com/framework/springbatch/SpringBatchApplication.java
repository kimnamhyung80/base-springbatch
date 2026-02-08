package com.framework.springbatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 * Spring Batch Enterprise Framework - Main Application
 * ═══════════════════════════════════════════════════════════════════════════════
 * 
 * 대기업급 Spring Batch 베이스 프레임워크
 * - Spring Batch 5.x 기반
 * - JPA + MyBatis 듀얼 영속성
 * - JWT 인증 (선택적)
 * - Kubernetes Ready
 * 
 * @author Framework Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
@EnableScheduling
public class SpringBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchApplication.class, args);
    }
}
