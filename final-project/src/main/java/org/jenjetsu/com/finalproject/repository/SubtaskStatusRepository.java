package org.jenjetsu.com.finalproject.repository;

import java.util.Optional;

import org.jenjetsu.com.finalproject.model.SubtaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubtaskStatusRepository extends JpaRepository<SubtaskStatus, Integer> {
    
    public Optional<SubtaskStatus> findByName(String name);
    public boolean existsByName(String name);
}
