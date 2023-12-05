package org.jenjetsu.com.todo.service;

import java.util.UUID;

import org.jenjetsu.com.todo.model.TaskUserInvite;

public interface TaskUserInviteService extends CRUDService<TaskUserInvite, UUID> {
    
    public void accpetInvite(UUID inviteId);
    public void declineInvite(UUID inviteId);
}
