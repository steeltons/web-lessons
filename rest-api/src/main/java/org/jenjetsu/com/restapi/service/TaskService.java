package org.jenjetsu.com.restapi.service;

import java.util.List;
import java.util.UUID;

import org.jenjetsu.com.restapi.model.Task;

public interface TaskService extends JpaService<Task, UUID> {
    
    public List<Task> readAllByUserId(UUID userId);
}
