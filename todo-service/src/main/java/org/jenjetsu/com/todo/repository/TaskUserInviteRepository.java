package org.jenjetsu.com.todo.repository;

import java.util.UUID;

import org.jenjetsu.com.todo.model.TaskUserInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskUserInviteRepository extends JpaRepository<TaskUserInvite, UUID> {
    
    @Query(
        value = "SELECT (COUNT(tui) > 0) " +
                "FROM t_task_user_invite tui " +
                "LEFT OUTER JOIN tui.receiver r " +
                "WHERE tui.taskUserInviteId = :inviteId " +
                "AND r.userId = :userId"
    )
    public boolean isUserInviteReceiver(@Param("userId") UUID userId, 
                                        @Param("inviteId") UUID inviteId);
}
