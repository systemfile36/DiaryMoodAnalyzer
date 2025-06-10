package org.diarymoodanalyzer.repository;

import org.diarymoodanalyzer.domain.Diary;
import org.diarymoodanalyzer.dto.response.GetDiaryTitleByPageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
                (d.id, d.title, d.depressionLevel, d.createdAt, d.updatedAt, d.user.email)
            FROM Diary d WHERE d.user.email = :email
            """)
    Page<GetDiaryTitleByPageResponse> findByUserEmailOnlyTitle(@Param("email") String email, Pageable pageable);


    /*
    diaryId를 받아서 해당 다이어리의 소유자를 userId로 반환한다.
     */
    @Query("SELECT d.user.id FROM Diary d WHERE d.id = :id")
    Long findUserIdById(@Param("id") Long diaryId);

    /**
     * Return <code>createdAt</code> and <code>depressionScore</code>
     * where <code>createdAt</code> between <code>start</code> and <code>end</code>
     * <br/>
     * This method use JPQL
     * @param email owner's email
     * @param start start of date range
     * @param end end of date range
     * @return List of <code>createdAt</code> as {@link LocalDateTime LocalDateTime}
     * and <code>depressionScore</code> as <code>int</code>
     */
    @Query(value="SELECT d.createdAt, d.depressionScore FROM Diary d WHERE (d.createdAt BETWEEN :start AND :end) AND d.user.email = :email")
    List<Object[]> findDepressionScoreBetween(@Param("email") String email, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * Return <code>createdAt</code> and daily average <code>depressionScore</code>
     * where <code>createdAt</code> between <code>start</code> and <code>end</code>
     * <br/>
     * This method use native SQL query
     * @param email owner's email
     * @param start start of date range
     * @param end end of date range
     * @return List of <code>createdAt</code> as {@link LocalDateTime LocalDateTime}
     * and daily average <code>depressionScore</code> as <code>double</code>
     */
    @Query(value= """
            SELECT DATE(d.created_at) AS date, AVG(d.depression_level)
            FROM diaries d
            INNER JOIN users u ON u.id = d.user_id
            WHERE u.email = :email
                AND (created_at BETWEEN :start AND :end)
            GROUP BY DATE(d.created_at)
            """, nativeQuery = true) // Use native query for performance and convenience
    List<Object[]> findDailyDepressionScoreAvg(@Param("email") String email, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * Return <code>createdAt</code> and daily average <code>depressionScore</code> without range
     * <br/>
     * This method use native SQL query
     * @param email owner's email
     * @return List of <code>createdAt</code> as {@link LocalDateTime LocalDateTime}
     * and daily average <code>depressionScore</code> as <code>double</code>
     */
    @Query(value= """
            SELECT DATE(d.created_at) AS date, AVG(d.depression_level)
            FROM diaries d
            INNER JOIN users u ON u.id = d.user_id
            WHERE u.email = :email
            GROUP BY DATE(d.created_at)
            """, nativeQuery = true) // Use native query for performance and convenience
    List<Object[]> findDailyDepressionScoreAvg(@Param("email") String email);

    /**
     * 사용자의 이메일을 받아서 해당 사용자가 작성한 Diary의
     * 날짜별 depression level의 평균을 반환한다.
     * @param email - 사용자의 이메일. Diary의 주인
     * @return (날짜, 평균 depression_level)의 쌍을 담은 리스트
     * @deprecated <code>depressionLevel</code> not be used anymore
     */
    @Deprecated
    @Query( value = """
            SELECT DATE(d.created_at) AS date, AVG(d.depression_level)
            FROM diaries d
            INNER JOIN users u
            ON u.id = d.user_id
            WHERE u.email = :email
            GROUP BY DATE(d.created_at)
            ORDER BY date ASC
            """, nativeQuery = true) //네이티브 쿼리 사용
    List<Object[]> findDailyDepressionLevelAvg(@Param("email") String email);

    /**
     * 사용자의 이메일을 받아서 해당 사용자가 작성한 Diary의
     * 날짜별 depression level의 평균을 반환한다.
     * 이때, 시작 날짜와 종료 날짜를 받아서, 해당 날짜 사이의 것만 리턴한다.
     * @param email - 사용자의 이메일
     * @param startDate - 시작 날짜
     * @param endDate - 종료 날짜
     * @return (날짜, 평균 depression_level)의 쌍을 담은 리스트
     * @deprecated <code>depressionLevel</code> not be used anymore
     */
    @Deprecated
    @Query(value = """
            SELECT DATE(d.created_at) AS date, AVG(d.depression_level)
            FROM diaries d
            INNER JOIN users u
            ON u.id = d.user_id
            WHERE u.email = :email
                AND DATE(d.created_at) >= :startDate
                AND DATE(d.created_at) <= :endDate
            GROUP BY DATE(d.created_at)
            ORDER BY date ASC
            """, nativeQuery = true) //네이티브 쿼리 사용
    List<Object[]> findDailyDepressionLevelAvg(@Param("email")String email, LocalDate startDate, LocalDate endDate);

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
