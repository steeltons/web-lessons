package org.jenjetsu.com.restapi.service;

import java.util.UUID;

import org.jenjetsu.com.restapi.model.User;

public interface UserService extends JpaService<User, UUID> {
    
    public User readByIdFetchTasks(UUID userId);
}
