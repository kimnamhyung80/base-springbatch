package com.framework.springbatch.global.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.framework.springbatch.global.common.dto.ApiResponse;
import com.framework.springbatch.global.error.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 접근 거부 처리
 */
@Slf4j
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, 
                      HttpServletResponse response,
                      AccessDeniedException accessDeniedException) throws IOException {
        
        log.error("Access denied: {}, URI: {}", accessDeniedException.getMessage(), request.getRequestURI());

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiResponse<Void> apiResponse = ApiResponse.error(ErrorCode.ACCESS_DENIED);
        
        objectMapper.findAndRegisterModules();
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
