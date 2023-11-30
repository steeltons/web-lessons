package org.jenjetsu.com.todo.service;

import java.util.List;
import java.util.UUID;

import org.jenjetsu.com.todo.dto.ChangeStatusDTO;
import org.jenjetsu.com.todo.dto.TaskUserLinkDTO;
import org.jenjetsu.com.todo.model.Task;
import org.jenjetsu.com.todo.security.JwtUserIdAuthenticationToken;

public interface TaskService extends CRUDService<Task, UUID> {

    public Task readByIdFetchUserList(UUID taskId);
    public void linkUserWithTask(TaskUserLinkDTO dto, JwtUserIdAuthenticationToken token);
    public List<Task> readAllByUserId(UUID userID);
    public List<Task> readAllByCreatorId(UUID creatorId);
    public void changeTaskStatus(ChangeStatusDTO statusDTO);
    public void changeTaskDeleteStatus(UUID taskId, Boolean deleteStatus);
    public void addUserToTask(UUID userId, UUID taskId, UUID dashboardId);
}
