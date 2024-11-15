package org.diarymoodanalyzer.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 전문가 엔티티
 * User 와 1대다 양방향 관계로 매핑된다.
 * 기본적으로 `ROLE_EXPERT`의 권한을 가진다.
 */
@NoArgsConstructor
@Getter
@Entity
@Table(name = "experts")
public class Expert extends User {
    /**
     * 전문가가 관리하는 사용자들 목록
     * JPA 에서 users 테이블의 FK 를 통해서 채워줄 것
     */
    @OneToMany(mappedBy = "expert") //관계의 주인이 자신이 아님을 나타냄
    private final List<User> managedUsers = new ArrayList<>();

    /**
     * 전문가가 작성한 코멘트들
     * Comment와 1대다 관계
     */
    @OneToMany(mappedBy = "expert")
    private final List<Comment> comments = new ArrayList<>();


    public Expert(String email, String password) {
        this.setEmail(email); this.setPassword(password);

        //권한을 전문가로 설정
        this.setAuthority(UserAuthority.EXPERT);
    }

    /**
     *
     * @param user - 해당 전문가의 관리를 받을 사용자의 엔티티
     */
    public void addManagedUser(User user) {
        //양방향 관계 설정
        this.managedUsers.add(user);
        user.setExpert(this);
    }

    /**
     * 전문가의 권한. "ROLE_EXPERT"이다.
     * @return List.of(UserAuthority.EXPERT.getAuthority());
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return List.of(UserAuthority.EXPERT.getAuthority());
    }
}
