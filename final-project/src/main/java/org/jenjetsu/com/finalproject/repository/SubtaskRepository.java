package org.jenjetsu.com.finalproject.repository;

import java.util.UUID;

import org.jenjetsu.com.finalproject.model.Subtask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SubtaskRepository extends JpaRepository<Subtask, UUID>{
    
    
}
