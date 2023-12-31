package org.jenjetsu.com.todo.aspect;

import static java.lang.String.format;
import java.util.UUID;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.jenjetsu.com.todo.dto.ChangeStatusDTO;
import org.jenjetsu.com.todo.dto.TakeTaskDTO;
import org.jenjetsu.com.todo.dto.TaskCreateDTO;
import org.jenjetsu.com.todo.dto.TaskUserLinkDTO;
import org.jenjetsu.com.todo.exception.EntityAccessDeniedException;
import org.jenjetsu.com.todo.exception.EntityNotFoundException;
import org.jenjetsu.com.todo.repository.DashboardRepository;
import org.jenjetsu.com.todo.repository.TaskRepository;
import org.jenjetsu.com.todo.security.JwtUserIdAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@Aspect
@RequiredArgsConstructor
public class TaskPreEndpointProtectAsect {
    
    private final TaskRepository taskRep;
    private final DashboardRepository dashboardRep;
    
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
    @Deprecated(forRemoval = true)
    public void protectLinkUserWithTaskEndpoint(JoinPoint jp) {
        TaskUserLinkDTO linkDto = (TaskUserLinkDTO) jp.getArgs()[0];
        JwtUserIdAuthenticationToken token = (JwtUserIdAuthenticationToken) jp.getArgs()[1];
        if(!this.taskRep.isUserTaskCreator(token.getUserId(), linkDto.taskId())) {
            throw new EntityAccessDeniedException(format("Only creator can add user to task %s",
                                                         linkDto.taskId()
                                                        ));
        }
    }

    @Before("execution(public * org.jenjetsu.com.todo.controller.TaskController.takeTask(..))")
    public void protectTakeTaskEndpoint(JoinPoint jp) {
        TakeTaskDTO takeDto = (TakeTaskDTO) jp.getArgs()[0];
        JwtUserIdAuthenticationToken token = (JwtUserIdAuthenticationToken) jp.getArgs()[1];
        if(!this.dashboardRep.isUserInDashboard(takeDto.dashboardId(), token.getUserId())) {
            throw new EntityAccessDeniedException(format("User with id %s is not memeber of task dashboard %s",
                                                      token.getUserId(), takeDto.dashboardId()));
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
}
