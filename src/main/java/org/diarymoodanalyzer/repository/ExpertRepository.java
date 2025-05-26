package org.diarymoodanalyzer.repository;

import org.diarymoodanalyzer.domain.Expert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExpertRepository extends JpaRepository<Expert, Long> {
    Optional<Expert> findByEmail(String email);

    /**
     * `email`로 특정한 User의 Expert 엔티티를 반환한다.
     * @param email - `email` of User entity
     * @return Expert of User specified by `id`
     */
    @Query(value="SELECT u.expert FROM User u WHERE u.email = :email")
    Optional<Expert> findExpertByUserEmail(@Param("email") String email);

    /**
     * `id`로 특정한 User의 Expert 엔티티를 반환한다.
     * @param id - `id` of User entity
     * @return Expert of User specified by `id`
     */
    @Query(value="SELECT u.expert FROM User u WHERE u.id = :id")
    Optional<Expert> findExpertByUserId(@Param("id") Long id);

    /**
     * `expertId`로 특정한 Expert가 `userEmail`로 특정한 User를 가지는 지 여부를 반환한다.
     * @param expertId `id` of Expert entity
     * @param userEmail `email` of User entity
     * @return if Expert has the User than true, otherwise, false
     */
    @Query(value="SELECT COUNT(u) > 0 FROM User u WHERE u.expert.id = :id AND u.email = :email")
    boolean hasUserByIdAndEmail(@Param("id") Long expertId, @Param("email") String userEmail);

    /**
     * `expertId`로 특정한 Expert가 `userId`로 특정한 User를 가지는 지 여부를 반환한다.
     * @param expertId `id` of Expert entity
     * @param userId `id` of User entity
     * @return if Expert has the User than true, otherwise, false
     */
    @Query(value="SELECT COUNT(u) > 0 FROM User u WHERE u.expert.id = :expertId AND u.id = :userId")
    boolean hasUserByIdAndId(@Param("expertId") Long expertId, @Param("userId") String userId);

}
