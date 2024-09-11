package org.diarymoodanalyzer.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//회원 정보를 저장하는 클래스. 
//테스트를 위한 구현입니다. 실제로는 스프링 시큐리티 사용을 위해 UserDetails를 구현할 예정입니다.
@NoArgsConstructor
@Getter
@Table(name = "users")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId; //AUTO INCREMENT하는 기본 키

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email; //NOT NULL, UNIQUE 중복 값 허용 안하는 제약

    @Column(name = "password", nullable = false)
    private String password; //비밀번호. 해시값이 저장될 것이다.

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    //사용자의 일기 목록, Diary와 1대다 관계.
    //cascade를 통해 모든 상태가 전이되게 한다. (User가 삭제되면, 연관된 자식들(=Diary)도 삭제된다.)
    //orphanRemoval을 통해 부모와 연결이 끊긴 Diary를 삭제한다.
    // 즉, user의 diaries에서 특정 Diary가 삭제되면, 해당 Diary는 diary 테이블에서도 삭제된다.
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Diary> diaries = new ArrayList<>();

    @Builder
    public User(String email, String password) {
        this.email = email; this.password = password;
    }
}
