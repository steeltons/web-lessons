package org.jenjetsu.com.todo.service.implementation;

import static java.lang.String.format;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.jenjetsu.com.todo.dto.ChangeStatusDTO;
import org.jenjetsu.com.todo.exception.EntityNotFoundException;
import org.jenjetsu.com.todo.exception.EntityValidateException;
import org.jenjetsu.com.todo.model.Task;
import org.jenjetsu.com.todo.model.TaskStatus;
import org.jenjetsu.com.todo.model.User;
import org.jenjetsu.com.todo.repository.TaskRepository;
import org.jenjetsu.com.todo.repository.TaskStatusRepository;
import org.jenjetsu.com.todo.service.TaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskServiceImpl extends SimpleJpaService<Task, UUID> implements TaskService {

    private final TaskRepository taskRep;
    private final TaskStatusRepository taskStatusRep;

    public TaskServiceImpl(TaskRepository taskRep,
                           TaskStatusRepository taskStatusRep) {
        super(Task.class, taskRep);
        this.taskRep = taskRep;
        this.taskStatusRep = taskStatusRep;
    }

    @Override
    public Task create(Task raw) {
        TaskStatus standardStatus = this.taskStatusRep.readStandardTaskStatus(TaskStatusRepository.STANDART_TASK_NAME)
                .orElseThrow(() -> new EntityNotFoundException("Impossible to load standard task status"));
        raw.setStatus(standardStatus);
        raw.getUserList().add(raw.getCreatedBy());
        return this.taskRep.saveAndFlush(raw);
    }

    @Override
    public Task readByIdFetchUserList(UUID taskId) {
        if(taskId == null) {
            throw new EntityValidateException("Task id is null");
        }
        return this.taskRep.findByIdFetchUserList(taskId)
                .orElseThrow(() -> new EntityNotFoundException(format("Task with id %s not exists.", taskId)));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void addUserToTask(UUID userId, UUID taskId) {
        Task task = this.readByIdFetchUserList(taskId);
        Stream<User> userStream = task.getUserList().stream();
        if(userStream.filter((u) -> u.getUserId().equals(userId)).count() != 0) {
            throw new EntityValidateException(format("User with id %s is already member of task %s", 
                                                      userId, taskId));
        }
        User member = User.builder()
                          .userId(userId)
                          .dashboardList(Arrays.asList(task.getDashboard()))
                          .build();
        task.getUserList().add(member);
    } 

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeUserFromTask(UUID userId, UUID taskId) {
        Task task = this.readByIdFetchUserList(taskId);
        task.getUserList().removeIf((user) -> user.getUserId().equals(userId));
    }

    @Override
    public List<Task> readAllByCreatorId(UUID creatorId) {
        if(creatorId == null) {
            throw new EntityValidateException("Creator id is null");
        }
        return this.taskRep.findAllByCreatorId(creatorId);
    }

    @Override
    public List<Task> readAllByUserId(UUID userID) {
        if(userID == null) {
            throw new EntityValidateException("User id is null");
        }
        return this.taskRep.findAllByUserIdNotDeleted(userID);
    }

    @Override
    public void changeTaskStatus(ChangeStatusDTO statusDTO) {
        Task task = this.readById(statusDTO.taskId());
        TaskStatus status = taskStatusRep.findByStatus(statusDTO.status())
                    .orElseThrow(() -> new EntityNotFoundException(format("Status %s is not exists",
                            statusDTO.status())));
        task.setStatus(status);
        this.taskRep.saveAndFlush(task);
    }

    @Override
    public void changeTaskDeleteStatus(UUID taskId,
                                       Boolean deleteStatus) {
        Task task = this.taskRep.findByIdFetchActivityList(taskId)
            .orElseThrow(() -> new EntityNotFoundException(format("Task with id %s not found", 
                                                                   taskId)));
        if(!task.getDeleted().equals(deleteStatus)) {
            task.setDeleted(deleteStatus);
            task.getActivityList().forEach((activity) -> activity.setDeleted(deleteStatus));
            this.update(task);
        } else {
            throw new EntityValidateException(format("Task %s delete status is already %s", taskId, deleteStatus));
        }
    }

}
