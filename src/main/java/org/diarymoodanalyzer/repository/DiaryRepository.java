package org.diarymoodanalyzer.repository;

import org.diarymoodanalyzer.domain.Diary;
import org.diarymoodanalyzer.dto.response.GetDiaryTitleByPageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {

    /*
    User의 이메일을 받아서 해당 유저가 작성한 다이어리를 페이지로 반환한다.
    JPQL은 엔티티를 대상으로 한다. 별도의 JOIN 없이 Diary 엔티티가 가진 User에 대한 참조를 사용하여
    소유자를 추적할 수 있다.
    페이지는 말 그대로 페이지 단위로 반환한다고 보면 된다. 전체를 한번에 반환하는 것이 아니라 페이지 단위로
    반환하여 메모리 효율성도 좋고 클라이언트에서 무한 스크롤이나 페이지네이션을 구현할 때 편해진다.
     */
    @Query("SELECT d FROM Diary d WHERE d.user.email = :email")
    Page<Diary> findByUserEmail(@Param("email") String email, Pageable pageable);

    /*
    제목과 생성/수정 일자, email값만 받기 위한 쿼리. 필요한 값만 생성자로 DTO로 매핑한다.
    JPQL 쿼리에서 new를 통해 생성자를 사용하면 쿼리 결과를 자동으로 해당 DTO로 매핑해준다.
     */
    @Query("""
            SELECT new org.diarymoodanalyzer.dto.response.GetDiaryTitleByPageResponse
                (d.id, d.title, d.createdAt, d.updatedAt, d.user.email)
            FROM Diary d WHERE d.user.email = :email
            """)
    Page<GetDiaryTitleByPageResponse> findByUserEmailOnlyTitle(@Param("email") String email, Pageable pageable);


    /*
    diaryId를 받아서 해당 다이어리의 소유자를 userId로 반환한다.
     */
    @Query("SELECT d.user.id FROM Diary d WHERE d.id = :id")
    Long findUserIdById(@Param("id") Long diaryId);

/*

    diaryId와 userId를 받아서 해당 다이어리의 소유자가 맞는지 여부를 반환한다.
    JPQL/SQL CASE 문을 사용한다. d.diaryId == && d.user.userId == userId 조건에 맞는 레코드의 개수를 세어서
    0개 이상이면 TRUE, 아니면 FALSE 를 반환한다. 해당 반환값은 boolean 값으로 호출자로 넘어갈 것이다.

    @Query("""
            SELECT CASE WHEN COUNT(d) > 0 THEN TRUE ELSE FALSE END
            FROM Diary d WHERE d.diaryId = :diaryId AND d.user.userId = :userId
            """)
    boolean existsByDiaryIdAndUserId(@Param("diaryId") Long diaryId, @Param("userId") Long userId);
*/
}
