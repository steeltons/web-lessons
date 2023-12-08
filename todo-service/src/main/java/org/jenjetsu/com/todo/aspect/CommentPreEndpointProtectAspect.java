package org.jenjetsu.com.todo.aspect;

import static java.lang.String.format;
import java.util.UUID;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.jenjetsu.com.todo.dto.CommentCreateDTO;
import org.jenjetsu.com.todo.exception.EntityAccessDeniedException;
import org.jenjetsu.com.todo.repository.TaskCommentRepository;
import org.jenjetsu.com.todo.repository.TaskRepository;
import org.jenjetsu.com.todo.security.JwtUserIdAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@Aspect
@RequiredArgsConstructor
public class CommentPreEndpointProtectAspect {
    
    private final TaskCommentRepository commentRep;
    private final TaskRepository taskRep;

    @Before("execution(public * org.jenjetsu.com.todo.controller.TaskCommentController.createComment(..))")
    public void protectCommentCreateEndpoint(JoinPoint jp) {
        CommentCreateDTO createDto = (CommentCreateDTO) jp.getArgs()[0];
        JwtUserIdAuthenticationToken token = (JwtUserIdAuthenticationToken) jp.getArgs()[1];
        if(!this.taskRep.isUserTaskCreatorOrMember(token.getUserId(), createDto.taskId())) {
            throw new EntityAccessDeniedException(format("User %s is not task creator or member",
                                                         token.getUserId(), createDto.taskId()));
        }
    }

    @Before("execution(public * org.jenjetsu.com.todo.controller.TaskCommentController.getCommentById(..))")
    public void protectGetCommentByIdEndpoint(JoinPoint jp) {
        Long commentId = (Long) jp.getArgs()[0];
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUserIdAuthenticationToken token = (JwtUserIdAuthenticationToken) auth;
        UUID taskId = this.commentRep.getTaskIdByCommentId(commentId);
        if(!this.taskRep.isUserTaskCreatorOrMember(token.getUserId(), taskId)) {
            throw new EntityAccessDeniedException(format("User %s is not task creator or member",
                                                         token.getUserId(), taskId));
        }
    }

    @Before("execution(public * org.jenjetsu.com.todo.controller.TaskCommentController.deleteComment(..))")
    public void protectDeleteCommentEndpoint(JoinPoint jp) {
        Long commentId = (Long) jp.getArgs()[0];
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUserIdAuthenticationToken token = (JwtUserIdAuthenticationToken) auth;
        if(!this.commentRep.isUserCommentCreator(token.getUserId(), commentId)) {
            throw new EntityAccessDeniedException(format("User %s is not comment with id %d creator",
                                                         token.getUserId(), commentId));
        }
    }
}
