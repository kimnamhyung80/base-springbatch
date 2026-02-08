package com.framework.springbatch.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.Chunk;
import org.springframework.stereotype.Component;

/**
 * Chunk 처리 리스너
 * - Read, Process, Write 각 단계의 이벤트 처리
 */
@Slf4j
@Component
public class ChunkLogListener<I, O> implements 
        ItemReadListener<I>, 
        ItemProcessListener<I, O>, 
        ItemWriteListener<O> {

    // ═══════════════════════════════════════════════════════════════
    // Read Listener
    // ═══════════════════════════════════════════════════════════════

    @Override
    public void beforeRead() {
        // 필요시 구현
    }

    @Override
    public void afterRead(I item) {
        log.trace("Item Read: {}", item);
    }

    @Override
    public void onReadError(Exception ex) {
        log.error("Read Error: {}", ex.getMessage(), ex);
    }

    // ═══════════════════════════════════════════════════════════════
    // Process Listener
    // ═══════════════════════════════════════════════════════════════

    @Override
    public void beforeProcess(I item) {
        log.trace("Before Process: {}", item);
    }

    @Override
    public void afterProcess(I item, O result) {
        log.trace("After Process: {} -> {}", item, result);
    }

    @Override
    public void onProcessError(I item, Exception ex) {
        log.error("Process Error for item {}: {}", item, ex.getMessage(), ex);
    }

    // ═══════════════════════════════════════════════════════════════
    // Write Listener
    // ═══════════════════════════════════════════════════════════════

    @Override
    public void beforeWrite(Chunk<? extends O> items) {
        log.debug("Before Write: {} items", items.size());
    }

    @Override
    public void afterWrite(Chunk<? extends O> items) {
        log.debug("After Write: {} items written successfully", items.size());
    }

    @Override
    public void onWriteError(Exception ex, Chunk<? extends O> items) {
        log.error("Write Error for {} items: {}", items.size(), ex.getMessage(), ex);
    }
}
