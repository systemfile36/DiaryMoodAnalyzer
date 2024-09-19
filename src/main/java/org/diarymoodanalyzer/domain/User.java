package org.diarymoodanalyzer.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//회원 정보를 저장하는 클래스. 
//스프링 시큐리티 사용을 위해 UserDetails 를 구현
@NoArgsConstructor
@Getter
@Table(name = "users")
@Entity
public class User extends BaseEntity implements UserDetails { //공통 컬럼 상속

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId; //AUTO INCREMENT하는 기본 키

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email; //NOT NULL, UNIQUE 중복 값 허용 안하는 제약

    @Column(name = "password", nullable = false)
    private String password; //비밀번호. 해시값이 저장될 것이다.

    /*
    사용자의 일기 목록, Diary와 1대다 관계.
    cascade를 통해 모든 상태가 전이되게 한다. (User가 삭제되면, 연관된 자식들(=Diary)도 삭제된다.)
    orphanRemoval을 통해 부모와 연결이 끊긴 Diary를 삭제한다.
    즉, user의 diaries에서 특정 Diary가 삭제되면, 해당 Diary는 diary 테이블에서도 삭제된다.
    */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Diary> diaries = new ArrayList<>();

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

    //사용자의 권한 목록을 반환. 현재는 단순히 USER로 반환. 사용 시에는 ROLE_USER로 사용
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    //이메일을 유저 명으로 사용 (인덱스된 유니크 값이기 때문)
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
