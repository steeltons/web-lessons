package org.jenjetsu.com.finalproject.repository;

import java.util.UUID;

import org.jenjetsu.com.finalproject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
}
