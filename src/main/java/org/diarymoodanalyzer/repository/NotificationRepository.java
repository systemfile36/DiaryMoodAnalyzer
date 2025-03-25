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

    @Modifying
    @Query(value="UPDATE Notification n SET n.isRead = :isRead WHERE n.id = :id")
    int updateIsReadById(@Param("id") Long id, @Param("isRead") boolean isRead);

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
