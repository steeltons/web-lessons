package org.jenjetsu.com.todo.service;

import org.jenjetsu.com.todo.dto.ChangeStatusDTO;
import org.jenjetsu.com.todo.model.TaskActivity;
import org.jenjetsu.com.todo.security.JwtUserIdAuthenticationToken;

import java.util.List;
import java.util.UUID;

public interface TaskActivityService extends CRUDService<TaskActivity, UUID> {

    public List<TaskActivity> readAllByUserId(UUID userId);
    public List<TaskActivity> readAllByCreatorId(UUID creatorId);
    public void changeActivityStatus(ChangeStatusDTO statusDTO, JwtUserIdAuthenticationToken token);

}
