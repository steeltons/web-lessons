package org.jenjetsu.com.todo.repository;

import java.util.Optional;
import java.util.UUID;

import org.jenjetsu.com.todo.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("SELECT u FROM t_user u WHERE u.username=:username")
    public Optional<User> findByUsername(@Param("username") String username);

    @Query("SELECT u FROM t_user u WHERE u.email=:email")
    public Optional<User> findByEmail(@Param("email") String email);

    @EntityGraph(attributePaths= {"taskList", "dashboardList", "activityList"})
    @Query("SELECT u FROM t_user u WHERE u.userId = :userId")
    public Optional<User> findByIdFetchAll(@Param("userId") UUID userId);

    public boolean existsByUsername(String username);
    public boolean existsByEmail(String email);

    @Query("SELECT u.userId FROM t_user u WHERE u.username = :username")
    public Optional<UUID> getUserIdByUsername(@Param("username") String username);

    @Query("SELECT u.userId FROM t_user u WHERE u.email = :email")
    public Optional<UUID> getUserIdByEmail(@Param("email") String email);
}
