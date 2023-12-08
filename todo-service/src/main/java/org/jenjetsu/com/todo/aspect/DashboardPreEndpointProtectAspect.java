package org.jenjetsu.com.todo.aspect;

import static java.lang.String.format;
import java.util.UUID;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.jenjetsu.com.todo.dto.UserDashboardInviteDTO;
import org.jenjetsu.com.todo.exception.EntityAccessDeniedException;
import org.jenjetsu.com.todo.exception.EntityNotFoundException;
import org.jenjetsu.com.todo.repository.DashboardRepository;
import org.jenjetsu.com.todo.repository.DashboardUserInviteRepository;
import org.jenjetsu.com.todo.security.JwtUserIdAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@Aspect
@RequiredArgsConstructor
public class DashboardPreEndpointProtectAspect {
    
    private final DashboardUserInviteRepository inviteRep;
    private final DashboardRepository dashboardRep;

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
}
