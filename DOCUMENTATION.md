# 🚀 Spring Batch Enterprise Framework

> 대기업 수준의 배치 처리 시스템을 위한 Spring Batch 기반 프레임워크

---

## 📋 목차

1. [개요](#개요)
2. [기술 스택](#기술-스택)
3. [프로젝트 구조](#프로젝트-구조)
4. [빠른 시작](#빠른-시작)
5. [설정 가이드](#설정-가이드)
6. [배치 Job 패턴](#배치-job-패턴)
7. [API 문서](#api-문서)
8. [보안 설정](#보안-설정)
9. [모니터링](#모니터링)
10. [배포 가이드](#배포-가이드)
11. [트러블슈팅](#트러블슈팅)

---

## 개요

### 프레임워크 특징

| 특징 | 설명 |
|------|------|
| **엔터프라이즈급 아키텍처** | 대기업 환경에 적합한 확장 가능한 구조 |
| **다양한 배치 패턴** | Chunk, Partition, Tasklet, File Export 4가지 패턴 제공 |
| **듀얼 퍼시스턴스** | JPA + MyBatis 동시 지원 |
| **분산 처리 지원** | Redis 기반 분산 락으로 중복 실행 방지 |
| **유연한 보안** | JWT 인증 On/Off 가능 |
| **클라우드 네이티브** | Docker + Kubernetes 배포 지원 |
| **실시간 모니터링** | Prometheus + Grafana 연동 |

---

## 기술 스택

### Core
| 기술 | 버전 | 용도 |
|------|------|------|
| Java | 17+ | 런타임 |
| Spring Boot | 3.2.2 | 프레임워크 |
| Spring Batch | 5.x | 배치 처리 |
| Gradle | 8.5+ | 빌드 도구 |

### Persistence
| 기술 | 버전 | 용도 |
|------|------|------|
| Spring Data JPA | 3.2.x | ORM |
| Hibernate | 6.4.x | JPA 구현체 |
| MyBatis | 3.0.x | SQL Mapper |
| H2 / PostgreSQL | - | 데이터베이스 |

### Infrastructure
| 기술 | 버전 | 용도 |
|------|------|------|
| Redis | 7.x | 분산 락, 캐시 |
| Quartz | 2.3.x | 스케줄링 |
| Docker | 24.x | 컨테이너화 |
| Kubernetes | 1.28+ | 오케스트레이션 |

### Security & Monitoring
| 기술 | 버전 | 용도 |
|------|------|------|
| Spring Security | 6.2.x | 보안 |
| JWT (jjwt) | 0.12.x | 토큰 인증 |
| Micrometer | 1.12.x | 메트릭 |
| Prometheus | - | 모니터링 |

---

## 프로젝트 구조

```
d:\dev\springbatch\
│
├── 📁 gradle/wrapper/              # Gradle Wrapper
├── 📁 src/
│   ├── 📁 main/
│   │   ├── 📁 java/com/framework/springbatch/
│   │   │   │
│   │   │   ├── 📁 batch/                    # 🔷 Batch 모듈
│   │   │   │   ├── 📁 config/               # Batch 설정
│   │   │   │   │   ├── BatchConfig.java          # 핵심 Batch 설정
│   │   │   │   │   └── BatchProperties.java      # Batch 속성
│   │   │   │   │
│   │   │   │   ├── 📁 controller/           # Batch API
│   │   │   │   │   └── BatchJobController.java   # Job 실행 API
│   │   │   │   │
│   │   │   │   ├── 📁 dto/                  # Batch DTO
│   │   │   │   │   ├── JobExecutionDTO.java
│   │   │   │   │   ├── JobLaunchRequest.java
│   │   │   │   │   └── StepExecutionDTO.java
│   │   │   │   │
│   │   │   │   ├── 📁 job/sample/           # 샘플 Job
│   │   │   │   │   ├── SampleJobConfig.java      # Chunk 패턴
│   │   │   │   │   ├── PartitionJobConfig.java   # Partition 패턴
│   │   │   │   │   ├── TaskletJobConfig.java     # Tasklet 패턴
│   │   │   │   │   └── FileExportJobConfig.java  # 파일 Export
│   │   │   │   │
│   │   │   │   ├── 📁 listener/             # Batch 리스너
│   │   │   │   │   ├── JobExecutionLogListener.java
│   │   │   │   │   ├── StepExecutionLogListener.java
│   │   │   │   │   ├── ChunkLogListener.java
│   │   │   │   │   └── SkipLogListener.java
│   │   │   │   │
│   │   │   │   └── 📁 service/              # Batch 서비스
│   │   │   │       ├── BatchJobService.java      # Job 실행 서비스
│   │   │   │       └── BatchLockService.java     # 분산 락 서비스
│   │   │   │
│   │   │   ├── 📁 domain/                   # 🔷 도메인 모듈
│   │   │   │   ├── 📁 sample/               # 샘플 도메인
│   │   │   │   │   ├── 📁 entity/
│   │   │   │   │   │   ├── Sample.java
│   │   │   │   │   │   └── SampleResult.java
│   │   │   │   │   ├── 📁 repository/
│   │   │   │   │   │   ├── SampleRepository.java
│   │   │   │   │   │   └── SampleResultRepository.java
│   │   │   │   │   └── 📁 mapper/
│   │   │   │   │       └── SampleMapper.java     # MyBatis Mapper
│   │   │   │   │
│   │   │   │   └── 📁 user/                 # 사용자 도메인
│   │   │   │       ├── 📁 entity/
│   │   │   │       │   └── User.java
│   │   │   │       ├── 📁 repository/
│   │   │   │       │   └── UserRepository.java
│   │   │   │       ├── 📁 dto/
│   │   │   │       │   └── AuthDTO.java
│   │   │   │       └── 📁 controller/
│   │   │   │           └── AuthController.java
│   │   │   │
│   │   │   ├── 📁 global/                   # 🔷 공통 모듈
│   │   │   │   ├── 📁 common/
│   │   │   │   │   ├── 📁 dto/              # 공통 DTO
│   │   │   │   │   │   ├── ApiResponse.java
│   │   │   │   │   │   └── PageResponse.java
│   │   │   │   │   ├── 📁 entity/           # 공통 엔티티
│   │   │   │   │   │   └── BaseEntity.java
│   │   │   │   │   └── 📁 enums/            # 공통 Enum
│   │   │   │   │       ├── ResultCode.java
│   │   │   │   │       └── Status.java
│   │   │   │   │
│   │   │   │   ├── 📁 config/               # 설정
│   │   │   │   │   ├── JpaConfig.java
│   │   │   │   │   ├── MyBatisConfig.java
│   │   │   │   │   ├── RedisConfig.java
│   │   │   │   │   ├── SwaggerConfig.java
│   │   │   │   │   ├── CorsConfig.java
│   │   │   │   │   └── WebMvcConfig.java
│   │   │   │   │
│   │   │   │   ├── 📁 error/                # 예외 처리
│   │   │   │   │   ├── 📁 exception/
│   │   │   │   │   │   ├── BusinessException.java
│   │   │   │   │   │   ├── EntityNotFoundException.java
│   │   │   │   │   │   └── BatchJobException.java
│   │   │   │   │   ├── ErrorCode.java
│   │   │   │   │   └── GlobalExceptionHandler.java
│   │   │   │   │
│   │   │   │   ├── 📁 security/             # 보안
│   │   │   │   │   ├── SecurityConfig.java
│   │   │   │   │   ├── 📁 jwt/
│   │   │   │   │   │   ├── JwtTokenProvider.java
│   │   │   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   │   │   ├── JwtAuthenticationEntryPoint.java
│   │   │   │   │   │   └── JwtAccessDeniedHandler.java
│   │   │   │   │   └── CustomUserDetailsService.java
│   │   │   │   │
│   │   │   │   └── 📁 util/                 # 유틸리티
│   │   │   │       ├── SecurityUtils.java
│   │   │   │       ├── StringUtils.java
│   │   │   │       └── RedisService.java
│   │   │   │
│   │   │   └── SpringBatchApplication.java  # 메인 클래스
│   │   │
│   │   └── 📁 resources/
│   │       ├── application.yml               # 메인 설정
│   │       ├── banner.txt                    # 배너
│   │       ├── logback-spring.xml            # 로깅 설정
│   │       ├── 📁 sql/
│   │       │   ├── schema.sql                # 스키마
│   │       │   └── data.sql                  # 초기 데이터
│   │       └── 📁 mapper/
│   │           └── SampleMapper.xml          # MyBatis XML
│   │
│   └── 📁 test/                             # 테스트
│
├── 📁 k8s/                                  # Kubernetes 매니페스트
│   ├── configmap.yaml
│   ├── secret.yaml
│   ├── deployment.yaml
│   └── cronjob.yaml
│
├── 📁 docker/prometheus/
│   └── prometheus.yml                        # Prometheus 설정
│
├── 📄 build.gradle                          # Gradle 빌드
├── 📄 settings.gradle
├── 📄 gradlew                               # Unix Wrapper
├── 📄 gradlew.bat                           # Windows Wrapper
├── 📄 Dockerfile                            # Docker 이미지
├── 📄 docker-compose.yml                    # Docker Compose
├── 📄 .gitignore
└── 📄 README.md
```

---

## 빠른 시작

### 1. 요구사항

- **JDK 17+** 설치
- **Docker** (선택사항 - Redis, DB 사용 시)

### 2. 프로젝트 빌드

```bash
# Windows
.\gradlew.bat build -x test

# Linux/Mac
./gradlew build -x test
```

### 3. 애플리케이션 실행

```bash
# Windows
.\gradlew.bat bootRun

# Linux/Mac
./gradlew bootRun
```

### 4. 접속 확인

| URL | 설명 |
|-----|------|
| http://localhost:8080/api | API 기본 경로 |
| http://localhost:8080/api/swagger-ui.html | Swagger UI |
| http://localhost:8080/api/h2-console | H2 Console |
| http://localhost:8080/api/actuator/health | Health Check |

### 5. 기본 계정

| 계정 | 비밀번호 | 역할 |
|------|----------|------|
| admin | admin123! | 관리자 |
| batch | admin123! | 배치 운영자 |

---

## 설정 가이드

### application.yml 주요 설정

```yaml
# 서버 설정
server:
  port: 8080
  servlet:
    context-path: /api

# 데이터베이스 설정
spring:
  datasource:
    url: jdbc:h2:mem:springbatch
    username: sa
    password: 
    driver-class-name: org.h2.Driver

# JPA 설정
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true

# Redis 설정
  data:
    redis:
      host: localhost
      port: 6379

# Batch 설정
batch:
  chunk-size: 1000
  page-size: 500
  skip-limit: 10
  retry-limit: 3
  grid-size: 4

# 보안 설정
security:
  enabled: true
  jwt:
    secret: your-secret-key-here
    expiration: 3600000
```

### 환경별 설정

| 환경변수 | 기본값 | 설명 |
|----------|--------|------|
| `SERVER_PORT` | 8080 | 서버 포트 |
| `DB_URL` | jdbc:h2:mem:springbatch | DB URL |
| `REDIS_HOST` | localhost | Redis 호스트 |
| `BATCH_CHUNK_SIZE` | 1000 | 청크 사이즈 |
| `SECURITY_ENABLED` | true | 보안 활성화 |

---

## 배치 Job 패턴

### 1. Chunk 기반 처리 (SampleJobConfig)

대량 데이터를 청크 단위로 읽고, 처리하고, 쓰는 패턴

```java
@Bean
public Step sampleStep() {
    return new StepBuilder("sampleStep", jobRepository)
        .<Sample, SampleResult>chunk(1000, transactionManager)
        .reader(sampleReader())      // 데이터 읽기
        .processor(sampleProcessor()) // 변환 처리
        .writer(sampleWriter())       // 데이터 저장
        .faultTolerant()
        .skipLimit(10)
        .skip(Exception.class)
        .build();
}
```

**사용 케이스:**
- 대량 데이터 마이그레이션
- 일별/월별 정산 처리
- 데이터 변환 작업

### 2. Partition 기반 병렬 처리 (PartitionJobConfig)

데이터를 파티션으로 분할하여 병렬 처리하는 패턴

```java
@Bean
public Step masterStep() {
    return new StepBuilder("masterStep", jobRepository)
        .partitioner("slaveStep", partitioner())
        .step(slaveStep())
        .gridSize(4)  // 4개 파티션으로 분할
        .taskExecutor(partitionTaskExecutor())
        .build();
}
```

**사용 케이스:**
- ID 범위별 병렬 처리
- 날짜별 데이터 분할 처리
- 대용량 데이터 고속 처리

### 3. Tasklet 기반 처리 (TaskletJobConfig)

단일 작업 단위로 처리하는 패턴

```java
@Bean
public Tasklet cleanupTasklet() {
    return (contribution, chunkContext) -> {
        // 정리 작업 수행
        log.info("Cleanup completed");
        return RepeatStatus.FINISHED;
    };
}
```

**사용 케이스:**
- 파일 삭제/이동
- 테이블 TRUNCATE
- 외부 API 호출

### 4. 파일 Export (FileExportJobConfig)

데이터를 CSV/파일로 내보내는 패턴

```java
@Bean
public FlatFileItemWriter<Sample> csvWriter() {
    return new FlatFileItemWriterBuilder<Sample>()
        .name("csvWriter")
        .resource(new FileSystemResource("output/export.csv"))
        .delimited()
        .names("id", "name", "amount")
        .build();
}
```

**사용 케이스:**
- 일별 리포트 생성
- 데이터 백업
- 외부 시스템 연동 파일 생성

---

## API 문서

### Batch Job API

#### Job 목록 조회
```http
GET /api/batch/jobs
```

#### Job 실행
```http
POST /api/batch/jobs/{jobName}/run
Content-Type: application/json

{
  "parameters": {
    "date": "2026-02-06",
    "chunkSize": "1000"
  }
}
```

#### Job 실행 이력 조회
```http
GET /api/batch/jobs/{jobName}/executions
```

#### Job 중지
```http
POST /api/batch/jobs/executions/{executionId}/stop
```

#### Job 재시작
```http
POST /api/batch/jobs/executions/{executionId}/restart
```

### 인증 API

#### 로그인
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123!"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600000
  }
}
```

### Health Check API

```http
GET /api/actuator/health
```

**Response:**
```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "redis": { "status": "UP" },
    "diskSpace": { "status": "UP" }
  }
}
```

---

## 보안 설정

### JWT 인증 플로우

```
┌──────────┐      ┌──────────┐      ┌──────────┐
│  Client  │──1──▶│   Auth   │──2──▶│   JWT    │
│          │◀──3──│Controller│◀─────│ Provider │
└──────────┘      └──────────┘      └──────────┘
     │                                    │
     │                                    │
     ▼                                    │
┌──────────┐      ┌──────────┐           │
│  Request │──4──▶│   JWT    │◀──5───────┘
│  + Token │      │  Filter  │
└──────────┘      └──────────┘
                       │
                       ▼
                 ┌──────────┐
                 │ Security │
                 │ Context  │
                 └──────────┘
```

1. 클라이언트가 로그인 요청
2. AuthController가 JwtTokenProvider로 토큰 생성 요청
3. JWT 토큰 반환
4. 클라이언트가 토큰과 함께 API 요청
5. JwtAuthenticationFilter가 토큰 검증

### 보안 비활성화

개발/테스트 환경에서 보안 비활성화:

```yaml
security:
  enabled: false
```

### 권한별 접근 제어

| 역할 | 설명 | 접근 권한 |
|------|------|----------|
| ADMIN | 관리자 | 모든 API |
| BATCH_OPERATOR | 배치 운영자 | 배치 실행/조회 |
| USER | 일반 사용자 | 조회만 가능 |

---

## 모니터링

### Prometheus 메트릭

```http
GET /api/actuator/prometheus
```

**주요 메트릭:**
- `spring_batch_job_seconds` - Job 실행 시간
- `spring_batch_step_seconds` - Step 실행 시간
- `spring_batch_chunk_write_seconds` - Chunk 쓰기 시간
- `jvm_memory_used_bytes` - JVM 메모리 사용량
- `hikaricp_connections_active` - DB 커넥션 수

### Grafana 대시보드

docker-compose로 모니터링 스택 실행:

```bash
docker-compose up -d prometheus grafana
```

| 서비스 | URL |
|--------|-----|
| Prometheus | http://localhost:9090 |
| Grafana | http://localhost:3000 |

### 로깅

로그 파일 위치: `logs/`

| 파일 | 내용 |
|------|------|
| `application.log` | 전체 로그 |
| `error.log` | 에러만 |
| `batch.log` | 배치 로그 |

---

## 배포 가이드

### Docker 배포

#### 1. 이미지 빌드

```bash
docker build -t springbatch-framework:latest .
```

#### 2. 컨테이너 실행

```bash
docker run -d \
  --name springbatch \
  -p 8080:8080 \
  -e DB_URL=jdbc:postgresql://db:5432/batch \
  -e REDIS_HOST=redis \
  springbatch-framework:latest
```

#### 3. Docker Compose 실행

```bash
docker-compose up -d
```

### Kubernetes 배포

#### 1. ConfigMap & Secret 적용

```bash
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml
```

#### 2. Deployment 배포

```bash
kubectl apply -f k8s/deployment.yaml
```

#### 3. CronJob 배포 (스케줄 배치)

```bash
kubectl apply -f k8s/cronjob.yaml
```

#### 4. 배포 확인

```bash
kubectl get pods -l app=springbatch
kubectl logs -f deployment/springbatch
```

### 배포 체크리스트

- [ ] 환경변수 설정 확인
- [ ] 데이터베이스 연결 확인
- [ ] Redis 연결 확인
- [ ] Health Check 정상 확인
- [ ] 로그 레벨 설정 (운영: WARN 이상)
- [ ] JVM 메모리 설정
- [ ] 보안 설정 활성화

---

## 트러블슈팅

### 자주 발생하는 문제

#### 1. Job이 중복 실행됨

**원인:** 분산 환경에서 락 미적용

**해결:**
```yaml
batch:
  lock:
    enabled: true
```

#### 2. OutOfMemoryError 발생

**원인:** 청크 사이즈가 너무 큼

**해결:**
```yaml
batch:
  chunk-size: 500  # 줄이기
  page-size: 200
```

#### 3. Job이 FAILED 상태로 중단

**원인:** 예외 발생으로 Job 실패

**해결:**
```bash
# Job 상태 확인
GET /api/batch/jobs/{jobName}/executions

# 재시작
POST /api/batch/jobs/executions/{executionId}/restart
```

#### 4. 트랜잭션 타임아웃

**원인:** 청크 처리 시간이 너무 김

**해결:**
```yaml
spring:
  transaction:
    default-timeout: 600  # 10분
```

### 로그 분석

```bash
# 에러 로그 확인
grep "ERROR" logs/application.log

# 특정 Job 로그 확인
grep "sampleJob" logs/batch.log

# 실시간 로그 모니터링
tail -f logs/application.log
```

---

## 버전 히스토리

| 버전 | 날짜 | 변경 내용 |
|------|------|----------|
| 1.0.0 | 2026-02-06 | 최초 릴리즈 |

---

## 라이선스

이 프로젝트는 MIT 라이선스를 따릅니다.

---

## 문의

- **담당자:** Framework Team
- **이메일:** framework@company.com

---

*Generated by Spring Batch Enterprise Framework*
