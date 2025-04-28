package org.diarymoodanalyzer.repository;

import org.diarymoodanalyzer.domain.UserNotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserNotificationSettingRepository
    extends JpaRepository<UserNotificationSetting, Long> {

    @Query(value="SELECT u FROM UserNotificationSetting u WHERE u.user.email = :email")
    List<UserNotificationSetting> findByUserEmail(@Param("email") String email);

    @Query(value="SELECT u FROM UserNotificationSetting u WHERE u.user.id = :id")
    List<UserNotificationSetting> findByUserId(@Param("id") Long id);

    @Query(value="SELECT u FROM UserNotificationSetting u WHERE u.user.email = :email AND u.notificationType.name = :typeName")
    UserNotificationSetting findByUserEmailAndTypeName(@Param("email") String email, @Param("typeName") String typeName);

    @Query(value="SELECT u FROM UserNotificationSetting u WHERE u.user.id = :id AND u.notificationType.name = :typeName")
    UserNotificationSetting findByUserIdAndTypeName(@Param("id") Long id, @Param("typeName") String typeName);
}
