-- ═══════════════════════════════════════════════════════════════════════════════
-- Spring Batch Enterprise Framework - Initial Data
-- ═══════════════════════════════════════════════════════════════════════════════

-- ───────────────────────────────────────────────────────────────────────────────
-- 기본 관리자 계정 (password: admin123!)
-- ───────────────────────────────────────────────────────────────────────────────
INSERT INTO USERS (USERNAME, PASSWORD, EMAIL, ROLE, ENABLED, CREATED_AT)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'admin@springbatch.com', 'ADMIN', true, CURRENT_TIMESTAMP);

INSERT INTO USERS (USERNAME, PASSWORD, EMAIL, ROLE, ENABLED, CREATED_AT)
VALUES ('batch', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'batch@springbatch.com', 'BATCH_OPERATOR', true, CURRENT_TIMESTAMP);

-- ───────────────────────────────────────────────────────────────────────────────
-- 공통 코드 그룹
-- ───────────────────────────────────────────────────────────────────────────────
INSERT INTO COMMON_CODE_GROUP (GROUP_CODE, GROUP_NAME, DESCRIPTION) 
VALUES ('BATCH_STATUS', '배치상태', '배치 작업 상태 코드');

INSERT INTO COMMON_CODE_GROUP (GROUP_CODE, GROUP_NAME, DESCRIPTION) 
VALUES ('PROCESS_STATUS', '처리상태', '데이터 처리 상태 코드');

INSERT INTO COMMON_CODE_GROUP (GROUP_CODE, GROUP_NAME, DESCRIPTION) 
VALUES ('USER_ROLE', '사용자역할', '사용자 권한 역할');

-- ───────────────────────────────────────────────────────────────────────────────
-- 공통 코드
-- ───────────────────────────────────────────────────────────────────────────────
-- 배치 상태
INSERT INTO COMMON_CODE (GROUP_CODE, CODE, CODE_NAME, SORT_ORDER) VALUES ('BATCH_STATUS', 'STARTING', '시작중', 1);
INSERT INTO COMMON_CODE (GROUP_CODE, CODE, CODE_NAME, SORT_ORDER) VALUES ('BATCH_STATUS', 'STARTED', '시작됨', 2);
INSERT INTO COMMON_CODE (GROUP_CODE, CODE, CODE_NAME, SORT_ORDER) VALUES ('BATCH_STATUS', 'STOPPING', '중지중', 3);
INSERT INTO COMMON_CODE (GROUP_CODE, CODE, CODE_NAME, SORT_ORDER) VALUES ('BATCH_STATUS', 'STOPPED', '중지됨', 4);
INSERT INTO COMMON_CODE (GROUP_CODE, CODE, CODE_NAME, SORT_ORDER) VALUES ('BATCH_STATUS', 'COMPLETED', '완료', 5);
INSERT INTO COMMON_CODE (GROUP_CODE, CODE, CODE_NAME, SORT_ORDER) VALUES ('BATCH_STATUS', 'FAILED', '실패', 6);
INSERT INTO COMMON_CODE (GROUP_CODE, CODE, CODE_NAME, SORT_ORDER) VALUES ('BATCH_STATUS', 'ABANDONED', '포기', 7);

-- 처리 상태
INSERT INTO COMMON_CODE (GROUP_CODE, CODE, CODE_NAME, SORT_ORDER) VALUES ('PROCESS_STATUS', 'PENDING', '대기', 1);
INSERT INTO COMMON_CODE (GROUP_CODE, CODE, CODE_NAME, SORT_ORDER) VALUES ('PROCESS_STATUS', 'PROCESSING', '처리중', 2);
INSERT INTO COMMON_CODE (GROUP_CODE, CODE, CODE_NAME, SORT_ORDER) VALUES ('PROCESS_STATUS', 'COMPLETED', '완료', 3);
INSERT INTO COMMON_CODE (GROUP_CODE, CODE, CODE_NAME, SORT_ORDER) VALUES ('PROCESS_STATUS', 'FAILED', '실패', 4);
INSERT INTO COMMON_CODE (GROUP_CODE, CODE, CODE_NAME, SORT_ORDER) VALUES ('PROCESS_STATUS', 'SKIPPED', '건너뜀', 5);

-- 사용자 역할
INSERT INTO COMMON_CODE (GROUP_CODE, CODE, CODE_NAME, SORT_ORDER) VALUES ('USER_ROLE', 'ADMIN', '관리자', 1);
INSERT INTO COMMON_CODE (GROUP_CODE, CODE, CODE_NAME, SORT_ORDER) VALUES ('USER_ROLE', 'BATCH_OPERATOR', '배치운영자', 2);
INSERT INTO COMMON_CODE (GROUP_CODE, CODE, CODE_NAME, SORT_ORDER) VALUES ('USER_ROLE', 'USER', '일반사용자', 3);

-- ───────────────────────────────────────────────────────────────────────────────
-- 배치 스케줄 샘플
-- ───────────────────────────────────────────────────────────────────────────────
INSERT INTO BATCH_SCHEDULE (JOB_NAME, CRON_EXPRESSION, ENABLED, DESCRIPTION)
VALUES ('sampleJob', '0 0 2 * * ?', true, '샘플 배치 작업 - 매일 새벽 2시 실행');

INSERT INTO BATCH_SCHEDULE (JOB_NAME, CRON_EXPRESSION, ENABLED, DESCRIPTION)
VALUES ('dataCleanupJob', '0 0 3 * * ?', true, '데이터 정리 작업 - 매일 새벽 3시 실행');

-- ───────────────────────────────────────────────────────────────────────────────
-- 배치 파라미터 샘플
-- ───────────────────────────────────────────────────────────────────────────────
INSERT INTO BATCH_PARAMETER (JOB_NAME, PARAM_KEY, PARAM_VALUE, PARAM_TYPE, DESCRIPTION)
VALUES ('sampleJob', 'chunkSize', '1000', 'INTEGER', '청크 사이즈');

INSERT INTO BATCH_PARAMETER (JOB_NAME, PARAM_KEY, PARAM_VALUE, PARAM_TYPE, DESCRIPTION)
VALUES ('sampleJob', 'skipLimit', '10', 'INTEGER', '스킵 한도');

-- ───────────────────────────────────────────────────────────────────────────────
-- 샘플 데이터 (배치 처리 대상)
-- ───────────────────────────────────────────────────────────────────────────────
INSERT INTO SAMPLE (NAME, DESCRIPTION, STATUS, AMOUNT, PROCESSED, CREATED_AT) VALUES ('Sample 001', '테스트 데이터 1', 'ACTIVE', 1000.00, false, CURRENT_TIMESTAMP);
INSERT INTO SAMPLE (NAME, DESCRIPTION, STATUS, AMOUNT, PROCESSED, CREATED_AT) VALUES ('Sample 002', '테스트 데이터 2', 'ACTIVE', 2000.00, false, CURRENT_TIMESTAMP);
INSERT INTO SAMPLE (NAME, DESCRIPTION, STATUS, AMOUNT, PROCESSED, CREATED_AT) VALUES ('Sample 003', '테스트 데이터 3', 'ACTIVE', 3000.00, false, CURRENT_TIMESTAMP);
INSERT INTO SAMPLE (NAME, DESCRIPTION, STATUS, AMOUNT, PROCESSED, CREATED_AT) VALUES ('Sample 004', '테스트 데이터 4', 'ACTIVE', 4000.00, false, CURRENT_TIMESTAMP);
INSERT INTO SAMPLE (NAME, DESCRIPTION, STATUS, AMOUNT, PROCESSED, CREATED_AT) VALUES ('Sample 005', '테스트 데이터 5', 'INACTIVE', 5000.00, false, CURRENT_TIMESTAMP);
