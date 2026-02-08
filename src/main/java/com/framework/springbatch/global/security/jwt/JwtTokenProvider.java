package com.framework.springbatch.global.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * JWT 토큰 제공자
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private static final String AUTHORITIES_KEY = "roles";
    private static final String USER_ID_KEY = "userId";

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-validity:86400}")
    private long accessTokenValidity;

    @Value("${jwt.refresh-token-validity:604800}")
    private long refreshTokenValidity;

    @Value("${jwt.issuer:springbatch}")
    private String issuer;

    private SecretKey key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Access Token 생성
     */
    public String createAccessToken(Authentication authentication) {
        return createToken(authentication, accessTokenValidity * 1000);
    }

    /**
     * Refresh Token 생성
     */
    public String createRefreshToken(Authentication authentication) {
        return createToken(authentication, refreshTokenValidity * 1000);
    }

    /**
     * 토큰 생성
     */
    private String createToken(Authentication authentication, long validityInMilliseconds) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = System.currentTimeMillis();
        Date validity = new Date(now + validityInMilliseconds);

        return Jwts.builder()
                .subject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .issuer(issuer)
                .issuedAt(new Date(now))
                .expiration(validity)
                .signWith(key)
                .compact();
    }

    /**
     * 토큰에서 인증 정보 추출
     */
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .filter(auth -> !auth.isEmpty())
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    /**
     * 토큰 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Claims 파싱
     */
    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    /**
     * 토큰에서 사용자명 추출
     */
    public String getUsername(String token) {
        return parseClaims(token).getSubject();
    }
}
