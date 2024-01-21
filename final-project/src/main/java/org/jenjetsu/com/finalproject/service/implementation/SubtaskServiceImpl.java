package org.jenjetsu.com.finalproject.service.implementation;

import static java.lang.String.format;
import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.jenjetsu.com.finalproject.exception.EntityNotFoundException;
import org.jenjetsu.com.finalproject.model.Subtask;
import org.jenjetsu.com.finalproject.model.SubtaskStatus;
import org.jenjetsu.com.finalproject.model.SubtaskStatusDate;
import org.jenjetsu.com.finalproject.repository.ProjectRepository;
import org.jenjetsu.com.finalproject.repository.SubtaskRepository;
import org.jenjetsu.com.finalproject.repository.SubtaskStatusRepository;
import org.jenjetsu.com.finalproject.repository.TaskRepository;
import org.jenjetsu.com.finalproject.service.SubtaskService;
import org.jenjetsu.com.finalproject.validator.SubtaskValidator;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class SubtaskServiceImpl extends SimpleJpaService<Subtask, UUID>
                                implements SubtaskService {
    private SubtaskStatus DEFAULT_STATUS;
    
    private final SubtaskRepository subtaskRep;
    private final TaskRepository taskRep;
    private final SubtaskStatusRepository subtaskStatusRep;
    private final ProjectRepository projectRep;
    private final SubtaskValidator subtaskValidator;


    public SubtaskServiceImpl(SubtaskRepository subtaskRep,
                              SubtaskStatusRepository subtaskStatusRep,
                              ProjectRepository projectRep,
                              TaskRepository taskRep,
                              SubtaskValidator subtaskValidator) {
        super(Subtask.class, subtaskRep);
        this.subtaskRep = subtaskRep;
        this.subtaskStatusRep = subtaskStatusRep;
        this.projectRep = projectRep;
        this.taskRep = taskRep;
        this.subtaskValidator = subtaskValidator;
    }

    @PostConstruct
    private void init() {
        this.DEFAULT_STATUS = this.subtaskStatusRep.findByName("CREATED")
                                  .orElseThrow(() -> new EntityNotFoundException("No such status with name CREATED"));
    }

    @Override
    protected Subtask createEntity(Subtask raw) {
        this.subtaskValidator.checkSubtaskLeadTimeCrossTask(raw);
        this.subtaskValidator.checkUserBusyInProject(raw);
        raw.setSubtaskId(null);
        raw.setDeleted(false);
        SubtaskStatusDate date = SubtaskStatusDate.builder()
                                                  .subtaskStatus(DEFAULT_STATUS)
                                                  .subtask(raw)
                                                  .date(new Date(Instant.now().toEpochMilli()))
                                                  .build();
        raw.getSubtaskStatusList().add(date);
        return super.createEntity(raw);
    }

    @Override
    public Subtask updateEntity(Subtask newSubtask) {
        Subtask checkSubtask = this.subtaskRep.getSubtaskInformationToCheckCross(newSubtask.getSubtaskId());
        Date checkStartDate = newSubtask.getStartDate() != null 
                              ? newSubtask.getStartDate() 
                              : checkSubtask.getStartDate();
        Date checkEndDate = newSubtask.getEndDate() != null 
                              ? newSubtask.getEndDate()
                              : checkSubtask.getEndDate();
        // Check if update subtask move our working dates on another dates
        if (!(checkSubtask.getStartDate().before(checkEndDate) 
              && checkStartDate.before(checkSubtask.getEndDate()))) {
            checkSubtask = Subtask.builder()
                                .subtaskId(newSubtask.getSubtaskId())
                                .startDate(checkStartDate)
                                .endDate(checkEndDate)
                                .user(checkSubtask.getUser())
                                .build();
            this.subtaskValidator.checkUserBusyInProject(checkSubtask);
        }
        return super.updateEntity(newSubtask);
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
