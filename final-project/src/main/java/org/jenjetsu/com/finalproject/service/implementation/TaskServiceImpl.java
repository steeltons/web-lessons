package org.jenjetsu.com.finalproject.service.implementation;

import java.sql.Date;
import java.util.UUID;

import org.hibernate.Hibernate;
import org.jenjetsu.com.finalproject.exception.EntityValidateException;
import org.jenjetsu.com.finalproject.model.Subtask;
import org.jenjetsu.com.finalproject.model.Task;
import org.jenjetsu.com.finalproject.repository.SubtaskRepository;
import org.jenjetsu.com.finalproject.repository.TaskRepository;
import org.jenjetsu.com.finalproject.service.TaskService;
import org.jenjetsu.com.finalproject.validator.SubtaskValidator;
import org.jenjetsu.com.finalproject.validator.TaskValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskServiceImpl extends SimpleJpaService<Task, UUID>
                             implements TaskService {
    
    private final TaskRepository taskRep;
    private final TaskValidator taskValidator;
    private final SubtaskValidator subtaskValidator;
    private final SubtaskRepository subtaskRep;

    public TaskServiceImpl(TaskRepository taskRep,
                           TaskValidator taskValidator,
                           SubtaskValidator subtaskValidator,
                           SubtaskRepository subtaskRep) {
        super(Task.class, taskRep);
        this.taskRep = taskRep;
        this.taskValidator = taskValidator;
        this.subtaskValidator = subtaskValidator;
        this.subtaskRep = subtaskRep;
    }

    @Override
    public Task createEntity(Task raw) {
        raw.setTaskId(null);
        raw.setDeleted(false);
        taskValidator.validateTaskDatesRangeInProject(raw);
        taskValidator.validateTaskDependencies(raw);
        raw.getTaskDependencyList()
           .forEach((dep) -> dep.setTask(raw));
        return super.createEntity(raw);
    }

    @Override
    public Task updateEntity(Task newTask) {
        Task oldTask = this.readById(newTask.getTaskId());
        Date oldStartDate = oldTask.getStartDate();
        Date oldEndDate = oldTask.getEndDate();
        // oldTask.merge(newTask);
        // // if(oldTask.getStartDate().after(oldStartDate) || oldTask.getEndDate().before(oldEndDate)) {
        //     this.taskValidator.validateTaskDatesRangeInProject(oldTask);
        //     Hibernate.initialize(oldTask.getTaskDependencyList());
        //     this.taskValidator.validateTaskDependencies(oldTask);
        //     Hibernate.initialize(oldTask.getSubtaskList());
        //     this.moveSubtasksInTask(oldTask, oldStartDate, oldEndDate);
        // // }
        return super.updateEntity(oldTask);
    }

    private void moveSubtasksInTask(Task task, Date oldStartDate, Date oldEndDate) {
        long startDateMove = task.getStartDate().getTime() - oldStartDate.getTime();
        long endDateMove = task.getEndDate().getTime() - oldEndDate.getTime();
        if (startDateMove > 0l) {
            for (Subtask subtask : task.getSubtaskList()) {
                Date newSubtaskStartDate = new Date(subtask.getStartDate().getTime() + startDateMove);
                Date newSubtaskEndDate = new Date(subtask.getEndDate().getTime() + endDateMove);
                if (task.getEndDate().before(newSubtaskEndDate)) {
                    throw new EntityValidateException("TODO IMPLEMENT");
                }
                subtask.setStartDate(newSubtaskStartDate);
                subtask.setEndDate(newSubtaskEndDate);
                this.subtaskValidator.checkUserBusyInProject(subtask);
                this.subtaskRep.saveAndFlush(subtask);
            }
        }
        
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void restoreTask(UUID taskId) {
        Task task = this.readById(taskId);
        Hibernate.initialize(task.getTaskDependencyList());
        this.taskValidator.validateTaskDependencies(task);
        Hibernate.initialize(task.getSubtaskList());
        for (Subtask subtask : task.getSubtaskList()) {
            subtaskValidator.checkUserBusyInProject(subtask);
        }
        this.taskRep.restoreTask(taskId);
    }
}
