package com.framework.springbatch.domain.sample.repository;

import com.framework.springbatch.domain.sample.entity.SampleResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 샘플 결과 Repository
 */
@Repository
public interface SampleResultRepository extends JpaRepository<SampleResult, Long> {

    List<SampleResult> findBySampleId(Long sampleId);

    List<SampleResult> findByJobExecutionId(Long jobExecutionId);
}
