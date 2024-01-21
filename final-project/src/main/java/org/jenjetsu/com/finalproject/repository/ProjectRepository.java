package org.jenjetsu.com.finalproject.repository;

import java.util.UUID;

import org.jenjetsu.com.finalproject.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    
    @Query(
        value = """
                    INSERT INTO t_user_project(user_id, project_id)
                    VALUES(:userId, :projectId)
                """, nativeQuery = true
    )
    @Modifying
    @Transactional
    public void addUserToDashboard(@Param("userId") UUID userId, 
                                   @Param("projectId") UUID projectId);

    @Query(
        value = """
                    DELETE FROM t_user_project
                    WHERE user_id = :userId
                    AND project_id = :projectId
                """, nativeQuery = true
    )
    @Modifying  
    @Transactional                             
    public void removeUserFromProject(@Param("userId") UUID userId, 
                                      @Param("projectId") UUID projectId);

    @Query(
        value = """
                    SELECT EXISTS(
                        SELECT 1 FROM t_user_project tup
                        WHERE tup.project_id = :projectId
                        AND tup.user_id = :userId
                    )
                """, nativeQuery = true
    )
    public boolean isUserInProject(@Param("userId") UUID userId, 
                                   @Param("projectId") UUID projectId);

    @Query(
        value = """
                    SELECT EXISTS(
                        SELECT 1 FROM t_project tp
                        WHERE tp.projectId = :projectId
                        AND tp.createdBy.userId = :userId
                    )
                """
    )
    public boolean isUserProjectCreator(@Param("userId") UUID userId,
                                        @Param("projectId") UUID projectId);

    @Query(
        value = """
                    SELECT EXISTS(
                        SELECT 1 FROM t_user_project tup
                        WHERE tup.user_id = :userId
                        AND tup.project_id = :projectId
                    )
                """, nativeQuery = true
    )
    public boolean isUserProjectMember(@Param("userId") UUID userId,
                                       @Param("projectId") UUID projectId);
}
