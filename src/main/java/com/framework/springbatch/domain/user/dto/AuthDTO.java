package com.framework.springbatch.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 인증 관련 DTO
 */
public class AuthDTO {

    /**
     * 로그인 요청
     */
    @Getter
    @Setter
    public static class LoginRequest {
        @NotBlank(message = "사용자명은 필수입니다.")
        private String username;

        @NotBlank(message = "비밀번호는 필수입니다.")
        private String password;
    }

    /**
     * 로그인 응답
     */
    @Getter
    @Setter
    public static class LoginResponse {
        private String accessToken;
        private String refreshToken;
        private String tokenType = "Bearer";
        private Long expiresIn;

        public LoginResponse(String accessToken, String refreshToken, Long expiresIn) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.expiresIn = expiresIn;
        }
    }

    /**
     * 토큰 갱신 요청
     */
    @Getter
    @Setter
    public static class RefreshRequest {
        @NotBlank(message = "Refresh 토큰은 필수입니다.")
        private String refreshToken;
    }
}
