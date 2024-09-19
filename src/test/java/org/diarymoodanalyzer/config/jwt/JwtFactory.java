package org.diarymoodanalyzer.config.jwt;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

/*
테스트를 위한 객체. 테스트용 데이터를 생성하고 저장한다.
테스트에 필요한 부분만 초기화하고, 사용한다.
 */
public class JwtFactory {

    private String subject;
    private Date issuedAt;
    private Date expire;
    private Map<String, Object> claims;
    private GrantedAuthority authority;

    /*
    빌더 패턴에서 사용하기 위해 작성한 private 생성자
     */
    private JwtFactory(String subject, Date issuedAt, Date expire,
                      Map<String, Object> claims, GrantedAuthority authority) {
        this.subject = subject;
        this.issuedAt = issuedAt;
        this.expire = expire;
        this.claims = claims;
        this.authority = authority;
    }

    /*
    자신의 필드 값을 기반으로 토큰 생성
     */
    public String createToken(JwtProperties jwtProperties) {
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) //jsonwebtoken의 상수로 헤더 설정 { typ : "JWT" }
                .setIssuer(jwtProperties.getIssuer()) // 발급자, iss
                .setIssuedAt(issuedAt) // 발급 시간, iat
                .setExpiration(expire) // 만료 시간, exp
                .setSubject(subject) // 제목, sub. 사용자의 이메일
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey()) // 서명 설정. HS256과 비밀 키로 서명
                .compact();
    }

    //이하 빌더 패턴

    public JwtFactoryBuilder build() {
        return new JwtFactoryBuilder();
    }

    public static class JwtFactoryBuilder {
        //각 필드에 기본값들을 지정
        private String subject = "test@example.com";
        private Date issuedAt = new Date();
        private Date expire = new Date(System.currentTimeMillis() + Duration.ofHours(1).toMillis());
        private Map<String, Object> claims = Collections.emptyMap();
        private GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");

        public JwtFactoryBuilder() {}

        public JwtFactory build() {
            return new JwtFactory(subject, issuedAt, expire, claims, authority);
        }

        public JwtFactoryBuilder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public JwtFactoryBuilder issuedAt(Date issuedAt) {
            this.issuedAt = issuedAt;
            return this;
        }

        public JwtFactoryBuilder expire(Date expire) {
            this.expire = expire;
            return this;
        }

        public JwtFactoryBuilder claims(Map<String, Object> claims) {
            this.claims = claims;
            return this;
        }

        public JwtFactoryBuilder authority(GrantedAuthority authority) {
            this.authority = authority;
            return this;
        }

    }
}
