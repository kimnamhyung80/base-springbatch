package com.framework.springbatch.global.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 문자열 관련 유틸리티
 */
public final class StringUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private StringUtils() {
        // 유틸리티 클래스
    }

    /**
     * 문자열이 비어있는지 확인
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 문자열이 비어있지 않은지 확인
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * UUID 생성
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 짧은 UUID 생성 (8자)
     */
    public static String generateShortUUID() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    /**
     * 현재 날짜 문자열 (yyyyMMdd)
     */
    public static String getCurrentDateString() {
        return LocalDate.now().format(DATE_FORMATTER);
    }

    /**
     * 현재 일시 문자열 (yyyyMMddHHmmss)
     */
    public static String getCurrentDateTimeString() {
        return LocalDateTime.now().format(DATETIME_FORMATTER);
    }

    /**
     * 배치 실행 ID 생성
     */
    public static String generateBatchRunId(String jobName) {
        return String.format("%s_%s_%s", jobName, getCurrentDateTimeString(), generateShortUUID());
    }

    /**
     * 마스킹 처리 (이메일)
     */
    public static String maskEmail(String email) {
        if (isEmpty(email) || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        String localPart = parts[0];
        if (localPart.length() <= 3) {
            return localPart.charAt(0) + "***@" + parts[1];
        }
        return localPart.substring(0, 3) + "***@" + parts[1];
    }

    /**
     * 마스킹 처리 (전화번호)
     */
    public static String maskPhone(String phone) {
        if (isEmpty(phone) || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
}
