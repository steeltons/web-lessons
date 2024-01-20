package org.jenjetsu.com.finalproject.service.implementation;

import static java.lang.String.format;
import java.util.UUID;

import org.jenjetsu.com.finalproject.exception.EntityValidateException;
import org.jenjetsu.com.finalproject.model.Task;
import org.jenjetsu.com.finalproject.model.TaskDependency;
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
        for(TaskDependency taskDependency : raw.getTaskDependencyList()) {
            Task requiredTask = taskDependency.getRequiredTask();
            if(taskRep.areTasksDateCross(requiredTask.getTaskId(), 
                                         raw.getStartDate(), 
                                         raw.getEndDate())) {
                throw new EntityValidateException(format("Dependment task is overlaps in time with another %s",
                                                         requiredTask.getTaskId().toString()));
            }
        }
        return super.createEntity(raw);
    }
}
