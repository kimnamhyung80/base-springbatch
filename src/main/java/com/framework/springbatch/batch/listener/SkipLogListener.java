package com.framework.springbatch.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

/**
 * Skip 리스너
 * - 스킵된 항목 로깅
 */
@Slf4j
@Component
public class SkipLogListener<T, S> implements SkipListener<T, S> {

    @Override
    public void onSkipInRead(Throwable t) {
        log.warn("Skip in Read: {}", t.getMessage());
    }

    @Override
    public void onSkipInWrite(S item, Throwable t) {
        log.warn("Skip in Write: item={}, error={}", item, t.getMessage());
    }

    @Override
    public void onSkipInProcess(T item, Throwable t) {
        log.warn("Skip in Process: item={}, error={}", item, t.getMessage());
    }
}
