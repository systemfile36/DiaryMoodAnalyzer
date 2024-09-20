package org.diarymoodanalyzer.config.jwt;

import io.jsonwebtoken.Jwts;
import org.diarymoodanalyzer.domain.User;
import org.diarymoodanalyzer.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TokenProviderTest {
    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtProperties jwtProperties;

    @BeforeEach
    public void resetRepository() {
        userRepository.deleteAll();
    }

    @DisplayName("createToken: User와 만료 시간을 받아 토큰을 생성한다.")
    @Test
    public void createToken() {
        //테스트용 User 객체
        User testUser = userRepository.save(User.builder()
                .email("test@example.com")
                .password("password")
                .build());

        //실제로 토큰 생성
        String token = tokenProvider.createToken(testUser, Duration.ofHours(1));

        Long userId = Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody()
                .get("id", Long.class);

        //일치 여부 확인
        assertThat(userId).isEqualTo(testUser.getUserId());
    }

    @DisplayName("validateToken: 만료된 토큰에 대해 false를 반환한다.")
    @Test
    public void validateToken_case_invalid() {

        //빌더를 통해 만료 시간만 지정하여 토큰 생성
        String token = JwtFactory.builder()
                .expire(new Date(System.currentTimeMillis() - Duration.ofDays(1).toMillis()))
                .build()
                .createToken(jwtProperties);


        boolean result = tokenProvider.validateToken(token);

        assertThat(result).isFalse();

    }

    @DisplayName("validateToken: 유효한 토큰에 대해 true를 반환한다.")
    @Test
    public void validateToken_case_valid() {

        //기본 값으로 토큰 생성
        String token = JwtFactory.builder().build().createToken(jwtProperties);

        boolean result = tokenProvider.validateToken(token);

        assertThat(result).isTrue();
    }

    @DisplayName("getAuthentication: 토큰에서 인증 정보를 Authentication으로 반환한다.")
    @Test
    public void getAuthentication() {

        String userEmail = "test@gmail.com";
        String password = "password";

        userRepository.save(User.builder()
                .email(userEmail)
                .password(password)
                .build());

        String token = JwtFactory.builder()
                .subject(userEmail)
                .build().createToken(jwtProperties);

        //토큰에서 인증 정보를 받아옴
        Authentication auth = tokenProvider.getAuthentication(token);

        //인증 객체의 Principal(여기선 UserDetails)을 받아와서 사용자 이름을 비교한다.
        assertThat(((UserDetails)auth.getPrincipal()).getUsername()).isEqualTo(userEmail);

    }

    @DisplayName("getUserId: 토큰에서 User의 id값을 가져온다.")
    @Test
    public void getUserId() {
        Long userId = 1L;

        String token = JwtFactory.builder()
                .claims(Map.of("id", userId)) //토큰에 id 값을 설정
                .build().createToken(jwtProperties);

        Long result = tokenProvider.getUserId(token);

        assertThat(result).isEqualTo(userId);
    }
}
