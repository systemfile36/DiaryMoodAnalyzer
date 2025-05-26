package org.diarymoodanalyzer.util;


import org.diarymoodanalyzer.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.Optional;

/**
 * SecurityContextHolder 사용하여 인증에 대한 정보를 반환하는 유틸리티 클래스
 */
public class AuthenticationUtils {

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 현재 인증된 사용자의 이메일을 반환한다.
     * Principal의 getName을 호출한다.
     * @return 인증된 사용자의 이메일 값의 Optional. 인증 정보가 유효하지 않으면 Optional.EMPTY 반환.
     */
    public static Optional<String> getCurrentUserEmail() {
        Authentication auth = getAuthentication();

        return Optional.ofNullable(auth == null ? null : auth.getName());
    }

    public static Collection<? extends GrantedAuthority> getCurrentUserAuthorities() {
        Authentication auth = getAuthentication();
        return auth == null ? null : auth.getAuthorities();
    }

    /**
     * 현재 인증된 사용자가 author과 일치하는 권한을 가지고 있는지 여부를 반환한다.
     * @param author - 조회할 권한
     * @return 권한이 존재하면 true, otherwise, false
     */
    public static boolean hasAuthority(GrantedAuthority author) {
        Collection<? extends GrantedAuthority> authorities = getCurrentUserAuthorities();

        return authorities != null && authorities.stream()
                .anyMatch((value) -> value.getAuthority().equals(author.getAuthority()));
    }

    /*
    현재 프로젝트에서는 principal == UserDetails == User 이다.
     */

    /**
     * principal == ? extends UserDetails == User
     * @return 현재 인증된 사용자의 User 엔티티
     */
    public static User getCurrentUser() {
        Authentication auth = getAuthentication();
        if(auth != null && auth.getPrincipal() instanceof User) {
            return (User)auth.getPrincipal();
        } else {
            return null;
        }
    }

    /*
    현재 인증된 유저의 id 값을 리턴
     */
    public static Long getCurrentUserId() {
        User user = getCurrentUser();
        return user == null ? null : user.getId();
    }

    /*
    현재 프로젝트에서는 credentials == JWT token 이다.
     */
    public static String getCurrentUserToken() {
        Authentication auth = getAuthentication();
        if(auth != null && auth.getCredentials() instanceof String) {
            return (String)auth.getCredentials();
        } else {
            return null;
        }
    }
}
