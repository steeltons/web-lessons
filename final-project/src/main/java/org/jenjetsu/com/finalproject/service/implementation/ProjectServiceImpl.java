package org.jenjetsu.com.finalproject.service.implementation;

import java.sql.Date;
import java.time.Instant;
import java.util.UUID;

import org.jenjetsu.com.finalproject.model.Project;
import org.jenjetsu.com.finalproject.repository.ProjectRepository;
import org.jenjetsu.com.finalproject.service.ProjectService;
import org.springframework.stereotype.Service;

@Service
public class ProjectServiceImpl extends SimpleJpaService<Project, UUID>
                                implements ProjectService {
    
    private final ProjectRepository projectRep;

    public ProjectServiceImpl(ProjectRepository projectRep) {
        super(Project.class, projectRep);
        this.projectRep = projectRep;
    }

    @Override
    public Project createEntity(Project raw) {
        raw.setStartDate(new java.sql.Date(Date.from(Instant.now()).getTime()));
        raw.setDeleted(false);
        raw.setProjectId(null);
        return super.createEntity(raw);
    }
}
