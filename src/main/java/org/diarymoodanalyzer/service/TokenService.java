package org.diarymoodanalyzer.service;

import lombok.RequiredArgsConstructor;
import org.diarymoodanalyzer.config.jwt.TokenProvider;
import org.diarymoodanalyzer.domain.User;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    public String createNewAccessToken(String refreshToken) {
        //유효성 검사
        if(!tokenProvider.validateToken(refreshToken))
            throw new IllegalArgumentException("invalid token: " + refreshToken);

        //DB 에서 RefreshToken 엔티티를 가져온 뒤, userId 값을 가져옴
        Long userId = refreshTokenService.findByRefreshToken(refreshToken)
                .getUserId();

        User user = userService.findById(userId);

        //엑세스 토큰 반환
        return tokenProvider.createToken(user, TokenProvider.ACCESS_EXPIRE);
    }
}
