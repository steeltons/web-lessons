package org.jenjetsu.com.finalproject.repository;

import java.util.UUID;

import org.jenjetsu.com.finalproject.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    
    @Query(
        value = """
                    INSERT INTO t_user_project(user_id, project_id)
                    VALUES(:userId, :projectId)
                """, nativeQuery = false
    )
    @Modifying
    public void addUserToDashboard(@Param("userId") UUID userId, 
                                   @Param("projectId") UUID projectId);

    @Query(
        value = """
                    DELETE FROM t_user_project
                    WHERE user_id = :userId
                    AND project_id = :projectId
                """, nativeQuery = false
    )
    @Modifying                               
    public void removeUserFromProject(@Param("userId") UUID userId, 
                                      @Param("projectId") UUID projectId);

    @Query(
        value = """
                    SELECT EXISTS(
                        SELECT 1 FROM t_user_project tup
                        WHERE tup.project_id = :projectId
                        AND tup.user_id = :user_id
                    )
                """, nativeQuery = true
    )
    public boolean isUserInProject(@Param("userId") UUID userId, 
                                   @Param("projectId") UUID projectId);
}
