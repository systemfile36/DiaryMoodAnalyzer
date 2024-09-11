package org.diarymoodanalyzer.repository;

import org.diarymoodanalyzer.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    //이메일로 유저를 찾는 메소드. 이름 기반으로 생성
    Optional<User> findByEmail(String email);
}
