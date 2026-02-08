package com.framework.springbatch.batch.service;

import com.framework.springbatch.batch.config.BatchProperties;
import com.framework.springbatch.global.common.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

/**
 * 배치 분산 락 서비스
 * - Redis를 활용한 분산 환경에서의 배치 중복 실행 방지
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BatchLockService {

    private final RedisService redisService;
    private final BatchProperties batchProperties;

    private static final ThreadLocal<String> LOCK_VALUE_HOLDER = new ThreadLocal<>();

    /**
     * 배치 락 획득 시도
     *
     * @param jobName 작업명
     * @return 락 획득 성공 여부
     */
    public boolean tryLock(String jobName) {
        if (!batchProperties.getLock().isEnabled()) {
            log.debug("Batch lock is disabled");
            return true;
        }

        String lockKey = getLockKey(jobName);
        String lockValue = generateLockValue();
        Duration timeout = Duration.ofMillis(batchProperties.getLock().getTimeout());

        boolean acquired = redisService.tryLock(lockKey, lockValue, timeout);

        if (acquired) {
            LOCK_VALUE_HOLDER.set(lockValue);
            log.info("Batch lock acquired: jobName={}, lockKey={}", jobName, lockKey);
        } else {
            log.warn("Failed to acquire batch lock: jobName={}, lockKey={}", jobName, lockKey);
        }

        return acquired;
    }

    /**
     * 배치 락 해제
     *
     * @param jobName 작업명
     */
    public void unlock(String jobName) {
        if (!batchProperties.getLock().isEnabled()) {
            return;
        }

        String lockKey = getLockKey(jobName);
        String lockValue = LOCK_VALUE_HOLDER.get();

        if (lockValue != null) {
            redisService.unlock(lockKey, lockValue);
            LOCK_VALUE_HOLDER.remove();
            log.info("Batch lock released: jobName={}, lockKey={}", jobName, lockKey);
        }
    }

    /**
     * 락 키 생성
     */
    private String getLockKey(String jobName) {
        return batchProperties.getLock().getPrefix() + jobName;
    }

    /**
     * 락 값 생성 (고유 식별자)
     */
    private String generateLockValue() {
        return UUID.randomUUID().toString();
    }

    /**
     * 락이 존재하는지 확인
     */
    public boolean isLocked(String jobName) {
        if (!batchProperties.getLock().isEnabled()) {
            return false;
        }
        return redisService.hasKey(getLockKey(jobName));
    }
}
