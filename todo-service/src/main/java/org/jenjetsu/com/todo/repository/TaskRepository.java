package org.jenjetsu.com.todo.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.jenjetsu.com.todo.model.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    @EntityGraph(attributePaths = "activityList")
    @Query("SELECT t FROM t_task t LEFT OUTER JOIN t.userList u " +
            "WHERE u.userId=:userId ")
    public List<Task> findAllByUserIdWithActivities(@Param("userId") UUID userId);

    @Query("SELECT t FROM t_task t WHERE t.createdBy.userId=:creatorId")
    public List<Task> findAllByCreatorId(@Param("creatorId") UUID creatorId);

    @Query("SELECT t FROM t_task t " +
            "LEFT OUTER JOIN t.userList u " +
            "WHERE u.userId = :userId AND t.deleted = FALSE")
    public List<Task> findAllByUserIdNotDeleted(@Param("userId") UUID userId);

    @EntityGraph(attributePaths = "userList")
    @Query("SELECT t FROM t_task t WHERE t.taskId=:taskId")
    public Optional<Task> findByIdFetchUserList(@Param("taskId") UUID taskId);

    @EntityGraph(attributePaths = "activityList")
    @Query("SELECT t FROM t_task t WHERE t.taskId = :taskId")
    public Optional<Task> findByIdFetchActivityList(@Param("taskId") UUID taskId);

    @EntityGraph(attributePaths = {"activityList", "userList", "commentList"})
    @Query("SELECT t FROM t_task t WHERE t.taskId = :taskId")
    public Optional<Task> findByIdFetchAll(@Param("taskId") UUID taskId);

    @Modifying
    @Transactional(propagation = Propagation.REQUIRED)
    @Query(
        value = "INSERT INTO t_user_dashboard_task(user_id, dashboard_id, task_id) " +
                "VALUES(:userId, :dashboardId, :taskId)",
        nativeQuery = true
    )
    public void addUserToTask(@Param("userId") UUID userId, 
                              @Param("taskId") UUID taskId, 
                              @Param("dashboardId") UUID dashboardId);

    @Query(
        value = "SELECT (COUNT(t) > 0) " +
                "FROM t_task t " +
                "LEFT OUTER JOIN t.userList u " + 
                "WHERE t.taskId = :taskId " +
                "AND u.userId = :userId"
    )
    public boolean isUserInTask(@Param("userId") UUID userId, @Param("taskId") UUID taskId);

    @Query(
        value = "SELECT (COUNT(t) > 0) " +
                "FROM t_task t " +
                "WHERE t.taskId = :taskId " + 
                "AND t.createdBy.userId = :userId"
    )
    public boolean isUserTaskCreator(@Param("userId") UUID userId, @Param("taskId") UUID taskId);

    @Query(
        value = "SELECT (COUNT(t) > 0) " +
                "FROM t_task t " + 
                "LEFT OUTER JOIN t.userList u " +
                "WHERE t.taskId = :taskId " +
                "AND (t.createdBy.userId = :userId OR u.userId = :userId)"
    )
    public boolean isUserTaskCreatorOrMember(@Param("userId") UUID userId, 
                                             @Param("taskId") UUID taskd);
}
