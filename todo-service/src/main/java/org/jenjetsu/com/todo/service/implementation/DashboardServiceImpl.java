package org.jenjetsu.com.todo.service.implementation;

import java.util.UUID;

import org.jenjetsu.com.todo.model.Dashboard;
import org.jenjetsu.com.todo.repository.DashboardRepository;
import org.jenjetsu.com.todo.service.DashboardService;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl extends SimpleJpaService<Dashboard, UUID> 
                                  implements DashboardService{
    
    private DashboardRepository dashboardRep;

    public DashboardServiceImpl(DashboardRepository dashboardRep) {
        super(Dashboard.class);
        this.dashboardRep = dashboardRep;
    }
}
