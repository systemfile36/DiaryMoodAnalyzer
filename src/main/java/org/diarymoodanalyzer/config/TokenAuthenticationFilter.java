package org.diarymoodanalyzer.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.diarymoodanalyzer.config.jwt.TokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

/*
요청을 가로채서 토큰 기반으로 인증 정보를 저장하는 필터 클래스
OncePerRequestFilter 를 상속받아서 구현
 */
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    //토큰 추출을 위한 문자열 상수
    private final static String HEADER_AUTH = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        //헤더에서 Authorization 의 값을 불러온 뒤, 토큰 값만 추출
        String token = getAccessToken(request.getHeader(HEADER_AUTH));

        //토큰이 유효하다면
        if(tokenProvider.validateToken(token)) {
            //토큰에서 인증 정보를 받아온 뒤, SecurityContext 에 설정
            Authentication auth = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        //다음 필터로 넘김
        filterChain.doFilter(request, response);
    }

    private String getAccessToken(String authHeader) {
        //헤더에서 추출한 값이 유효하다면
        if(Objects.nonNull(authHeader) && authHeader.startsWith(TOKEN_PREFIX)) {
            //접두사를 분리해서 토큰만 반환
            return authHeader.substring(TOKEN_PREFIX.length());
        }

        return null;
    }
}
