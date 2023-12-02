package org.jenjetsu.com.todo.service.implementation;

import static java.lang.String.format;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.jenjetsu.com.todo.dto.ChangeStatusDTO;
import org.jenjetsu.com.todo.dto.TaskUserLinkDTO;
import org.jenjetsu.com.todo.exception.EntityNotFoundException;
import org.jenjetsu.com.todo.exception.EntityValidateException;
import org.jenjetsu.com.todo.model.Task;
import org.jenjetsu.com.todo.model.TaskStatus;
import org.jenjetsu.com.todo.repository.DashboardRepository;
import org.jenjetsu.com.todo.repository.TaskRepository;
import org.jenjetsu.com.todo.repository.TaskStatusRepository;
import org.jenjetsu.com.todo.repository.UserRepository;
import org.jenjetsu.com.todo.security.JwtUserIdAuthenticationToken;
import org.jenjetsu.com.todo.service.TaskService;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl extends SimpleJpaService<Task, UUID> implements TaskService {

    private final TaskRepository taskRep;
    private final TaskStatusRepository taskStatusRep;
    private final UserRepository userRep;
    private final DashboardRepository dashboardRep;

    public TaskServiceImpl(TaskRepository taskRep,
                           TaskStatusRepository taskStatusRep,
                           UserRepository userRep,
                           DashboardRepository dashboardRep) {
        super(Task.class, taskRep);
        this.taskRep = taskRep;
        this.taskStatusRep = taskStatusRep;
        this.userRep = userRep;
        this.dashboardRep = dashboardRep;
    }

    @Override
    public Task create(Task raw) {
        TaskStatus standardStatus = this.taskStatusRep.readStandardTaskStatus(TaskStatusRepository.STANDART_TASK_NAME)
                .orElseThrow(() -> new EntityNotFoundException("Impossible to load standard task status"));
        raw.setStatus(standardStatus);
        return this.taskRep.save(raw);
    }

    @Override
    public Task readByIdFetchUserList(UUID taskId) {
        if(taskId == null) {
            throw new EntityValidateException("Task id is null");
        }
        return this.taskRep.findByIdFetchUserList(taskId)
                .orElseThrow(() -> new EntityNotFoundException(format("Task with id %s not exists.", taskId)));
    }


    public Task readByIdFetchActivityList(UUID taskId) {
        if(taskId == null) {
            throw new EntityValidateException("Task id is null");
        }
        return this.taskRep.findByIdFetchActivityList(taskId)
                .orElseThrow(() -> new EntityNotFoundException(format("Task with id %s not exists.", taskId)));
    }

    @Override
    public void linkUserWithTask(TaskUserLinkDTO dto, JwtUserIdAuthenticationToken token) {
        UUID userId = dto.userId();
        if(userId == null) {
            Optional<UUID> optionalUserId = Optional.empty();
            if(dto.username() != null && this.userRep.existsByUsername(dto.username())) {
                optionalUserId = this.userRep.getUserIdByUsername(dto.username());
            }
            if(optionalUserId.isEmpty() && dto.email() != null) {
                optionalUserId = this.userRep.getUserIdByEmail(dto.email());
            } 
            if(optionalUserId.isEmpty()) {
                throw new EntityNotFoundException(format("User with id %s username %s and email %s not found",
                dto.userId(), dto.username(), dto.email()));
            } 
            userId = optionalUserId.get();
        }
        this.addUserToTask(userId, dto.taskId(), dto.dashboardId());
    }

    @Override
    public void addUserToTask(UUID userId, UUID taskId, UUID dashboardId) {
        if(!this.taskRep.existsById(taskId)) {
            throw new EntityNotFoundException(format("Task with id %s not exists", taskId));
        }
        if(!this.dashboardRep.isUserInDashboard(dashboardId, userId)) {
            throw new EntityValidateException(format("User with id %s is not memeber of task dashboard %s",
                                                     userId, dashboardId));
        }
        if(this.taskRep.isUserInTask(userId, taskId)) {
            throw new EntityValidateException(format("User with id %s is already member of task %s", 
                                                     userId, taskId));
        }
        this.taskRep.addUserToTask(userId, taskId, dashboardId);
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
        Task task = this.readByIdFetchActivityList(taskId);
        if(!task.getDeleted().equals(deleteStatus)) {
            task.setDeleted(deleteStatus);
            task.getActivityList().forEach((activity) -> activity.setDeleted(deleteStatus));
            this.update(task);
        } else {
            throw new EntityValidateException(format("Task %s delete status is already %s", taskId, deleteStatus));
        }
    }
}
