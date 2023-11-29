package org.jenjetsu.com.todo.service.implementation;

import static java.lang.String.format;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.jenjetsu.com.todo.dto.ChangeStatusDTO;
import org.jenjetsu.com.todo.dto.TaskUserLinkDTO;
import org.jenjetsu.com.todo.exception.EntityAccessDeniedException;
import org.jenjetsu.com.todo.exception.EntityNotFoundException;
import org.jenjetsu.com.todo.exception.EntityValidateException;
import org.jenjetsu.com.todo.model.Task;
import org.jenjetsu.com.todo.model.TaskStatus;
import org.jenjetsu.com.todo.model.User;
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

    public TaskServiceImpl(TaskRepository taskRep,
                           TaskStatusRepository taskStatusRep,
                           UserRepository userRep) {
        super(Task.class);
        this.taskRep = taskRep;
        this.taskStatusRep = taskStatusRep;
        this.userRep = userRep;
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
                .orElseThrow(() -> new EntityNotFoundException(format("Task with id % not exists.", taskId)));
    }


    public Task readByIdFetchActivityList(UUID taskId) {
        if(taskId == null) {
            throw new EntityValidateException("Task id is null");
        }
        return this.taskRep.findByIdFetchActivityList(taskId)
                .orElseThrow(() -> new EntityNotFoundException(format("Task with id % not exists.", taskId)));
    }

    public void linkUserWithTask(TaskUserLinkDTO dto, JwtUserIdAuthenticationToken token) {
        Task task = this.readByIdFetchUserList(dto.taskId());
        if(!task.getCreatedBy().getUserId().equals(token.getUserId())) {
            throw new EntityAccessDeniedException(format("Only creator can link user to task %s", dto.taskId()));
        }
        if(dto.taskId() == null && dto.username() == null && dto.email() == null) {
            throw new EntityValidateException("No user_id, username or email in request body");
        }
        Optional<User> optionalUser = Optional.empty();
        if(dto.userId() != null) {
            optionalUser = this.userRep.findById(dto.userId());
        } else if (dto.username() != null) {
            optionalUser = this.userRep.findUserByUsername(dto.username());
        } else if(dto.email() != null){
            optionalUser = this.userRep.findUserByEmail(dto.email());
        }
        if(optionalUser.isPresent()) {
            List<User> userList = task.getUserList();
            User userToAdd = optionalUser.get();
            if (userList.stream().filter((u) -> u.getUserId().equals(userToAdd.getUserId())).count() == 0) {
                userList.add(userToAdd);
                this.update(task);
            } else {
                throw new EntityValidateException(format("User %s with id %s is already linked",
                        userToAdd.getUsername(), userToAdd.getUserId()));
            }
        } else {
            throw new EntityNotFoundException(format("User with id %s, username %s, email %s not exists",
                    dto.userId(), dto.username(), dto.email()));
        }
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
    public void changeTaskStatus(ChangeStatusDTO statusDTO, JwtUserIdAuthenticationToken token) {
        Task task = this.readByIdFetchUserList(statusDTO.taskId());
        UUID creatorId = task.getCreatedBy().getUserId();
        List<UUID> userIdList = task.getUserList().stream().map(User::getUserId).toList();
        if(creatorId.equals(token.getUserId()) || userIdList.contains(token.getUserId())) {
            TaskStatus status = taskStatusRep.findByStatus(statusDTO.status())
                    .orElseThrow(() -> new EntityNotFoundException(format("Status %s is not exists",
                            statusDTO.status())));
            task.setStatus(status);
            this.taskRep.saveAndFlush(task);
        } else {
            throw new EntityAccessDeniedException(format("Only creator or worker can change status of task %s",
                    statusDTO.taskId()));
        }
    }

    public void changeTaskDeleteStatus(UUID taskId,
                                       Boolean deleteStatus,
                                       JwtUserIdAuthenticationToken token) {
        Task task = this.readByIdFetchActivityList(taskId);
        if(task.getCreatedBy().getUserId().equals(token.getUserId())) {
            if(!task.getDeleted().equals(deleteStatus)) {
                task.setDeleted(deleteStatus);
                task.getActivityList().forEach((activity) -> activity.setDeleted(deleteStatus));
                this.update(task);
            } else {
                throw new EntityValidateException(format("Task %s delete status is already %s", taskId, deleteStatus));
            }
        } else {
            throw new EntityAccessDeniedException(format("Only creator can change delete status of task %s", taskId));
        }
    }
}
