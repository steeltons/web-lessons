package org.jenjetsu.com.todo.service;

import java.util.List;
import java.util.UUID;

import org.jenjetsu.com.todo.dto.ChangeStatusDTO;
import org.jenjetsu.com.todo.model.TaskActivity;

public interface TaskActivityService extends CRUDService<TaskActivity, UUID> {

    public List<TaskActivity> readAllByUserId(UUID userId);
    public List<TaskActivity> readAllByCreatorId(UUID creatorId);
    public void changeActivityStatus(ChangeStatusDTO statusDTO);
    public void changeActivityDeleteStatus(UUID activityId, boolean deleteStatus);
}
