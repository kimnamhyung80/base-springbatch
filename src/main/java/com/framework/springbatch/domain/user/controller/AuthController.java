package com.framework.springbatch.domain.user.controller;

import com.framework.springbatch.domain.user.dto.AuthDTO;
import com.framework.springbatch.global.common.dto.ApiResponse;
import com.framework.springbatch.global.security.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 컨트롤러
 */
@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.access-token-validity:86400}")
    private long accessTokenValidity;

    @Operation(summary = "로그인", description = "사용자 로그인 및 JWT 토큰 발급")
    @PostMapping("/login")
    public ApiResponse<AuthDTO.LoginResponse> login(
            @Valid @RequestBody AuthDTO.LoginRequest request) {
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        return ApiResponse.success(
                new AuthDTO.LoginResponse(accessToken, refreshToken, accessTokenValidity),
                "로그인 성공"
        );
    }

    @Operation(summary = "토큰 갱신", description = "Refresh 토큰으로 Access 토큰 갱신")
    @PostMapping("/refresh")
    public ApiResponse<AuthDTO.LoginResponse> refresh(
            @Valid @RequestBody AuthDTO.RefreshRequest request) {
        
        if (!jwtTokenProvider.validateToken(request.getRefreshToken())) {
            return ApiResponse.error(401, "A003", "유효하지 않은 Refresh 토큰입니다.");
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(request.getRefreshToken());
        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        return ApiResponse.success(
                new AuthDTO.LoginResponse(accessToken, refreshToken, accessTokenValidity),
                "토큰 갱신 성공"
        );
    }
}
