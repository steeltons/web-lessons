package org.jenjetsu.com.finalproject.config;

import org.jenjetsu.com.finalproject.model.SubtaskStatus;
import org.jenjetsu.com.finalproject.model.SubtaskStatusDate;
import org.jenjetsu.com.finalproject.model.SubtaskStatusDate.SubtaskStatusDateKey;
import org.jenjetsu.com.finalproject.model.TaskDependency;
import org.jenjetsu.com.finalproject.repository.SubtaskStatusDateRepository;
import org.jenjetsu.com.finalproject.repository.SubtaskStatusRepository;
import org.jenjetsu.com.finalproject.repository.TaskDependencyRepository;
import org.jenjetsu.com.finalproject.service.CRUDService;
import org.jenjetsu.com.finalproject.service.implementation.SimpleJpaService;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SimpleJpaServiceConfig {
    
    public CRUDService<TaskDependency, Long> taskDependencyService(TaskDependencyRepository rep) {
        return new SimpleJpaService<>(TaskDependency.class, rep);
    }

    public CRUDService<SubtaskStatus, Integer> subtaskStatusService(SubtaskStatusRepository rep) {
        return new SimpleJpaService<>(SubtaskStatus.class, rep);
    }

    public CRUDService<SubtaskStatusDate, SubtaskStatusDateKey> subtaskStatusDate(SubtaskStatusDateRepository rep) {
        return new SimpleJpaService<>(SubtaskStatusDate.class, rep);
    }
}

