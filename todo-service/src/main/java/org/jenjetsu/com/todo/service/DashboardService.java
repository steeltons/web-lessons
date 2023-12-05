package org.jenjetsu.com.todo.service;

import java.util.UUID;

import org.jenjetsu.com.todo.model.Dashboard;

public interface DashboardService extends CRUDService<Dashboard, UUID> {
 
    public void changeDashboardDeleteStatus(UUID dashboardId, boolean status);
}
