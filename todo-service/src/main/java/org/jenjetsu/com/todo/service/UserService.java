package org.jenjetsu.com.todo.service;

import java.util.List;
import java.util.UUID;

import org.jenjetsu.com.todo.model.Task;
import org.jenjetsu.com.todo.model.User;
import org.springframework.security.oauth2.jwt.Jwt;

public interface UserService extends CRUDService<User, UUID> {

    public List<Task> readAllUserTasksWithUserActivities(UUID userId);
    public User readByIdFetchAll(UUID userId);
    public User readByUsername(String login);
    public boolean existsByUsername(String username);
    public boolean existsByEmail(String email);
    public User createFromJwt(Jwt jwt);
    public User readUserByUsername(String username);
    public User readUserByEmail(String email);
    public void blockUser(UUID userId);

}
