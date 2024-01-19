package org.jenjetsu.com.finalproject.service;

import java.util.List;
import java.util.UUID;

import org.jenjetsu.com.finalproject.model.Subtask;
import org.jenjetsu.com.finalproject.model.User;

public interface UserService extends CRUDService<User, UUID> {
    
    public UUID readUserIdByUsername(String username);
    public UUID readUserIdByEmail(String email);

    public List<Subtask> readUserSubtasks(UUID userId, UUID projectId, boolean skipCompleted);
}
