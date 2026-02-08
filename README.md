# 🚀 Spring Batch Enterprise Framework

> **대기업급 Spring Batch 베이스 프레임워크**  
> 확장 가능하고 안정적인 배치 처리를 위한 기반 아키텍처

---

## 📋 목차

1. [개요](#개요)
2. [기술 스택](#기술-스택)
3. [프로젝트 구조](#프로젝트-구조)
4. [핵심 기능](#핵심-기능)
5. [배치 Job 패턴](#배치-job-패턴)
6. [설정 가이드](#설정-가이드)
7. [실행 방법](#실행-방법)
8. [API 엔드포인트](#api-엔드포인트)
9. [Kubernetes 배포](#kubernetes-배포)
10. [확장 가이드](#확장-가이드)

---

## 개요

### 프레임워크 특징

| 특징 | 설명 |
|------|------|
| **Spring Batch 5.x** | 최신 Spring Batch 기반 |
| **JPA + MyBatis** | 듀얼 영속성 계층 |
| **분산 락** | Redis 기반 중복 실행 방지 |
| **JWT 인증** | API 보안 (선택적 활성화) |
| **K8s Ready** | ConfigMap/Secret/CronJob 지원 |
| **모니터링** | Prometheus + Grafana 연동 |

### 버전 정보

```
Java: 17+
Spring Boot: 3.2.2
Spring Batch: 5.1.0
Gradle: 8.5+
```

---

## 기술 스택

### Core Dependencies

```gradle
// Spring Batch
implementation 'org.springframework.boot:spring-boot-starter-batch'

// JPA + MyBatis
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
implementation 'org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.3'

// Redis (분산 락 & 캐시)
implementation 'org.springframework.boot:spring-boot-starter-data-redis'

// Quartz (스케줄링)
implementation 'org.springframework.boot:spring-boot-starter-quartz'

// JWT
implementation 'io.jsonwebtoken:jjwt-api:0.12.3'

// Monitoring
implementation 'io.micrometer:micrometer-registry-prometheus'
```

---

## 프로젝트 구조

```
src/main/java/com/framework/springbatch/
├── SpringBatchApplication.java          # 메인 애플리케이션
│
├── batch/                               # 배치 핵심 모듈
│   ├── config/
│   │   ├── BatchConfig.java            # 배치 설정 (JobLauncher, TaskExecutor)
│   │   └── BatchProperties.java        # 배치 속성
│   ├── controller/
│   │   └── BatchJobController.java     # 배치 API 컨트롤러
│   ├── dto/
│   │   ├── BatchJobInfoDTO.java
│   │   ├── BatchJobExecutionDTO.java
│   │   └── BatchJobRunRequest.java
│   ├── job/
│   │   └── sample/                     # 샘플 배치 Job
│   │       ├── SampleJobConfig.java    # Chunk 기반 Job
│   │       ├── PartitionJobConfig.java # 파티셔닝 Job
│   │       ├── TaskletJobConfig.java   # Tasklet 기반 Job
│   │       └── FileExportJobConfig.java# 파일 출력 Job
│   ├── listener/
│   │   ├── JobExecutionLogListener.java
│   │   ├── StepExecutionLogListener.java
│   │   ├── ChunkLogListener.java
│   │   └── SkipLogListener.java
│   └── service/
│       ├── BatchJobService.java        # 배치 실행 서비스
│       └── BatchLockService.java       # 분산 락 서비스
│
├── domain/                              # 도메인 계층
│   ├── sample/                          # 샘플 도메인
│   │   ├── entity/
│   │   ├── dto/
│   │   ├── repository/
│   │   └── mapper/
│   └── user/                            # 사용자 도메인
│       ├── entity/
│       ├── dto/
│       ├── repository/
│       └── controller/
│
└── global/                              # 글로벌 계층
    ├── common/
    │   ├── code/ResultCode.java
    │   ├── dto/ApiResponse.java
    │   ├── entity/BaseEntity.java
    │   └── service/RedisService.java
    ├── config/
    │   ├── db/                          # DB 설정
    │   ├── security/                    # Security 설정
    │   └── web/                         # Web 설정
    ├── error/
    │   ├── ErrorCode.java
    │   ├── GlobalExceptionHandler.java
    │   └── exception/
    ├── security/
    │   ├── jwt/
    │   └── service/
    └── util/
```

---

## 핵심 기능

### 1. 배치 실행 관리

```java
// 동기 실행
batchJobService.runJob("sampleJob", parameters);

// 비동기 실행
batchJobService.runJobAsync("sampleJob", parameters);

// 작업 중지
batchJobService.stopJob(executionId);

// 작업 재시작
batchJobService.restartJob(executionId);
```

### 2. 분산 락 (Redis)

```java
// 자동으로 분산 락 획득/해제
if (batchLockService.tryLock("sampleJob")) {
    try {
        // 배치 실행
    } finally {
        batchLockService.unlock("sampleJob");
    }
}
```

### 3. 다양한 리스너

```java
// Job 실행 리스너 - 시작/종료 로깅
@Component
public class JobExecutionLogListener implements JobExecutionListener

// Step 실행 리스너
@Component
public class StepExecutionLogListener implements StepExecutionListener

// Chunk 리스너 - Read/Process/Write 이벤트
@Component
public class ChunkLogListener<I, O> implements ItemReadListener<I>, ...

// Skip 리스너 - 스킵 항목 로깅
@Component
public class SkipLogListener<T, S> implements SkipListener<T, S>
```

---

## 배치 Job 패턴

### 1. Chunk 기반 Job (대용량 처리)

```java
@Bean
public Job sampleJob() {
    return new JobBuilder("sampleJob", jobRepository)
        .listener(jobExecutionLogListener)
        .start(sampleStep())
        .build();
}

@Bean
public Step sampleStep() {
    return new StepBuilder("sampleStep", jobRepository)
        .<Sample, SampleResult>chunk(1000, transactionManager)
        .reader(sampleReader())
        .processor(sampleProcessor())
        .writer(sampleResultWriter())
        .faultTolerant()
        .skipLimit(10)
        .skip(Exception.class)
        .retryLimit(3)
        .retry(Exception.class)
        .build();
}
```

### 2. Partitioning Job (병렬 처리)

```java
@Bean
public Job partitionJob() {
    return new JobBuilder("partitionJob", jobRepository)
        .start(partitionMasterStep())
        .build();
}

@Bean
public Step partitionMasterStep() {
    return new StepBuilder("partitionMasterStep", jobRepository)
        .partitioner("workerStep", samplePartitioner())
        .step(partitionWorkerStep())
        .gridSize(4)  // 4개 파티션 병렬 처리
        .taskExecutor(partitionTaskExecutor)
        .build();
}
```

### 3. Tasklet 기반 Job (단순 작업)

```java
@Bean
public Job dataCleanupJob() {
    return new JobBuilder("dataCleanupJob", jobRepository)
        .start(cleanupOldDataStep())
        .next(updateStatisticsStep())
        .next(archiveDataStep())
        .build();
}

@Bean
public Tasklet cleanupOldDataTasklet() {
    return (contribution, chunkContext) -> {
        // 단순 작업 로직
        jdbcTemplate.update("DELETE FROM ...");
        return RepeatStatus.FINISHED;
    };
}
```

### 4. 파일 출력 Job

```java
@Bean
public FlatFileItemWriter<Sample> fileExportWriter() {
    return new FlatFileItemWriterBuilder<Sample>()
        .name("fileExportWriter")
        .resource(new FileSystemResource("./output/export.csv"))
        .headerCallback(writer -> writer.write("ID,NAME,..."))
        .lineAggregator(lineAggregator)
        .build();
}
```

---

## 설정 가이드

### 환경변수 목록

| 환경변수 | 기본값 | 설명 |
|---------|--------|------|
| `DB_URL` | H2 In-Memory | 데이터베이스 URL |
| `DB_USERNAME` | sa | DB 사용자명 |
| `DB_PASSWORD` | (empty) | DB 비밀번호 |
| `REDIS_HOST` | localhost | Redis 호스트 |
| `BATCH_CHUNK_SIZE` | 1000 | 청크 사이즈 |
| `BATCH_SKIP_LIMIT` | 10 | 스킵 한도 |
| `BATCH_RETRY_LIMIT` | 3 | 재시도 한도 |
| `BATCH_LOCK_ENABLED` | true | 분산 락 활성화 |
| `SECURITY_ENABLED` | true | 보안 활성화 |
| `JWT_SECRET` | (base64) | JWT 시크릿 키 |

### 로컬 개발 (H2)

```bash
# 기본 실행 (H2 + 보안 비활성화)
./gradlew bootRun

# 접속
# API: http://localhost:8080/api
# Swagger: http://localhost:8080/api/swagger-ui.html
# H2 Console: http://localhost:8080/api/h2-console
```

### Docker Compose

```bash
# 전체 스택 실행 (PostgreSQL + Redis + App)
docker-compose up -d

# 모니터링 포함 실행
docker-compose --profile monitoring up -d

# 로그 확인
docker-compose logs -f springbatch
```

---

## 실행 방법

### 빌드

```bash
# 빌드
./gradlew build -x test

# 테스트 포함 빌드
./gradlew build

# JAR 실행
java -jar build/libs/springbatch.jar
```

### 배치 실행 (API)

```bash
# 로그인 (토큰 발급)
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123!"}'

# 배치 실행
curl -X POST http://localhost:8080/api/v1/batch/jobs/run \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {TOKEN}" \
  -d '{
    "jobName": "sampleJob",
    "parameters": {"date": "20260206"},
    "async": false
  }'

# 실행 상태 조회
curl http://localhost:8080/api/v1/batch/jobs/executions/{executionId} \
  -H "Authorization: Bearer {TOKEN}"
```

---

## API 엔드포인트

### 인증 API

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/v1/auth/login` | 로그인 |
| POST | `/v1/auth/refresh` | 토큰 갱신 |

### 배치 관리 API

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/v1/batch/jobs` | 전체 Job 목록 |
| GET | `/v1/batch/jobs/{jobName}` | Job 정보 조회 |
| POST | `/v1/batch/jobs/run` | Job 실행 |
| POST | `/v1/batch/jobs/executions/{id}/stop` | Job 중지 |
| POST | `/v1/batch/jobs/executions/{id}/restart` | Job 재시작 |
| GET | `/v1/batch/jobs/executions/{id}` | 실행 정보 조회 |
| GET | `/v1/batch/jobs/{jobName}/executions` | 실행 이력 |

### 시스템 API

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/v1/health` | 헬스체크 |
| GET | `/v1/batch/health` | 배치 헬스체크 |
| GET | `/actuator/prometheus` | 메트릭 |

---

## Kubernetes 배포

### 배포 구조

```
k8s/
├── configmap.yaml   # 환경 설정
├── secret.yaml      # 비밀 정보
├── deployment.yaml  # 애플리케이션 배포
└── cronjob.yaml     # 스케줄 배치 실행
```

### 배포 명령

```bash
# 네임스페이스 생성
kubectl create namespace batch

# ConfigMap & Secret 적용
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml

# 애플리케이션 배포
kubectl apply -f k8s/deployment.yaml

# CronJob 배포
kubectl apply -f k8s/cronjob.yaml

# 상태 확인
kubectl get pods -n batch
kubectl logs -f deployment/springbatch -n batch
```

---

## 확장 가이드

### 새 배치 Job 추가

```
1. batch/job/{도메인}/ 디렉토리 생성

2. Job Configuration 생성
   └── {JobName}JobConfig.java
   
3. 필요한 컴포넌트 구현
   └── Reader, Processor, Writer 또는 Tasklet

4. (선택) 커스텀 리스너 추가

5. API로 실행 테스트
```

### Job Configuration 템플릿

```java
@Configuration
@RequiredArgsConstructor
public class MyJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final BatchProperties batchProperties;
    private final JobExecutionLogListener jobListener;

    @Bean
    public Job myJob() {
        return new JobBuilder("myJob", jobRepository)
            .listener(jobListener)
            .start(myStep())
            .build();
    }

    @Bean
    public Step myStep() {
        return new StepBuilder("myStep", jobRepository)
            .<Input, Output>chunk(batchProperties.getChunkSize(), transactionManager)
            .reader(myReader())
            .processor(myProcessor())
            .writer(myWriter())
            .faultTolerant()
            .skipLimit(batchProperties.getSkipLimit())
            .skip(Exception.class)
            .build();
    }
}
```

---

## 라이선스

MIT License

---

## 변경 이력

| 버전 | 날짜 | 내용 |
|------|------|------|
| 1.0.0 | 2026-02-06 | 초기 버전 |
| - | - | Spring Batch 5.x 기반 |
| - | - | Chunk/Partition/Tasklet 패턴 |
| - | - | Redis 분산 락 |
| - | - | JWT 인증 |
| - | - | Kubernetes 배포 지원 |
