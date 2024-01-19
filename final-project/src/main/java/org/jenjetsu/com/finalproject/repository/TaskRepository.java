package org.jenjetsu.com.finalproject.repository;

import java.util.UUID;

import org.jenjetsu.com.finalproject.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    
}
