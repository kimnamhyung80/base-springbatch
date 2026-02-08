package com.framework.springbatch.global.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Redis 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 값 저장
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 값 저장 (만료시간 포함)
     */
    public void set(String key, Object value, Duration timeout) {
        redisTemplate.opsForValue().set(key, value, timeout);
    }

    /**
     * 값 저장 (만료시간 포함)
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 값 조회
     */
    public Optional<Object> get(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    /**
     * 문자열 값 조회
     */
    public Optional<String> getString(String key) {
        return Optional.ofNullable(stringRedisTemplate.opsForValue().get(key));
    }

    /**
     * 키 존재 여부 확인
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 키 삭제
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 키 만료시간 설정
     */
    public void expire(String key, Duration timeout) {
        redisTemplate.expire(key, timeout);
    }

    /**
     * 분산 락 획득 시도
     */
    public boolean tryLock(String lockKey, String value, Duration timeout) {
        Boolean result = stringRedisTemplate.opsForValue()
                .setIfAbsent(lockKey, value, timeout);
        return Boolean.TRUE.equals(result);
    }

    /**
     * 분산 락 해제
     */
    public void unlock(String lockKey, String value) {
        String currentValue = stringRedisTemplate.opsForValue().get(lockKey);
        if (value.equals(currentValue)) {
            stringRedisTemplate.delete(lockKey);
        }
    }

    /**
     * 증가 연산
     */
    public Long increment(String key) {
        return stringRedisTemplate.opsForValue().increment(key);
    }

    /**
     * 증가 연산 (지정값)
     */
    public Long increment(String key, long delta) {
        return stringRedisTemplate.opsForValue().increment(key, delta);
    }
}
