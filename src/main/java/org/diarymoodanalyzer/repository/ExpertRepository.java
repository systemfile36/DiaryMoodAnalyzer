package org.diarymoodanalyzer.repository;

import org.diarymoodanalyzer.domain.Expert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExpertRepository extends JpaRepository<Expert, Long> {
    Optional<Expert> findById(Long id);
}
