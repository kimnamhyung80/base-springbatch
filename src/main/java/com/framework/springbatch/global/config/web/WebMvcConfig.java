package com.framework.springbatch.global.config.web;

import com.framework.springbatch.global.util.SecurityUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Optional;

/**
 * WebMvc 설정
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * JPA Auditing을 위한 현재 사용자 제공
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.ofNullable(SecurityUtils.getCurrentUsername())
                .or(() -> Optional.of("SYSTEM"));
    }
}
