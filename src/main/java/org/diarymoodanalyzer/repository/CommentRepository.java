package org.diarymoodanalyzer.repository;

import org.diarymoodanalyzer.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {



//    @Query("SELECT c FROM Comment c WHERE c.expert.email = :email")
//    List<Comment> findByExpertEmail(@Param("email") String email);

//    @Query("SELECT c FROM Comment c WHERE c.diary.id = :id")
//    List<Comment> findByDiaryId(@Param("id") Long id);
}
