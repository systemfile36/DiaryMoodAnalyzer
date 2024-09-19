package org.diarymoodanalyzer.config.jwt;

import io.jsonwebtoken.Jwts;
import org.diarymoodanalyzer.domain.User;
import org.diarymoodanalyzer.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TokenProviderTest {
    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtProperties jwtProperties;

    @DisplayName("createToken: User와 만료 시간을 받아 토큰을 생성한다.")
    @Test
    public void createToken() {

        User testUser = userRepository.save(User.builder()
                .email("test@example.com")
                .password("password")
                .build());

        String token = tokenProvider.createToken(testUser, Duration.ofHours(1));

        Long userId = Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody()
                .get("id", Long.class);

        assertThat(userId).isEqualTo(testUser.getUserId());
    }
}
