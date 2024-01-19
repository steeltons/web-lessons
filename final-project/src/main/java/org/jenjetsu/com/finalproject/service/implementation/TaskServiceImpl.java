package org.jenjetsu.com.finalproject.service.implementation;

import java.util.UUID;

import org.jenjetsu.com.finalproject.model.Task;
import org.jenjetsu.com.finalproject.repository.TaskRepository;
import org.jenjetsu.com.finalproject.service.TaskService;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl extends SimpleJpaService<Task, UUID>
                             implements TaskService {
    
    private final TaskRepository taskRep;

    public TaskServiceImpl(TaskRepository taskRep) {
        super(Task.class, taskRep);
        this.taskRep = taskRep;
    }

    @Override
    public Task createEntity(Task raw) {
        raw.setTaskId(null);
        raw.setDeleted(false);
        return super.createEntity(raw);
    }
}
