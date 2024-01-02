package org.jenjetsu.com.restapi.service.implementation;

import static java.lang.String.format;
import java.util.List;
import java.util.UUID;

import org.jenjetsu.com.restapi.exception.EntityNotFoundException;
import org.jenjetsu.com.restapi.model.Task;
import org.jenjetsu.com.restapi.repository.TaskRepository;
import org.jenjetsu.com.restapi.repository.UserRepository;
import org.jenjetsu.com.restapi.service.TaskService;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl extends SimpleJpaService<Task, UUID>
                             implements TaskService {
    
    private final TaskRepository taskRep;
    private final UserRepository userRep;

    public TaskServiceImpl(TaskRepository taskRep,
                           UserRepository userRep) {
        super(Task.class, taskRep);
        this.taskRep = taskRep;
        this.userRep = userRep;
    }

    @Override
    protected Task createEntity(Task raw) {
        UUID userId = raw.getUser().getUserId();
        if (userId != null && userRep.existsById(userId)) {
            return super.createEntity(raw);
        } else {
            throw new EntityNotFoundException(format("User with id %s not found", userId));
        }
    }

    @Override
    public List<Task> readAllByUserId(UUID userId) {
        if(userId != null && userRep.existsById(userId)) {
            return this.taskRep.findAllByUserId(userId);
        } else {
            throw new EntityNotFoundException(format("User with id %s not found", userId));
        }
    }
}
