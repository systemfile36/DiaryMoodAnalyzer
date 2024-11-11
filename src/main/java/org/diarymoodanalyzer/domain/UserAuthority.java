package org.diarymoodanalyzer.domain;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * 사용자의 권한을 정리한 Enum
 * GrantedAuthority 타입으로 권한을 저장한다.
 */
@Getter
public enum UserAuthority {
    USER(new SimpleGrantedAuthority("ROLE_USER")),
    EXPERT(new SimpleGrantedAuthority("ROLE_EXPERT"))
    ;

    private final GrantedAuthority authority;

    UserAuthority(GrantedAuthority authority) {
        this.authority = authority;
    }
}
