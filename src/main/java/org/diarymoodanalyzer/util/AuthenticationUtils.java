package org.diarymoodanalyzer.util;


import org.diarymoodanalyzer.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;

/*
SecurityContextHolder 사용하여 인증에 대한 정보를 반환하는 유틸리티 클래스
 */
public class AuthenticationUtils {

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /*
    현재 프로젝트에서는 Username == userEmail 이다.
     */
    public static String getCurrentUserEmail() {
        Authentication auth = getAuthentication();
        return auth == null ? null : auth.getName();
    }

    public static Collection<? extends GrantedAuthority> getCurrentUserAuthorities() {
        Authentication auth = getAuthentication();
        return auth == null ? null : auth.getAuthorities();
    }

    /*
    현재 프로젝트에서는 principal == UserDetails == User 이다.
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
        return user == null ? null : user.getUserId();
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
