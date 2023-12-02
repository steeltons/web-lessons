package org.jenjetsu.com.todo.repository;

import java.util.Optional;

import org.jenjetsu.com.todo.model.ActivityStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityStatusRepository extends JpaRepository<ActivityStatus, Long> {

    public static final String STANDARD_STATUS = "CREATED";

    public Optional<ActivityStatus> findByStatus(String status);
}
