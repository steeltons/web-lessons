package org.jenjetsu.com.todo.aspect;

import static java.lang.String.format;
import java.util.UUID;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.jenjetsu.com.todo.dto.ChangeStatusDTO;
import org.jenjetsu.com.todo.dto.TaskCreateDTO;
import org.jenjetsu.com.todo.dto.TaskUserLinkDTO;
import org.jenjetsu.com.todo.dto.UserDashboardInviteDTO;
import org.jenjetsu.com.todo.exception.EntityAccessDeniedException;
import org.jenjetsu.com.todo.exception.EntityNotFoundException;
import org.jenjetsu.com.todo.repository.DashboardRepository;
import org.jenjetsu.com.todo.repository.DashboardUserInviteRepository;
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
public class UserPreEndpointAuthorizeAspect {
    
    private final DashboardUserInviteRepository inviteRep;
    private final DashboardRepository dashboardRep;
    private final TaskRepository taskRep;
    private final TaskActivityRepository activityRep;

    @Before("execution(public * org.jenjetsu.com.todo.controller.DashboardController.acceptInvite(..))")
    public void protectAcceptInviteEndpoint(JoinPoint jp) {
        UUID inviteId = (UUID) jp.getArgs()[0];
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUserIdAuthenticationToken token = (JwtUserIdAuthenticationToken) auth;
        if (!this.inviteRep.compareReceiverIdAndInputId(inviteId, token.getUserId())) {
            throw new EntityAccessDeniedException(format("User %s is not receiver", token.getUserId()));
        }
    }

    @Before("execution(public * org.jenjetsu.com.todo.controller.DashboardController.inviteUser(..))")
    public void protectInviteUserEndpoint(JoinPoint jp) {
        UserDashboardInviteDTO inviteDto = (UserDashboardInviteDTO) jp.getArgs()[0];
        JwtUserIdAuthenticationToken token = (JwtUserIdAuthenticationToken) jp.getArgs()[1];
        if(!this.dashboardRep.existsById(inviteDto.dashboardId())) {
            throw new EntityNotFoundException(format("Dashboard with id %s not exists", inviteDto.dashboardId()));
        }
        if (!dashboardRep.isUserDashbordCreator(inviteDto.dashboardId(), token.getUserId())) {
            throw new EntityAccessDeniedException(format("User %s is not dashboard %s owner",
                                                         token.getUserId(),
                                                         inviteDto.dashboardId()
                                                        ));
        }
    }

    @Before("execution(public * org.jenjetsu.com.todo.controller.TaskController.saveNewTask(..))")
    public void protectSaveNewTaskEndpoint(JoinPoint jp) {
        TaskCreateDTO createDto = (TaskCreateDTO) jp.getArgs()[0];
        JwtUserIdAuthenticationToken token = (JwtUserIdAuthenticationToken) jp.getArgs()[1];
        if(!this.dashboardRep.existsById(createDto.dashboardId())) {
            throw new EntityNotFoundException(format("Dashboard with id %s not exists", createDto.dashboardId()));
        }
        if(!this.dashboardRep.isUserInDashboard(createDto.dashboardId(), token.getUserId())) {
            throw new EntityAccessDeniedException(format("User is not dashboard %s member",
                                                         createDto.dashboardId()
                                                        ));
        }
    }

    @Before("execution(public * org.jenjetsu.com.todo.controller.TaskController.linkUserWithTask(..))")
    public void protectLinkUserWithTaskEndpoint(JoinPoint jp) {
        TaskUserLinkDTO linkDto = (TaskUserLinkDTO) jp.getArgs()[0];
        JwtUserIdAuthenticationToken token = (JwtUserIdAuthenticationToken) jp.getArgs()[1];
        if(!this.taskRep.isUserTaskCreator(token.getUserId(), linkDto.taskId())) {
            throw new EntityAccessDeniedException(format("Only creator can add user to task %s",
                                                         linkDto.taskId()
                                                        ));
        }
    }

    @Before("execution(public * org.jenjetsu.com.todo.controller.TaskController.deleteTask(..)) || " + 
            "execution(public * org.jenjetsu.com.todo.controller.TaskController.restoreTask(..))")
    public void protectTaskDeleteStatusEndpoint(JoinPoint jp) {
        UUID taskId = (UUID) jp.getArgs()[0];
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUserIdAuthenticationToken token = (JwtUserIdAuthenticationToken) auth;
        if(!this.taskRep.isUserTaskCreator(token.getUserId(), taskId)) {
            throw new EntityAccessDeniedException(format("User %s is not task %s creator",
                                                         token.getUserId(), taskId));
        }
    }

    @Before("execution(public * org.jenjetsu.com.todo.controller.TaskController.changeTaskStatus(..))")
    public void protectChangeTaskStatusEndpoint(JoinPoint jp) {
        ChangeStatusDTO statusDto = (ChangeStatusDTO) jp.getArgs()[0];
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUserIdAuthenticationToken token = (JwtUserIdAuthenticationToken) auth;
        if(!this.taskRep.isUserTaskCreatorOrMember(token.getUserId(), statusDto.taskId())) {
            throw new EntityAccessDeniedException(format("User %s is not task %s creator or member",
                                                         token.getUserId(), statusDto.taskId()));
        }
    }

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
