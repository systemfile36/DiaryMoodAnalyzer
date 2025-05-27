package org.diarymoodanalyzer.repository;

import org.diarymoodanalyzer.domain.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query(value="SELECT n FROM Notification n WHERE n.targetUser.email = :email")
    List<Notification> findByTargetUserEmail(@Param("email") String userEmail);

    @Query(value = "SELECT n FROM Notification n WHERE n.senderUser.email = :email")
    List<Notification> findBySenderUserEmail(@Param("email") String userEmail);

    /**
     * 인자의 id에 해당하는 단일 알림의 isRead 컬럼을 :isRead로 설정한다.
     * @param id 읽음 설정을 변경할 알림의 ID
     * @param isRead 설정할 읽음 설정
     */
    @Modifying
    @Query(value="UPDATE Notification n SET n.isRead = :isRead WHERE n.id = :id")
    void updateIsReadById(@Param("id") Long id, @Param("isRead") boolean isRead);

    /**
     * ids에 해당하는 모든 알림의 isRead 컬럼을 :isRead로 설정한다.
     * @param ids 읽음 상태를 변경할 알림의 ID
     * @param isRead 설정할 읽음 설정
     * @return 변경된 컬럼의 수
     */
    @Modifying
    @Query(value="UPDATE Notification n SET n.isRead = :isRead WHERE n.id IN :ids")
    int updateIsReadByIds(@Param("ids") List<Long> ids, @Param("isRead") boolean isRead);

    /**
     * userEmail에 해당하는 사용자의 모든 알림의 isRead 컬럼을 :isRead로 설정한다. 
     * @param userEmail 지정할 사용자의 이메일
     * @param isRead 설정할 읽음 설정
     */
    @Deprecated //UPDATE and DELETE query not support JOIN
    @Modifying
    @Query(value="UPDATE Notification n SET n.isRead = :isRead WHERE n.targetUser.email = :email")
    void updateIsReadByTargetUserEmail(@Param("email") String userEmail, @Param("isRead") boolean isRead);

    /**
     * `userId`에 해당하는 사용자의 모든 알림의 `isRead` 컬럼을 `:isRead`로 설정한다.
     * @param userId 지정할 사용자의 id
     * @param isRead 설정할 컬럼 값
     */
    @Modifying
    @Query(value="UPDATE Notification n SET n.isRead = :isRead WHERE n.targetUser.id = :userId")
    void updateIsReadByTargetUserId(@Param("userId") Long userId, @Param("isRead") boolean isRead);
    //UPDATE and DELETE query not support JOIN. So use FK instead of email


    /**
     * userEmail에 해당하는 사용자의 모든 알림을 삭제한다.
     * @param userEmail
     */
    @Deprecated //UPDATE and DELETE query not support JOIN
    @Modifying
    @Query(value="DELETE FROM Notification n WHERE n.targetUser.email = :email")
    void deleteAllByTargetUserEmail(@Param("email") String userEmail);

    /**
     * `userId`에 해당하는 사용자의 모든 알림을 삭제한다.
     * @param userId 지정할 사용자의 `id`
     */
    @Modifying
    @Query(value="DELETE FROM Notification n WHERE n.targetUser.id = :userId")
    void deleteAllByTargetUserId(@Param("userId") Long userId);
    //UPDATE and DELETE query not support JOIN. So use FK instead of email

    /**
     * 사용자의 알림 개수를 조회한다. 
     * @param targetUserEmail 알림 개수를 조회할 사용자 
     * @return 해당 사용자가 target인 알림의 개수
     */
    @Query(value="SELECT COUNT(n) FROM Notification n WHERE n.targetUser.email = :targetUserEmail")
    int countByTargetUserEmail(@Param("targetUserEmail") String targetUserEmail);

    /**
     * 사용자의 알림의 id를 오래된 순으로 가져온다.
     * @param targetUserEmail 알림을 조회할 사용자
     * @param pageable 페이징 변수.
     * @return 페이징 변수에 맞게 반환된 알림의 id List
     */
    @Query(value="SELECT n.id FROM Notification n WHERE n.targetUser.email = :targetUserEmail ORDER BY n.createdAt ASC")
    List<Long> findOldestNotificationIds(@Param("targetUserEmail") String targetUserEmail, Pageable pageable);
}
