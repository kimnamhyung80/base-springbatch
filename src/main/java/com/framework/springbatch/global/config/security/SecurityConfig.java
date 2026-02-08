package com.framework.springbatch.global.config.security;

import com.framework.springbatch.global.security.jwt.JwtAccessDeniedHandler;
import com.framework.springbatch.global.security.jwt.JwtAuthenticationEntryPoint;
import com.framework.springbatch.global.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Spring Security 설정
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Value("${security.enabled:true}")
    private boolean securityEnabled;

    /**
     * 인증 제외 경로
     */
    private static final String[] AUTH_WHITELIST = {
            // API 인증
            "/v1/auth/**",
            "/v1/health",
            "/v1/batch/health",
            
            // Swagger
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            
            // Actuator
            "/actuator/**",
            
            // H2 Console
            "/h2-console/**",
            
            // 에러
            "/error"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CORS 설정
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            
            // CSRF 비활성화 (JWT 사용)
            .csrf(AbstractHttpConfigurer::disable)
            
            // H2 Console을 위한 Frame Options 설정
            .headers(headers -> headers
                    .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
            
            // 세션 사용 안함 (Stateless)
            .sessionManagement(session -> 
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 예외 처리
            .exceptionHandling(exception -> exception
                    .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                    .accessDeniedHandler(jwtAccessDeniedHandler));

        // Security 활성화 여부에 따른 인가 설정
        if (securityEnabled) {
            http.authorizeHttpRequests(auth -> auth
                    .requestMatchers(AUTH_WHITELIST).permitAll()
                    .requestMatchers("/v1/batch/jobs/**").hasAnyRole("ADMIN", "BATCH_OPERATOR")
                    .anyRequest().authenticated());
            
            // JWT 필터 추가
            http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        } else {
            // Security 비활성화: 모든 요청 허용
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        }

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
