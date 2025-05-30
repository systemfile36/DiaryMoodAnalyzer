package org.diarymoodanalyzer.repository;

import org.diarymoodanalyzer.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    //이메일로 유저를 찾는 메소드. 이름 기반으로 생성
    Optional<User> findByEmail(String email);

    /**
     * 지정된 email로 User 엔티티를 가져온다.
     * Expert 엔티티도 fetch join으로 같이 가져온다.
     * @param email
     * @return
     */
    @Query(value="SELECT u FROM User u JOIN FETCH u.expert WHERE u.email = :email")
    Optional<User> findByEmailWithExpert(@Param("email") String email); //Using 'fetch join' to avoid N+1

    /*
    JPQL을 사용하여 이메일을 통해 User의 user_id만 받아온다.
    JPQL은 테이블과 컬럼이 아닌 엔티티와 필드를 대상으로 한다. 이 점 주의!
     */
    @Query("SELECT u.id FROM User u WHERE u.email = :email")
    Long findIdByEmail(@Param("email") String email);

    //이메일로 유저의 존재 유무를 반환
    boolean existsByEmail(String email);
}
