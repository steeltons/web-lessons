package org.jenjetsu.com.finalproject.repository;

import java.util.Optional;
import java.util.UUID;

import org.jenjetsu.com.finalproject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    @Query("SELECT t.userId FROM t_user t WHERE t.username = :username")
    public Optional<UUID> findUserIdByUsername(@Param("username") String username);
    @Query("SELECT t.userId FROM t_user t WHERE t.email = :email")
    public Optional<UUID> findUserIdByEmail(@Param("email") String email);
}
