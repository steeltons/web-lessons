package org.jenjetsu.com.finalproject.aspect;

import java.util.UUID;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.jenjetsu.com.finalproject.dto.ProjectReturnDTO;
import org.jenjetsu.com.finalproject.dto.UserProjectDTO;
import org.jenjetsu.com.finalproject.exception.EntityAccessDeniedException;
import org.jenjetsu.com.finalproject.repository.ProjectRepository;
import org.jenjetsu.com.finalproject.security.model.BearerTokenAuthentication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@Aspect
@RequiredArgsConstructor
public class ProjectControllerAscpet {

    private final String ADMIN_ROLE = "ROLE_MODERATOR";
    
    private final ProjectRepository projectRep;
    
    @Before("execution (public * org.jenjetsu.com.finalproject.controller.ProjectController.inviteUserToProject(..))")
    public void protectInviteUserToProject(JoinPoint jp) {
        BearerTokenAuthentication token = this.getAuth();
        if(!this.isUserAdmin(token)) {
            UserProjectDTO dto = (UserProjectDTO) jp.getArgs()[0];
            if(!this.projectRep.isUserProjectCreator(token.getUserId(), dto.projectId())) {
            throw new EntityAccessDeniedException("User is not project owner");
        }
        }
    }

    @Before("execution (public * org.jenjetsu.com.finalproject.controller.ProjectController.kickUserFromProject(..))")
    public void protectKickUserFromProject(JoinPoint jp) {
        BearerTokenAuthentication token = this.getAuth();
        if(!this.isUserAdmin(token)) {
            UserProjectDTO dto = (UserProjectDTO) jp.getArgs()[0];
            if(!this.projectRep.isUserProjectCreator(token.getUserId(), dto.projectId())) {
                throw new EntityAccessDeniedException("User is not project owner");
            }
        }
    }

    @Before("execution (public * org.jenjetsu.com.finalproject.controller.ProjectController.delete(..))")
    public void protectProjectDelete(JoinPoint jp) {
        BearerTokenAuthentication token = this.getAuth();
        if(!this.isUserAdmin(token)) {
            UUID projectId = (UUID) jp.getArgs()[0];
            if(!this.projectRep.isUserProjectCreator(token.getUserId(), projectId)) {
                throw new EntityAccessDeniedException("User is not project owner");
            }
        }
    }

    @Before("execution (public * org.jenjetsu.com.finalproject.controller.ProjectController.patchProject(..))")
    public void protectPatchProject(JoinPoint jp) {
        BearerTokenAuthentication token = this.getAuth();
        if(!this.isUserAdmin(token)) {
            ProjectReturnDTO dto = (ProjectReturnDTO) jp.getArgs()[0];
            if(!this.projectRep.isUserProjectCreator(token.getUserId(), dto.projectId())) {
                throw new EntityAccessDeniedException("User is not project owner");
            }
        }
    }
    @Before("execution (public * org.jenjetsu.com.finalproject.controller.ProjectController.putProject(..))")
    public void protectPutProject(JoinPoint jp) {
        BearerTokenAuthentication token = this.getAuth();
        if(!this.isUserAdmin(token)) {
            ProjectReturnDTO dto = (ProjectReturnDTO) jp.getArgs()[0];
            if(!this.projectRep.isUserProjectCreator(token.getUserId(), dto.projectId())) {
                throw new EntityAccessDeniedException("User is not project owner");
            }
        }
    }

    private boolean isUserAdmin(BearerTokenAuthentication token) {
        return token.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter((authority) -> authority.equals(ADMIN_ROLE))
                    .findFirst()
                    .isPresent();
    }

    private BearerTokenAuthentication getAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (BearerTokenAuthentication) auth;
    }
}
