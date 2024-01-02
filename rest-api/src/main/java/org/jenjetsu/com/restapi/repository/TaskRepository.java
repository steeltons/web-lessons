package org.jenjetsu.com.restapi.repository;

import java.util.List;
import java.util.UUID;

import org.jenjetsu.com.restapi.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    
    @Query(
        value = "SELECT u.taskList FROM t_user u " +
                "WHERE u.userId = :userId"
    )
    public List<Task> findAllByUserId(@Param("userId") UUID userId);
}
