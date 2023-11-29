package org.jenjetsu.com.todo.repository;

import org.jenjetsu.com.todo.model.Task;
import org.jenjetsu.com.todo.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @Override
    @Query("SELECT u FROM t_user u WHERE u.userId=:userId")
    public Optional<User> findById(@Param("userId") UUID id);

    @Query("SELECT u FROM t_user u WHERE u.username=:username")
    public Optional<User> findByUsername(@Param("username") String username);

    @Query("SELECT u FROM t_user u WHERE u.username=:username")
    public Optional<User> findUserByUsername(@Param("username") String username);

    @Query("SELECT u FROM t_user u WHERE u.email=:email")
    public Optional<User> findUserByEmail(@Param("email") String email);

    public boolean existsByUsername(String username);
    public boolean existsByEmail(String email);
}
