package com.framework.springbatch.global.common.controller;

import com.framework.springbatch.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 헬스체크 및 시스템 정보 컨트롤러
 */
@Tag(name = "Health", description = "헬스체크 및 시스템 정보 API")
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class HealthController {

    private final Environment environment;
    private final Optional<BuildProperties> buildProperties;

    @Operation(summary = "헬스체크", description = "서버 상태를 확인합니다.")
    @GetMapping("/health")
    public ApiResponse<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("profiles", environment.getActiveProfiles());
        
        buildProperties.ifPresent(props -> {
            health.put("version", props.getVersion());
            health.put("name", props.getName());
            health.put("buildTime", props.getTime());
        });
        
        return ApiResponse.success(health);
    }

    @Operation(summary = "배치 상태 체크", description = "배치 시스템 상태를 확인합니다.")
    @GetMapping("/batch/health")
    public ApiResponse<Map<String, Object>> batchHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("batchEnabled", true);
        health.put("timestamp", LocalDateTime.now());
        
        return ApiResponse.success(health);
    }
}
