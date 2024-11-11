package org.diarymoodanalyzer.config.jwt;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.diarymoodanalyzer.domain.User;
import org.diarymoodanalyzer.service.UserDetailService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.stream.Collectors;

/*
토큰을 생성, 토큰에서 인증 정보 추출 등의 기능을 하는 클래스
 */
@RequiredArgsConstructor
@Service
public class TokenProvider {

    private final JwtProperties jwtProperties;

    private final UserDetailService userDetailService;

    //토큰 생성 측에서 사용할 상수
    public static final Duration REFRESH_EXPIRE = Duration.ofHours(3);
    public static final Duration ACCESS_EXPIRE = Duration.ofMinutes(10);

    /*
    User 객체와 Duration 타입으로 만료까지의 시간을 받아서 JWT 토큰을 만들어 반환
     */
    public String createToken(User user, Duration expiredAt) {

        //현재 시간에 Duration을 더해서 Date 객체로 만듦.
        Date expire = new Date(System.currentTimeMillis() + expiredAt.toMillis());

        //토큰의 클레임에 권한을 설정하기 위해 User에서 권한 추출
        String role = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority) //GrantedAuthority 인터페이스에 정의된 메소드로 매핑
                .collect(Collectors.joining()); //스트림을 하나의 문자열로 결합한다.

        //jsonwebtoken 의 빌더로 토큰 생성 후 리턴
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) //jsonwebtoken의 상수로 헤더 설정 { typ : "JWT" }
                .setIssuer(jwtProperties.getIssuer()) // 발급자, iss
                .setIssuedAt(expire) // 발급 시간, iat
                .setExpiration(expire) // 만료 시간, exp
                .setSubject(user.getEmail()) // 제목, sub. 사용자의 이메일
                .claim("id", user.getId()) //사용자의 id
                .claim("role", role) // 사용자의 권한
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey()) // 서명 설정. HS256과 비밀 키로 서명
                .compact();
    }

    /*
    토큰의 유효성 검사
     */
    public boolean validateToken(String token) {

        try {
            //실제로 파싱을 해봄
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey()) //비밀키로 복호화
                    .parseClaimsJws(token);
            //파싱이 오류없이 이루어졌다면 유효함
            return true;
        } catch(SignatureException e) {
            //서명이 유효하지 않음. 추후 로깅 추가
        } catch(MalformedJwtException e) {
            //JWT 형식이 유효하지 않음
        } catch(ExpiredJwtException e) {
            //JWT 가 만료됨
        } catch(UnsupportedJwtException e) {
            //지원하지 않는 JWT
        } catch(IllegalArgumentException e) {
            //null or empty
        } catch(Exception e) {
            //Unexpected Exception
        }

        //오류가 발생하여 catch 블록으로 이동했다면 false를 반환
        return false;
    }

    /*
    토큰에서 인증 정보를 추출하여 Authentication 구현 객체로 반환
     */
    public Authentication getAuthentication(String token) {
        //토큰에서 클레임들을 추출한다.
        Claims claims = getClaims(token);

        //UserDetails 를 불러옴. 클레임에서 sub 값(사용자 이메일)을 받아와서 찾는다.
        UserDetails user = userDetailService.loadUserByUsername(claims.getSubject());

        //principal 을 UserDetails 타입 user, credential 을 JWT 토큰 그 자체로, 권한을 UserDetails 에 정의된 대로 설정하였다.
        //Authentication 을 구현한 객체임.
        return new JwtAuthenticationToken(user, token, user.getAuthorities());
    }

    /*
    토큰에서 User의 id를 불러옴
     */
    public Long getUserId(String token) {
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }
}
