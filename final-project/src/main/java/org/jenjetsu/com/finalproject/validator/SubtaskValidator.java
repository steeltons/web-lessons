package org.jenjetsu.com.finalproject.validator;

import static java.lang.String.format;
import java.sql.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jenjetsu.com.finalproject.exception.EntityValidateException;
import org.jenjetsu.com.finalproject.model.Subtask;
import org.jenjetsu.com.finalproject.repository.SubtaskRepository;
import org.jenjetsu.com.finalproject.repository.TaskRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubtaskValidator {
    
    private final SubtaskRepository subtaskRep;
    private final TaskRepository taskRep;

    public void checkSubtaskLeadTimeCrossTask(Subtask subtask) {
        UUID taskId = subtask.getTask().getTaskId();
        Date startDate = subtask.getStartDate();
        Date endDate = subtask.getEndDate();
        if(this.subtaskRep.isSubtaskLeadTimeStepOverTask(taskId, startDate, endDate)) {
            throw new EntityValidateException("Time to complete subtask is step over of " +
                                              "time to complete task");
        }
    }

    public void checkUserBusyInProject(Subtask subtask) {
        UUID userId = subtask.getUser().getUserId();
        UUID projectId = null;
        if(subtask.getTask() != null && subtask.getTask().getTaskId() != null) {
            projectId = taskRep.findProjectIdByTaskId(subtask.getTask().getTaskId());
        } else {
            projectId = subtaskRep.findProjectIdBySubtaskId(subtask.getSubtaskId());
        }
        Date start = subtask.getStartDate();
        Date end = subtask.getEndDate();
        List<Subtask> userSubtaskAtThisTime = 
            this.subtaskRep.findAllUserSubtaskBetweenDates(userId, start, end, projectId);
        if(!userSubtaskAtThisTime.isEmpty()) {
            String subtaskIdLine = userSubtaskAtThisTime.stream()
                                        .map((sub) -> sub.getSubtaskId().toString())
                                        .collect(Collectors.joining(", "));
            throw new EntityValidateException(format("User %s is busy on this tasks: [%s]",
                                                     userId.toString(), subtaskIdLine));
        }
    }
}
