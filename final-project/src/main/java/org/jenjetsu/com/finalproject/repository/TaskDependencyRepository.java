package org.jenjetsu.com.finalproject.repository;

import org.jenjetsu.com.finalproject.model.TaskDependency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskDependencyRepository extends JpaRepository<TaskDependency, Long> {
    
}
