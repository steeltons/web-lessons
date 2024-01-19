package org.jenjetsu.com.finalproject.controller;

import java.util.UUID;

import org.jenjetsu.com.finalproject.deserializer.ProjectDeserializer;
import org.jenjetsu.com.finalproject.model.Project;
import org.jenjetsu.com.finalproject.serializer.ProjectSerializer;
import org.jenjetsu.com.finalproject.service.ProjectService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/projects")
public class ProjectController extends CRUDController<Project, UUID>{
    
    private final ProjectService projectService;
    
    public ProjectController(ProjectService projectService,
                             ProjectSerializer serializer,
                             ProjectDeserializer deserializer) {
        super(projectService);
        this.projectService = projectService;
        super.setModelDeserializer(deserializer);
        super.setModelSerializer(serializer);
    }
}
