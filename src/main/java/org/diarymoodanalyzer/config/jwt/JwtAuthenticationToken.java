package org.diarymoodanalyzer.config.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/*
JWT 토큰 기반으로 인증 정보를 저장하는 Authentication (UsernamePasswordAuthenticationToken 을 대체하기 위함)
SecurityContext 에 저장하기 위함
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    //UserDetails 타입의 인증 주체
    private final UserDetails principal;

    //credentials 를 JWT 토큰으로 설정
    private final String token;


    public JwtAuthenticationToken(
            UserDetails principal, String token, Collection<? extends GrantedAuthority> authorities
    ) {
        //부모 생성자 호출하여 권한 설정
        super(authorities);
        this.principal = principal;
        this.token = token;

        //권한 설정 여부 true
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return this.token;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }
}
