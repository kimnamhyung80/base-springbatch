# ═══════════════════════════════════════════════════════════════════════════════
# Dockerfile for Spring Batch Enterprise Framework
# ═══════════════════════════════════════════════════════════════════════════════
# Multi-stage build for optimized image size

# ───────────────────────────────────────────────────────────────────────────────
# Stage 1: Build
# ───────────────────────────────────────────────────────────────────────────────
FROM gradle:8.5-jdk17 AS builder

WORKDIR /app

# Gradle 캐시 활용을 위해 의존성 파일 먼저 복사
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# 의존성 다운로드 (캐시 레이어)
RUN gradle dependencies --no-daemon || true

# 소스 코드 복사
COPY src ./src

# 빌드
RUN gradle build -x test --no-daemon

# ───────────────────────────────────────────────────────────────────────────────
# Stage 2: Runtime
# ───────────────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

# 메타데이터
LABEL maintainer="framework@company.com"
LABEL description="Spring Batch Enterprise Framework"
LABEL version="1.0.0"

# 보안: 비-root 사용자 생성
RUN addgroup -S spring && adduser -S spring -G spring

# 타임존 설정
ENV TZ=Asia/Seoul
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone

# 작업 디렉토리
WORKDIR /app

# 로그 디렉토리 생성
RUN mkdir -p /app/logs /app/output && \
    chown -R spring:spring /app

# 빌드된 JAR 복사
COPY --from=builder /app/build/libs/springbatch.jar app.jar

# 사용자 전환
USER spring:spring

# 포트 노출
EXPOSE 8080

# JVM 옵션 (환경변수로 오버라이드 가능)
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# 헬스체크
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/actuator/health/liveness || exit 1

# 실행
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
