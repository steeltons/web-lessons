package org.jenjetsu.com.todo.repository;

import java.util.UUID;

import org.jenjetsu.com.todo.model.TaskComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskCommentRepository extends JpaRepository<TaskComment, Long> {

    @Query(
        value = "SELECT c.task.taskId FROM t_task_comment c " +
                "WHERE c.taskCommentId = :commentId"
    )
    public UUID getTaskIdByCommentId(@Param("commentId") Long commentId);

    @Query(
        value = "SELECT (COUNT(c) > 0) " +
                "FROM t_task_comment c " +
                "LEFT OUTER JOIN c.uploadedBy u " +
                "WHERE c.taskCommentId = :commentId " +
                "AND u.userId = :userId"
    )
    public boolean isUserCommentCreator(@Param("userId") UUID userId, 
                                        @Param("commentId") Long commentId);
}
