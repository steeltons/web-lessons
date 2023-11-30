package org.jenjetsu.com.todo.repository;

import java.util.UUID;

import org.jenjetsu.com.todo.model.DashboardUserInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DashboardUserInviteRepository extends JpaRepository<DashboardUserInvite, UUID> {
 
    @Query(
        value = "SELECT CASE WHEN (COUNT(dui) > 0) THEN TRUE ELSE FALSE END " +
                "FROM t_dashboard_user_invite dui " +
                "WHERE dui.dashboardUserInviteId = :inviteId " + 
                "AND dui.receiver.userId = :inputId"
    )
    public boolean compareReceiverIdAndInputId(@Param("inviteId") UUID inviteId, 
                                               @Param("inputId") UUID inputId);
}
