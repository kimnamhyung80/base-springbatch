package com.framework.springbatch.batch.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 배치 프레임워크 속성
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "batch")
public class BatchProperties {

    /**
     * 기본 청크 사이즈
     */
    private int chunkSize = 1000;

    /**
     * 페이지 사이즈 (Paging Reader용)
     */
    private int pageSize = 1000;

    /**
     * 스킵 한도
     */
    private int skipLimit = 10;

    /**
     * 재시도 한도
     */
    private int retryLimit = 3;

    /**
     * 스로틀 한도 (병렬 처리)
     */
    private int throttleLimit = 4;

    /**
     * 그리드 사이즈 (파티셔닝)
     */
    private int gridSize = 4;

    /**
     * 분산 락 설정
     */
    private Lock lock = new Lock();

    /**
     * 알림 설정
     */
    private Notification notification = new Notification();

    @Getter
    @Setter
    public static class Lock {
        private boolean enabled = true;
        private long timeout = 60000;
        private String prefix = "batch:lock:";
    }

    @Getter
    @Setter
    public static class Notification {
        private boolean enabled = false;
        private String slackWebhook;
        private boolean emailEnabled = false;
    }
}
