package com.framework.springbatch.global.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.framework.springbatch.global.common.dto.ApiResponse;
import com.framework.springbatch.global.error.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 인증 실패 처리
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, 
                        HttpServletResponse response,
                        AuthenticationException authException) throws IOException {
        
        log.error("Unauthorized error: {}, URI: {}", authException.getMessage(), request.getRequestURI());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiResponse<Void> apiResponse = ApiResponse.error(ErrorCode.UNAUTHORIZED);
        
        objectMapper.findAndRegisterModules();
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
