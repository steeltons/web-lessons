package org.jenjetsu.com.todo.repository;

import java.util.Optional;
import java.util.UUID;

import org.jenjetsu.com.todo.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskStatusRepository extends JpaRepository<TaskStatus, UUID> {

    public static final String STANDART_TASK_NAME = "CREATED";

    @Query("SELECT ts FROM t_task_status ts WHERE ts.status = :name")
    public Optional<TaskStatus> readStandardTaskStatus(@Param("name") String name);

    public Optional<TaskStatus> findByStatus(String status);
}
