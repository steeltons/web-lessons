package org.jenjetsu.com.todo.repository;

import java.util.UUID;

import org.jenjetsu.com.todo.model.Dashboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DashboardRepository extends JpaRepository<Dashboard, UUID> {
    
    @Query(
        value = "SELECT (COUNT(d) > 0) " +
                "FROM t_dashboard d " + 
                "WHERE d.dashboardId = :dashboardId " +
                "AND d.createdBy.userId = :userId"
    )
    public boolean isUserDashbordCreator(@Param("dashboardId") UUID dashboardId, 
                                         @Param("userId") UUID userId);

    @Query(
        value = "SELECT (COUNT(d) > 0) " +
                "FROM t_dashboard d " +
                "LEFT OUTER JOIN d.userList u " + 
                "WHERE d.dashboardId = :dashboardId " +
                "AND u.userId = :userId"
    )
    public boolean isUserInDashboard(@Param("dashboardId") UUID dashboardId,
                                     @Param("userId") UUID userId);
}
