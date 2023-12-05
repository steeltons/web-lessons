package org.jenjetsu.com.todo.service.implementation;

import static java.lang.String.format;
import java.util.UUID;

import org.jenjetsu.com.todo.exception.EntityValidateException;
import org.jenjetsu.com.todo.model.Dashboard;
import org.jenjetsu.com.todo.repository.DashboardRepository;
import org.jenjetsu.com.todo.service.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardServiceImpl extends SimpleJpaService<Dashboard, UUID> 
                                  implements DashboardService{
    
    private DashboardRepository dashboardRep;

    public DashboardServiceImpl(DashboardRepository dashboardRep) {
        super(Dashboard.class, dashboardRep);
        this.dashboardRep = dashboardRep;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void changeDashboardDeleteStatus(UUID dashboardId, boolean deleteStatus) {
        Dashboard dashboard = this.readById(dashboardId);
        if(!dashboard.getIsDeleted().equals(deleteStatus)) {
            dashboard.setIsDeleted(deleteStatus);
            dashboard.getTaskList()
                     .stream()
                     .forEach((task) -> {
                                task.setDeleted(deleteStatus);
                                task.getActivityList().forEach((activity) -> activity.setDeleted(deleteStatus));
                             });
            this.update(dashboard);
        } else {
            String stringStatus = (dashboard.getIsDeleted()) ? "deleted" : "restored";
            throw new EntityValidateException(format("Dashboard %s is already %s", 
                                                     dashboardId, stringStatus));
        }
    }
}
