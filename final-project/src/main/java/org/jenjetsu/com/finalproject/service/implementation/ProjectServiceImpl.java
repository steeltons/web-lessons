package org.jenjetsu.com.finalproject.service.implementation;

import static java.lang.String.format;
import java.sql.Date;
import java.time.Instant;
import java.util.UUID;

import org.jenjetsu.com.finalproject.exception.EntityNotFoundException;
import org.jenjetsu.com.finalproject.exception.EntityValidateException;
import org.jenjetsu.com.finalproject.model.Project;
import org.jenjetsu.com.finalproject.repository.ProjectRepository;
import org.jenjetsu.com.finalproject.repository.UserRepository;
import org.jenjetsu.com.finalproject.service.ProjectService;
import org.springframework.stereotype.Service;

@Service
public class ProjectServiceImpl extends SimpleJpaService<Project, UUID>
                                implements ProjectService {
    
    private final ProjectRepository projectRep;
    private final UserRepository userRep;

    public ProjectServiceImpl(ProjectRepository projectRep,
                              UserRepository userRep) {
        super(Project.class, projectRep);
        this.projectRep = projectRep;
        this.userRep = userRep;
    }

    @Override
    public Project createEntity(Project raw) {
        raw.setStartDate(new java.sql.Date(Date.from(Instant.now()).getTime()));
        raw.setDeleted(false);
        raw.setProjectId(null);
        return super.createEntity(raw);
    }

    @Override
    public void addUserToProject(UUID userId, UUID projectId) {
        if(!this.existsById(projectId)) {
            throw new EntityNotFoundException(format("Project with id %s not exists", projectId));
        }
        if(userId == null || !userRep.existsById(userId)) {
            throw new EntityNotFoundException(format("User with id %s not exists", userId));
        }
        if(this.projectRep.isUserInProject(userId, projectId)) {
            throw new EntityValidateException(format("User %s is already in project", userId));
        }
        this.projectRep.addUserToDashboard(userId, projectId);
    }

    @Override
    public void removeUserFromProject(UUID userId, UUID projectId) {
        if(!this.existsById(projectId)) {
            throw new EntityNotFoundException(format("Project with id %s not exists", projectId));
        }
        if(userId == null || !userRep.existsById(userId)) {
            throw new EntityNotFoundException(format("User with id %s not exists", userId));
        }
        if(!this.projectRep.isUserInProject(userId, projectId)) {
            throw new EntityValidateException(format("User %s does not even present in project", userId));
        }
        this.projectRep.removeUserFromProject(userId, projectId);
    }
}
