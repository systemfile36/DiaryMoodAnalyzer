package org.diarymoodanalyzer.service;

import lombok.RequiredArgsConstructor;
import org.diarymoodanalyzer.domain.RefreshToken;
import org.diarymoodanalyzer.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(()->new IllegalArgumentException("not found: token " + refreshToken));
    }

    /*
    리프레쉬 토큰을 DB에 저장
     */
    public RefreshToken save(Long userId, String refreshToken) {
        return refreshTokenRepository.save(new RefreshToken(userId, refreshToken));
    }
}
