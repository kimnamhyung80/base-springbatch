package com.framework.springbatch.global.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Security 관련 유틸리티
 */
public final class SecurityUtils {

    private SecurityUtils() {
        // 유틸리티 클래스
    }

    /**
     * 현재 인증된 사용자명 조회
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            return (String) principal;
        }
        
        return null;
    }

    /**
     * 현재 인증된 사용자 ID 조회
     */
    public static Long getCurrentUserId() {
        // CustomUserPrincipal 사용 시 구현
        return null;
    }

    /**
     * 현재 사용자가 특정 역할을 가지고 있는지 확인
     */
    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null) {
            return false;
        }
        
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
    }

    /**
     * 현재 사용자가 인증되어 있는지 확인
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() 
                && !"anonymousUser".equals(authentication.getPrincipal());
    }
}
