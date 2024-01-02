package org.jenjetsu.com.restapi.repository;

import java.util.Optional;
import java.util.UUID;

import org.jenjetsu.com.restapi.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    @EntityGraph(attributePaths = "taskList")
    @Query(
        value = "SELECT u FROM t_user u " +
                "WHERE u.userId = :userId"
    )
    public Optional<User> findByIdFetchTasks(@Param("userId") UUID userId);
    public boolean existsByUsername(String username);
    public boolean existsByEmail(String email);
}
