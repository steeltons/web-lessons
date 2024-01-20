package org.jenjetsu.com.finalproject.service.implementation;

import static java.lang.String.format;
import java.sql.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jenjetsu.com.finalproject.exception.EntityNotFoundException;
import org.jenjetsu.com.finalproject.exception.EntityValidateException;
import org.jenjetsu.com.finalproject.model.Subtask;
import org.jenjetsu.com.finalproject.repository.ProjectRepository;
import org.jenjetsu.com.finalproject.repository.SubtaskRepository;
import org.jenjetsu.com.finalproject.repository.SubtaskStatusRepository;
import org.jenjetsu.com.finalproject.service.SubtaskService;
import org.springframework.stereotype.Service;

@Service
public class SubtaskServiceImpl extends SimpleJpaService<Subtask, UUID>
                                implements SubtaskService {
    
    private final SubtaskRepository subtaskRep;
    private final SubtaskStatusRepository subtaskStatusRep;
    private final ProjectRepository projectRep;

    public SubtaskServiceImpl(SubtaskRepository subtaskRep,
                              SubtaskStatusRepository subtaskStatusRep,
                              ProjectRepository projectRep) {
        super(Subtask.class, subtaskRep);
        this.subtaskRep = subtaskRep;
        this.subtaskStatusRep = subtaskStatusRep;
        this.projectRep = projectRep;
    }

    @Override
    protected Subtask createEntity(Subtask raw) {
        raw.setSubtaskId(null);
        raw.setDeleted(false);
        UUID userId = raw.getUser().getUserId();
        Date start = raw.getStartDate();
        Date end = raw.getEndDate();
        List<Subtask> userSubtasksAtSameTime = this.subtaskRep.findAllUserSubtaskBetweenDates(userId, start, end);
        if(!userSubtasksAtSameTime.isEmpty()) {
            String subtasksIdInLine = userSubtasksAtSameTime.stream()
                                                            .map((sub) -> sub.getSubtaskId().toString())
                                                            .collect(Collectors.joining(", "));
            throw new EntityValidateException(format("User %s is already busy on this subtasks [%s]",
                                                      userId, subtasksIdInLine));

        }
        return raw;
    }

    @Override
    public List<Subtask> readAllUserSubtasksByParams(UUID userId, UUID projectId, String statusName) {
        if (statusName != null && !this.subtaskStatusRep.existsByName(statusName)) {
            throw new EntityNotFoundException(format("Subtask status name %s is not exists",
                                                     statusName));
        }
        if (projectId != null && !this.projectRep.existsById(projectId)) {
            throw new EntityNotFoundException(format("Project with id %s not exists", projectId.toString()));
        }
        return this.subtaskRep.findAllUserSubtasks(userId, projectId, statusName);
    }
}
