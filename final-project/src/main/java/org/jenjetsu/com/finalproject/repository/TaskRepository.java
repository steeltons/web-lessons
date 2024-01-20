package org.jenjetsu.com.finalproject.repository;

import java.sql.Date;
import java.util.UUID;

import org.jenjetsu.com.finalproject.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    
    @Query(
        value = """
                select (COUNT(tt) > 0) from t_task tt 
                where tt.taskId = :requiredTaskId
                and tt.startDate <= :endDate
                and :startDate <= tt.endDate 
                """
    )
    public boolean areTasksDateCross(@Param("requiredTaskId") UUID requiredTaskId, 
                                     @Param("startDate") Date startDate, 
                                     @Param("endDate") Date endDate);
    
}
