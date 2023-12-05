package org.jenjetsu.com.todo.service;

import java.util.UUID;

import org.jenjetsu.com.todo.model.DashboardUserInvite;

public interface DashboardUserInviteService extends CRUDService<DashboardUserInvite, UUID> {
    
    public void acceptInvite(UUID inviteId);
    public void declineInvite(UUID inviteId);
}
