package org.jenjetsu.com.finalproject.repository;

import org.jenjetsu.com.finalproject.model.SubtaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubtaskStatusRepository extends JpaRepository<SubtaskStatus, Integer> {
    
    public boolean existsByName(String name);
}
