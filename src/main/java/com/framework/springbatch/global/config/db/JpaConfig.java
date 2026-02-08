package com.framework.springbatch.global.config.db;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * JPA 설정
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.framework.springbatch.domain")
public class JpaConfig {
    // JPA Auditing은 메인 애플리케이션에서 @EnableJpaAuditing으로 활성화
}
