package org.jenjetsu.com.todo.repository;

import org.jenjetsu.com.todo.model.TaskActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TaskActivityRepository extends JpaRepository<TaskActivity, UUID> {

    @Query("SELECT ta FROM t_task_activity ta LEFT OUTER JOIN ta.user u " +
            "WHERE u.userId=:userId " +
            "AND ta.deleted = FALSE ")
    public List<TaskActivity> findAllByUserIdNotDeleted(@Param("userId") UUID userId);

    @Query("SELECT ta FROM t_task_activity ta WHERE ta.createdBy.userId = :creatorId")
    public List<TaskActivity> findAllByCreatorId(@Param("creatorId") UUID creatorId);
}
