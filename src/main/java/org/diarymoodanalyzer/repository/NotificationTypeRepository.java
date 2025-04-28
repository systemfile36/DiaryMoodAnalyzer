package org.diarymoodanalyzer.repository;

import org.diarymoodanalyzer.domain.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NotificationTypeRepository extends JpaRepository<NotificationType, Long> {
    @Query(value="SELECT n FROM NotificationType n WHERE n.name = :name")
    Optional<NotificationType> findByName(@Param("name") String name);

    void deleteByName(String name);

    @Modifying
    @Query(value="UPDATE NotificationType n SET n.description = :description WHERE n.id = :id")
    void updateDescriptionById(@Param("id") Long id, @Param("description") String description);

    @Query(value="SELECT n.id FROM NotificationType n WHERE n.name = :name")
    Long findIdByName(@Param("name") String name);
}
