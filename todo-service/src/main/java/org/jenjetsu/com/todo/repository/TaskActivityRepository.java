package org.jenjetsu.com.todo.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.jenjetsu.com.todo.model.TaskActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskActivityRepository extends JpaRepository<TaskActivity, UUID> {

    @Query("SELECT ta FROM t_task_activity ta LEFT OUTER JOIN ta.user u " +
            "WHERE u.userId=:userId " +
            "AND ta.deleted = FALSE ")
    public List<TaskActivity> findAllByUserIdNotDeleted(@Param("userId") UUID userId);

    @Query("SELECT ta FROM t_task_activity ta WHERE ta.createdBy.userId = :creatorId")
    public List<TaskActivity> findAllByCreatorId(@Param("creatorId") UUID creatorId);

    @Query(
        value = "SELECT (COUNT(a) > 0) " +
                "FROM t_task_activity a " +
                "WHERE a.taskActivityId = :activityId " +
                "AND a.createdBy.userId = :userId"
    )
    public boolean isUserActivityCreator(@Param("userId") UUID userId, 
                                         @Param("activityId") UUID activityId);

    
    @Query(
        value = "SELECT (COUNT(a) > 0) " +
                "FROM t_task_activity a " +
                "WHERE a.taskActivityId = :activityId " +
                "AND (a.createdBy.userId = :userId OR a.user.userId = :userId)"
    )
    public boolean isUserActivityCreatorOrMember(@Param("userId") UUID userId, 
                                                 @Param("activityId") UUID activityId);

    @Query(
        value = "SELECT a.task.taskId " + 
                "FROM t_task_activity a " + 
                "WHERE a.taskActivityId = :activityId"
    )
    public Optional<UUID> getTaskIdByActivityId(@Param("activityId") UUID activityId);
}
