package org.jenjetsu.com.finalproject.validator;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

import org.jenjetsu.com.finalproject.exception.EntityValidateException;
import org.jenjetsu.com.finalproject.model.Task;
import org.jenjetsu.com.finalproject.repository.TaskRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskValidator {
    
    private final TaskRepository taskRep;
    
    public void validateTaskDatesRangeInProject(Task task) {
        UUID projectId = task.getProject().getProjectId();
        Date start = task.getStartDate();
        Date end = task.getEndDate();
        if(this.taskRep.isTaskDatesNotOverProject(projectId, start, end)) {
            throw new EntityValidateException("Task start or end date is step over project dates");
        }
    }

    public void validateTaskDependencies(Task task) {
        List<UUID> dependencyIdList = task.getTaskDependencyList()
                                          .stream()
                                          .map((dep) -> dep.getRequiredTask().getTaskId())
                                          .toList();
        Date start = task.getStartDate();
        Date end = task.getEndDate();
        if (this.taskRep.areTasksDateCross(dependencyIdList, start, end)) {
            throw new EntityValidateException("Dependment task is overlaps in time with dependencies");
        }
    }
}
