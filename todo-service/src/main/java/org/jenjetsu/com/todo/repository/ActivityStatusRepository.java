package org.jenjetsu.com.todo.repository;

import org.jenjetsu.com.todo.model.ActivityStatus;
import org.jenjetsu.com.todo.model.TaskActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ActivityStatusRepository extends JpaRepository<ActivityStatus, UUID> {

    public static final String STANDARD_STATUS = "CREATED";

    public Optional<ActivityStatus> findByStatus(String status);
}
