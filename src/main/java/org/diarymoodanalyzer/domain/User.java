package org.diarymoodanalyzer.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 사용자 엔티티.
 */
@NoArgsConstructor
@Getter
@Setter
@Table(name = "users")
@Entity
@Inheritance(strategy = InheritanceType.JOINED) //상속 매핑을 JOIN 형태로 하기 위함
public class User extends BaseEntity implements UserDetails { //공통 컬럼 상속

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; //AUTO INCREMENT하는 기본 키

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email; //NOT NULL, UNIQUE 중복 값 허용 안하는 제약

    @Column(name = "password", nullable = false)
    private String password; //비밀번호. 해시값이 저장될 것이다.

    /*
    사용자의 일기 목록, Diary와 1대다 관계.
    cascade를 통해 모든 상태가 전이되게 한다. (User가 삭제되면, 연관된 자식들(=Diary)도 삭제된다.)
    orphanRemoval을 통해 부모와 연결이 끊긴 Diary를 삭제한다.
    */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Diary> diaries = new ArrayList<>();

    /**
     * 담당 전문가에 대한 참조.
     * 외래 키 expert_id로 연결됨
     */
    @ManyToOne
    @JoinColumn(name = "expert_id")
    private Expert expert;

    /*
    User와 Diary 사이의 관계를 명확히 하기 위함이다.
    추가한 다이어리에 해당 다이어리의 주인을 추가해주어야 오류가 안생긴다.
    */
    public void addDiary(Diary diary) {
        this.diaries.add(diary);
        diary.setUser(this);
    }

    @Builder
    public User(String email, String password) {
        this.email = email; this.password = password;
    }

    /**
     * 사용자의 권한 목록을 반환. 권한 기반으로 인가를 하기 위해 사용.
     * 다른 권한을 가진 사용자라면, 오버라이딩해서 사용한다.
     * @return List.of(UserAuthority.USER.getAuthority());
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return List.of(UserAuthority.USER.getAuthority());
    }

    /**
     * 사용자의 username을 반환.
     * 해당 엔티티에서는 email을 반환한다.
     * @return User 엔티티의 email 필드
     */
    @Override
    public String getUsername() {
        return this.email;
    }

    //계정의 만료 여부 반환
    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    //계정 잠금 여부 반환
    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    //비밀번호 만료 여부 반환
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //계정이 활성화 되어 있는지 여부
    @Override
    public boolean isEnabled() {
        return true;
    }
}
