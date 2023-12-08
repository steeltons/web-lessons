package org.jenjetsu.com.todo.aspect;

import static java.lang.String.format;
import java.util.UUID;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.jenjetsu.com.todo.dto.ChangeStatusDTO;
import org.jenjetsu.com.todo.exception.EntityAccessDeniedException;
import org.jenjetsu.com.todo.exception.EntityNotFoundException;
import org.jenjetsu.com.todo.repository.TaskActivityRepository;
import org.jenjetsu.com.todo.repository.TaskRepository;
import org.jenjetsu.com.todo.security.JwtUserIdAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@Aspect
@RequiredArgsConstructor
public class TaskActivityPreEndpointProtectAspect {
    
    private final TaskActivityRepository activityRep;
    private final TaskRepository taskRep;

    @Before("execution(public * org.jenjetsu.com.todo.controller.TaskActivityController.deleteTaskActivity(..)) || " + 
            "execution(public * org.jenjetsu.com.todo.controller.TaskActivityController.restoreTaskActivity(..))")
    public void protectActivityDeleteStatusEndpoint(JoinPoint jp) {
        UUID activityId = (UUID) jp.getArgs()[0];
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUserIdAuthenticationToken token = (JwtUserIdAuthenticationToken) auth;
        if(!this.activityRep.isUserActivityCreator(token.getUserId(), activityId)) {
            throw new EntityAccessDeniedException(format("User %s is not activity %s creator",
                                                         token.getUserId(), activityId));
        }
    }

    @Before("execution(public * org.jenjetsu.com.todo.controller.TaskActivityController.*subscribe*Activity(..))")
    public void protectSubscribeToActivityEndpoint(JoinPoint jp) {
        UUID activityId = (UUID) jp.getArgs()[0];
        JwtUserIdAuthenticationToken token = (JwtUserIdAuthenticationToken) jp.getArgs()[1];
        UUID taskId = this.activityRep.getTaskIdByActivityId(activityId)
            .orElseThrow(() -> new EntityNotFoundException(format("Activity with id %s not found",
                                                                   activityId)));
        if(!this.taskRep.isUserInTask(token.getUserId(), taskId)) {
            throw new EntityAccessDeniedException(format("User with id %s is not member of task %s", 
                                                      token.getUserId(), taskId));
        }
    }

    // @Before("execution(public * org.jenjetsu.com.todo.controller.TaskActivityController.unsubscribeFromActivity(..))")
    // public void protectUnsubscribeFromActivityEndpoint(JoinPoint jp) {
    //     UUID activityId = (UUID) jp.getArgs()[0];
    //     JwtUserIdAuthenticationToken token = (JwtUserIdAuthenticationToken) jp.getArgs()[1];
    //     if(!this.activityRep.isUserActivityCreatorOrMember(token.getUserId(), activityId)) {
    //         throw new EntityAccessDeniedException(format("User %s is not activity %s creator or member",
    //                                                      token.getUserId(), activityId));
    //     }
    // }

    @Before("execution(public * org.jenjetsu.com.todo.controller.TaskActivityController.changeActivityStatus(..))")
    public void protectActivityChangeStatusEndpoint(JoinPoint jp) {
        ChangeStatusDTO statusDto = (ChangeStatusDTO) jp.getArgs()[0];
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUserIdAuthenticationToken token = (JwtUserIdAuthenticationToken) auth;
        if(!this.activityRep.isUserActivityCreatorOrMember(token.getUserId(), statusDto.taskActivityId())) {
            throw new EntityAccessDeniedException(format("User %s is not activity %s creator or member",
                                                         token.getUserId(), statusDto.taskActivityId()));
        }
    }
}
