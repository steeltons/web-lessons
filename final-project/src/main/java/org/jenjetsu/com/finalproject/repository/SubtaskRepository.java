package org.jenjetsu.com.finalproject.repository;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

import org.jenjetsu.com.finalproject.model.Subtask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SubtaskRepository extends JpaRepository<Subtask, UUID>{
    
    @Query(
        value = """
                select 
                    ts.* 
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
                and (tss.name != :statusName or CAST(:statusName as VARCHAR(64)) = null)
                """, nativeQuery = true
    )
    public List<Subtask> findAllUserSubtasks(@Param("userId") UUID userId,
                                             @Param("projectId") UUID projectId,
                                             @Param("statusName") String statusName);

    @Query(
        value = """
                    SELECT ts
                    FROM t_subtask ts
                    WHERE ts.userId = :userId
                    AND ts.startDate >= :startDate
                    AND ts.endDate <= :endDate
                """
    )
    public List<Subtask> findAllUserSubtaskBetweenDates(@Param("userId") UUID userId,
                                                        @Param("startDate") Date startDate,
                                                        @Param("endDate") Date endDate);
}
