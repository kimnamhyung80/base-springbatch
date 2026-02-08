package com.framework.springbatch.domain.sample.repository;

import com.framework.springbatch.domain.sample.entity.Sample;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 샘플 Repository (JPA)
 */
@Repository
public interface SampleRepository extends JpaRepository<Sample, Long> {

    /**
     * 상태로 조회
     */
    List<Sample> findByStatus(String status);

    /**
     * 처리되지 않은 샘플 조회
     */
    List<Sample> findByProcessedFalse();

    /**
     * 상태와 처리 여부로 페이징 조회
     */
    Page<Sample> findByStatusAndProcessed(String status, Boolean processed, Pageable pageable);

    /**
     * 처리 상태 일괄 업데이트
     */
    @Modifying
    @Query("UPDATE Sample s SET s.processed = :processed WHERE s.id IN :ids")
    int updateProcessedByIds(@Param("ids") List<Long> ids, @Param("processed") Boolean processed);

    /**
     * 미처리 건수 조회
     */
    @Query("SELECT COUNT(s) FROM Sample s WHERE s.processed = false AND s.status = :status")
    long countUnprocessedByStatus(@Param("status") String status);
}
