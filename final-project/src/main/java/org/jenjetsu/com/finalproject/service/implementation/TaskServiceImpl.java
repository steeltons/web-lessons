package org.jenjetsu.com.finalproject.service.implementation;

import java.util.UUID;

import org.jenjetsu.com.finalproject.model.Task;
import org.jenjetsu.com.finalproject.repository.TaskRepository;
import org.jenjetsu.com.finalproject.service.TaskService;
import org.jenjetsu.com.finalproject.validator.TaskValidator;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl extends SimpleJpaService<Task, UUID>
                             implements TaskService {
    
    private final TaskRepository taskRep;
    private final TaskValidator taskValidator;

    public TaskServiceImpl(TaskRepository taskRep,
                           TaskValidator taskValidator) {
        super(Task.class, taskRep);
        this.taskRep = taskRep;
        this.taskValidator = taskValidator;
    }

    @Override
    public Task createEntity(Task raw) {
        raw.setTaskId(null);
        raw.setDeleted(false);
        taskValidator.validateTaskDatesRangeInProject(raw);
        taskValidator.validateTaskDependencies(raw);
        raw.getTaskDependencyList()
           .forEach((dep) -> dep.setTask(raw));
        return super.createEntity(raw);
    }

    
}
