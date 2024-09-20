package org.diarymoodanalyzer.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId; //UNIQUE 로 설정하여 인덱싱

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    public RefreshToken(Long userId, String refreshToken) {
        this.userId = userId; this.refreshToken = refreshToken;
    }

    /*
    토큰 갱신 시에 사용할 메소드.
    JPA의 변경 감시 기능을 이용
     */
    public RefreshToken update(String updatedToken) {
        this.refreshToken = updatedToken;
        return this;
    }
}
