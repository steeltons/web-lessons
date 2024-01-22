package org.jenjetsu.com.finalproject.repository;

import java.sql.Date;
import java.util.Collection;
import java.util.UUID;

import org.jenjetsu.com.finalproject.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    
    @Query(
        value = """
                SELECT EXISTS(
                    SELECT 1 FROM t_task tt
                    WHERE tt.taskId IN (:taskIdCollection)
                    AND tt.startDate <= :endDate
                    AND :startDate <= tt.endDate
                )
                """
    )
    public boolean areTasksDateCross(@Param("taskIdCollection") Collection<UUID> taskIdCollection, 
                                     @Param("startDate") Date startDate, 
                                     @Param("endDate") Date endDate);
    
    @Query(
        value = """
                    SELECT 
                        new org.jenjetsu.com.finalproject.model.Task(
                            tt.taskId, tt.startDate, tt.endDate) 
                    FROM t_task tt
                    WHERE tt.taskId = :taskId
                """
    )
    public Task findTaskInformationForCheckCross(@Param("taskId") UUID taskId);

    @Query(
        value = """
                    SELECT EXISTS (
                        SELECT 1 FROM t_project tp
                        WHERE tp.projectId = :projectId
                        AND (tp.startDate > :startDate OR tp.endDate < :endDate)
                    )
                """
    )
    public boolean isTaskDatesNotOverProject(@Param("projectId") UUID projectId,
                                            @Param("startDate") Date startDate,
                                            @Param("endDate") Date endDate);

    @Query(
        value = """
                    SELECT tp.projectId
                    FROM t_project tp
                    LEFT JOIN tp.taskList tt
                    WHERE tt.taskId = :taskId
                """
    )
    public UUID findProjectIdByTaskId(@Param("taskId") UUID taskId);

    @Override
    @Query(
        value = """
                    INSERT INTO t_task_delete(task_id)
                    VALUES (:taskId)
                """, nativeQuery = true
    )
    @Modifying
    public void deleteById(@Param("taskId") UUID taskId);

    @Query(
        value = """
                    DELETE FROM t_task_delete
                    WHERE task_id = :taskId
                """, nativeQuery = true
    )
    @Modifying
    public void restoreTask(@Param("taskId") UUID taskId);
}
