package org.diarymoodanalyzer.domain;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;

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

    public String getAuthorityString() {
        return authority.getAuthority();
    }

    /**
     * 권한을 받아서 UserAuthority enum을 반환한다.
     * GrantedAuthority.getAuthority()를 통해 구분한다.
     * @param author - 조회할 권한 인터페이스 (? extends GrantedAuthority )
     * @return - 해당하는 UserAuthority, 없으면 null
     */
    public static UserAuthority valeOfAuthority(GrantedAuthority author) {
        return Arrays.stream(values())
                .filter((value) -> value.authority.getAuthority().equals(author.getAuthority()))
                .findAny()
                .orElse(null);
    }

    /**
     * 권한을 문자열로 받아서 UserAuthority enum을 반환한다.
     * GrantedAuthority.getAuthority()를 통해 구분한다.
     * @param author - 조회할 권한의 문자열 ex) "ROLE_EXPERT", "ROLE_USER",
     * @return - 해당하는 UserAuthority, 없으면 null
     */
    public static UserAuthority valueOfAuthority(String author) {
        return Arrays.stream(values())
                .filter((value) -> value.authority.getAuthority().equals(author))
                .findAny()
                .orElse(null);
    }

    UserAuthority(GrantedAuthority authority) {
        this.authority = authority;
    }
}
