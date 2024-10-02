package org.diarymoodanalyzer.service;

import jakarta.transaction.Transactional;
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
    리프레쉬 토큰을 DB에 저장하거나 업데이트
     */
    public void saveOrUpdate(Long userId, String refreshToken) {
        //Optional 의 map과 orElse를 사용한다.
        //만약 해당 userId에 해당하는 리프레쉬 토큰이 존재한다면 map의 결과물이,
        //없다면 .orElse의 결과물이 token에 할당된다.
        RefreshToken token = refreshTokenRepository.findByUserId(userId)
                        .map(entity -> entity.update(refreshToken))
                        .orElse(new RefreshToken(userId, refreshToken));

        refreshTokenRepository.save(token);
    }

    @Transactional
    public void deleteByUserId(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}
