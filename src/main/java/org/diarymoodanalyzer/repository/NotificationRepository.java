package org.diarymoodanalyzer.repository;

import org.diarymoodanalyzer.domain.Notification;
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
}
