package org.jenjetsu.com.finalproject.service;

import java.util.UUID;

import org.jenjetsu.com.finalproject.model.Project;

public interface ProjectService extends CRUDService<Project, UUID> {
    
    public void addUserToProject(UUID userId, UUID projectId);
    public void removeUserFromProject(UUID userId, UUID projectId);
}
