package org.jenjetsu.com.todo.service.implementation;

import static java.lang.String.format;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.jenjetsu.com.todo.dto.ChangeStatusDTO;
import org.jenjetsu.com.todo.exception.EntityAccessDeniedException;
import org.jenjetsu.com.todo.exception.EntityNotFoundException;
import org.jenjetsu.com.todo.exception.EntityValidateException;
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

@Service
public class TaskActivityServiceImpl extends SimpleJpaService<TaskActivity, UUID>
                                     implements TaskActivityService {

    private final TaskActivityRepository activityRep;
    private final ActivityStatusRepository statusRep;
    private final UserRepository userRep;

    public TaskActivityServiceImpl(TaskActivityRepository activityRep,
                                   ActivityStatusRepository statusRep,
                                   UserRepository userRep) {
        super(TaskActivity.class, activityRep);
        this.activityRep = activityRep;
        this.statusRep = statusRep;
        this.userRep = userRep;
    }

    @Override
    protected TaskActivity createEntity(TaskActivity raw) {
        ActivityStatus activityStatus = this.statusRep.findByStatus(ActivityStatusRepository.STANDARD_STATUS)
                .orElseThrow(() -> new EntityNotFoundException("Standard status CREATED not exists"));
        raw.setActivityStatus(activityStatus);
        UUID userId = raw.getUser().getUserId();
        if(userId == null) {
            Optional<UUID> optionalUserId = Optional.empty();
            if (raw.getUser().getUsername() != null) {
                optionalUserId = this.userRep.getUserIdByUsername(raw.getUser().getUsername());
            } 
            if (optionalUserId.isEmpty() && raw.getUser().getEmail() != null) {
                optionalUserId = this.userRep.getUserIdByEmail(raw.getUser().getEmail());
            }
            userId = optionalUserId.orElseThrow(() -> new
                    EntityNotFoundException(format("User with username %s and email %s not exists", 
                                                   raw.getUser().getUsername(),
                                                   raw.getUser().getEmail())));
         }
        raw.setUser(User.builder().userId(userId).build());
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
    public void changeActivityStatus(ChangeStatusDTO statusDTO) {
        TaskActivity taskActivity = this.readById(statusDTO.taskActivityId());
        ActivityStatus status = this.statusRep.findByStatus(statusDTO.status())
                    .orElseThrow(() -> new EntityNotFoundException(format("Status %s not exists", statusDTO.status())));
        taskActivity.setActivityStatus(status);
        this.activityRep.saveAndFlush(taskActivity);
    }

    @Override
    public void changeActivityDeleteStatus(UUID activityId, boolean deleteStatus) {
        TaskActivity activity = this.readById(activityId);
        if(!activity.getDeleted().equals(deleteStatus)) {
            activity.setDeleted(deleteStatus);
            this.activityRep.saveAndFlush(activity);
        } else {
            throw new EntityValidateException(format("Activity %s is already ", activityId) + 
                                              ((deleteStatus) ? "deleted" : "restored"));
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
