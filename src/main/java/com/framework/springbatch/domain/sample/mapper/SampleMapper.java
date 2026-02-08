package com.framework.springbatch.domain.sample.mapper;

import com.framework.springbatch.domain.sample.dto.SampleDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 샘플 Mapper (MyBatis)
 */
@Mapper
public interface SampleMapper {

    /**
     * 미처리 샘플 목록 조회 (페이징)
     */
    List<SampleDTO> selectUnprocessedSamples(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 상태별 샘플 목록 조회
     */
    List<SampleDTO> selectByStatus(@Param("status") String status);

    /**
     * 처리 상태 일괄 업데이트
     */
    int updateProcessedBatch(@Param("ids") List<Long> ids, @Param("processed") boolean processed);

    /**
     * 상태별 건수 통계
     */
    List<Map<String, Object>> selectCountByStatus();

    /**
     * ID 범위로 조회 (파티셔닝용)
     */
    List<SampleDTO> selectByIdRange(@Param("minId") Long minId, @Param("maxId") Long maxId);

    /**
     * 최소/최대 ID 조회
     */
    Map<String, Long> selectMinMaxId();
}
