package org.jenjetsu.com.finalproject.repository;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

import org.jenjetsu.com.finalproject.model.Subtask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SubtaskRepository extends JpaRepository<Subtask, UUID>{
    
    @Query(
        value = """
                select 
                    ts.*,
                    tss.name,
                    tssd.date
                from t_subtask ts 
                left join t_task tt on ts.task_id = tt.task_id 
                left join t_subtask_status_date tssd on tssd.subtask_id = ts.subtask_id 
                left join t_subtask_status tss on tss.subtask_status_id = tssd.subtask_status_id 
                where ts.user_id = :userId
                and (tt.project_id = :projectId or CAST(:projectId AS UUID) is NULL)
                and tssd.date = (select MAX(tssd2.date) 
                                 from t_subtask_status_date tssd2 
                                 where tssd2.subtask_id = ts.subtask_id
                                )
                and (tss.subtask_status_id != 151 or :showAllSubtasks)
                """, nativeQuery = true
    )
    public List<Subtask> findAllUserSubtasks(@Param("userId") UUID userId,
                                             @Param("projectId") UUID projectId,
                                             @Param("showAllSubtasks") Boolean showAlsoCompleted);

    @Query(
        value = """
                    SELECT ts
                    FROM t_subtask ts
                    LEFT OUTER JOIN ts.task tt
                    WHERE ts.user.userId = :userId
                    AND (ts.startDate >= :startDate OR ts.endDate <= :endDate)
                    AND tt.project.projectId = :projectId
                """
    )
    public List<Subtask> findAllUserSubtaskBetweenDates(@Param("userId") UUID userId,
                                                        @Param("startDate") Date startDate,
                                                        @Param("endDate") Date endDate,
                                                        @Param("projectId") UUID projectId);

    @Query(
        value = """
                    SELECT 
                        new org.jenjetsu.com.finalproject.model.Subtask(
                            ts.subtaskId, ts.startDate, 
                            ts.endDate, ts.user.userId)
                    FROM t_subtask ts
                    WHERE ts.subtaskId = :subtaskId
                """
    )
    public Subtask getSubtaskInformationToCheckCross(@Param("subtaskId") UUID subtaskId);

    @Query(
        value = """
                    SELECT EXISTS(
                        SELECT 1 FROM t_task tt
                        WHERE tt.taskId = :taskId
                        AND (tt.startDate > :startDate OR tt.endDate < :endDate)
                    )
                """
    )
    public boolean isSubtaskLeadTimeStepOverTask(@Param("taskId") UUID taskId,
                                                 @Param("startDate") Date startDate,
                                                 @Param("endDate") Date endDate);

    @Query(
        value = """
                    SELECT tt.project.projectId
                    FROM t_task tt
                    LEFT OUTER JOIN tt.subtaskList ts
                    WHERE ts.subtaskId = :subtaskId 
                """
    )
    public UUID findProjectIdBySubtaskId(@Param("subtaskId") UUID subtaskId);

    @Override
    @Query(
        value = """
                    INSERT INTO t_subtask_delete(subtas_id)
                    VALUES (:subtaskId)
                """, nativeQuery = true
    )
    @Modifying
    public void deleteById(@Param("subtaskId") UUID subtaskId);
}
