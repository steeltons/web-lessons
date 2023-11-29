package org.jenjetsu.com.todo.service.implementation;

import org.jenjetsu.com.todo.dto.ChangeStatusDTO;
import org.jenjetsu.com.todo.exception.EntityAccessDeniedException;
import org.jenjetsu.com.todo.exception.EntityCreateException;
import org.jenjetsu.com.todo.exception.EntityNotFoundException;
import org.jenjetsu.com.todo.model.ActivityStatus;
import org.jenjetsu.com.todo.model.TaskActivity;
import org.jenjetsu.com.todo.model.User;
import org.jenjetsu.com.todo.repository.ActivityStatusRepository;
import org.jenjetsu.com.todo.repository.TaskActivityRepository;
import org.jenjetsu.com.todo.repository.UserRepository;
import org.jenjetsu.com.todo.security.JwtUserIdAuthenticationToken;
import org.jenjetsu.com.todo.service.TaskActivityService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;

@Service
public class TaskActivityServiceImpl extends SimpleJpaService<TaskActivity, UUID>
                                     implements TaskActivityService {

    private final TaskActivityRepository activityRep;
    private final ActivityStatusRepository statusRep;
    private final UserRepository userRep;

    public TaskActivityServiceImpl(TaskActivityRepository activityRep,
                                   ActivityStatusRepository statusRep,
                                   UserRepository userRep) {
        super(TaskActivity.class);
        this.activityRep = activityRep;
        this.statusRep = statusRep;
        this.userRep = userRep;
    }

    @Override
    protected TaskActivity createEntity(TaskActivity raw) {
        ActivityStatus activityStatus = this.statusRep.findByStatus(ActivityStatusRepository.STANDARD_STATUS)
                .orElseThrow(() -> new EntityNotFoundException("Standard status CREATED not exists"));
        raw.setActivityStatus(activityStatus);
        User user = raw.getUser();
        if(user.getUserId() == null) {
            Optional<User> optionalUser = Optional.empty();
            String username = user.getUsername();
            String email = user.getEmail();
            if (username != null) {
                optionalUser = this.userRep.findUserByUsername(username);
            } else {
                optionalUser = this.userRep.findUserByEmail(email);
            }
            user = optionalUser.orElseThrow(() -> new
                    EntityNotFoundException(format("User with username %s and email %s not exists", username, email)));
         }
        raw.setUser(user);
        return super.createEntity(raw);
    }

    @Override
    public List<TaskActivity> readAllByUserId(UUID userId) {
        return this.activityRep.findAllByUserIdNotDeleted(userId);
    }

    @Override
    public List<TaskActivity> readAllByCreatorId(UUID creatorId) {
        return this.activityRep.findAllByCreatorId(creatorId);
    }

    @Override
    public void changeActivityStatus(ChangeStatusDTO statusDTO,
                                     JwtUserIdAuthenticationToken token) {
        TaskActivity taskActivity = this.readById(statusDTO.taskActivityId());
        UUID creatorId = taskActivity.getCreatedBy().getUserId();
        UUID userId = taskActivity.getUser().getUserId();
        if(creatorId.equals(token.getUserId()) || userId.equals(token.getUserId())) {
            ActivityStatus status = this.statusRep.findByStatus(statusDTO.status())
                    .orElseThrow(() -> new EntityNotFoundException(format("Status %s not exists", statusDTO.status())));
            taskActivity.setActivityStatus(status);
            this.activityRep.saveAndFlush(taskActivity);
        } else {
            throw new EntityAccessDeniedException(format("Only creator or worker can change status of activity %s",
                    statusDTO.taskActivityId().toString()));
        }
    }

    @Override
    protected TaskActivity updateEntity(TaskActivity newEntity) {
        JwtUserIdAuthenticationToken token = (JwtUserIdAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if(newEntity.getCreatedBy().getUserId().equals(token.getUserId())) {
            return this.activityRep.saveAndFlush(newEntity);
        } else {
            throw new EntityAccessDeniedException(format("Only creator can modify activity %s.",
                    newEntity.getTaskActivityId().toString()));
        }
     }
}
