package org.jenjetsu.com.finalproject.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jenjetsu.com.finalproject.dto.ProjectCreateDTO;
import org.jenjetsu.com.finalproject.dto.ProjectReturnDTO;
import org.jenjetsu.com.finalproject.dto.UserProjectDTO;
import org.jenjetsu.com.finalproject.model.Project;
import org.jenjetsu.com.finalproject.model.User;
import org.jenjetsu.com.finalproject.security.model.BearerTokenAuthentication;
import org.jenjetsu.com.finalproject.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/projects")
public class ProjectController{
    
    private final ProjectService projectService;
    
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    public ProjectReturnDTO getProjectById(@PathVariable("projectId") UUID projectId) {
        Project project = this.projectService.readById(projectId);
        return ProjectReturnDTO.convert(project);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('MANAGER')")
    public Map<String, List<ProjectReturnDTO>> getAllProjects() {
        List<ProjectReturnDTO> dtos = this.projectService.readAll()
                                          .stream()
                                          .map(ProjectReturnDTO::convert)
                                          .toList();
        return Map.of("project_list", dtos);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> createProject(@RequestBody ProjectCreateDTO dto,
                                             BearerTokenAuthentication token) {
        Project raw = ProjectCreateDTO.convert(dto);
        User creator = User.builder().userId(token.getUserId()).build();
        raw.setCreatedBy(creator);
        raw.getUserList().add(creator);
        raw = this.projectService.create(raw);
        return Map.of("project_id", raw.getProjectId().toString());
    }

    @PostMapping("/invite")
    @ResponseStatus(HttpStatus.CREATED)
    public void inviteUserToProject(@RequestBody UserProjectDTO dto) {
        this.projectService.addUserToProject(dto.userId(), dto.projectId());
    }  

    @DeleteMapping("/{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("projectId") UUID projectId) {
        this.projectService.deleteById(projectId);
    }

    @DeleteMapping("/kick")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void kickUserFromProject(@RequestBody UserProjectDTO dto) {
        this.projectService.removeUserFromProject(dto.userId(), dto.projectId());
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void patchProject(@RequestBody ProjectReturnDTO dto) {
        Project mergingProject = ProjectReturnDTO.convertToProject(dto);
        Project mergableProject = this.projectService.readById(dto.projectId());
        Project newProject = mergableProject.patchModel(mergingProject);
        this.projectService.update(newProject);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void putProject(@RequestBody ProjectReturnDTO dto) {
        Project project = ProjectReturnDTO.convertToProject(dto);
        this.projectService.update(project);
    }
}
