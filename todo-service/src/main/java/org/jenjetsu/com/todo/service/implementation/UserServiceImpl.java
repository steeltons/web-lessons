package org.jenjetsu.com.todo.service.implementation;

import static java.lang.String.format;
import java.util.List;
import java.util.UUID;

import org.jenjetsu.com.todo.exception.EntityCreateException;
import org.jenjetsu.com.todo.exception.EntityNotFoundException;
import org.jenjetsu.com.todo.exception.EntityValidateException;
import org.jenjetsu.com.todo.model.Task;
import org.jenjetsu.com.todo.model.User;
import org.jenjetsu.com.todo.repository.TaskRepository;
import org.jenjetsu.com.todo.repository.UserRepository;
import org.jenjetsu.com.todo.service.UserService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl extends SimpleJpaService<User, UUID> implements UserService {

    private final UserRepository userRep;
    private final TaskRepository taskRep;

    public UserServiceImpl(UserRepository userRep,
                           TaskRepository taskRep) {
        super(User.class);
        this.userRep = userRep;
        this.taskRep = taskRep;
    }


    @Override
    public List<Task> readAllUserTasksWithUserActivities(UUID userId) {
        if(!this.existsById(userId)) {
            throw new EntityNotFoundException(format("No such User with id %s", userId.toString()));
        }
        return taskRep.findAllByUserIdWithActivities(userId);
    }

    @Override
    public User readByIdFetchAll(UUID userId) {
        // return this.userRep.findByIdFetchAll(userId)
        //     .orElseThrow(() -> new EntityNotFoundException(format("User with id %s not exists", userId)));
        return null;
    }

    @Override
    public User readUserByUsername(String username) {
        return this.userRep.findByUsername(username)
                .orElseThrow((() -> new EntityNotFoundException(format("User with username %s not exists", username))));
    }

    @Override
    public User readUserByEmail(String email) {
        return this.userRep.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(format("User with email %s not exists", email)));
    }

    @Override
    public User readByUsername(String username) {
        return this.userRep.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(format("User with username %s not exists", username)));
    }

    @Override
    public boolean existsByUsername(String username) {
        return username != null && this.userRep.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return email != null && this.userRep.existsByEmail(email);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void blockUser(UUID userId) {
        User user = readById(userId);
        user.setBlocked(true);
    }

    @Override
    public User createFromJwt(Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        if(username == null) {
            throw new EntityValidateException("No preferred_username in jwt token to save user");
        }
        String email = jwt.getClaimAsString("email");
        if(email == null) {
            throw new EntityValidateException("No email in jwt token to save user");
        }
        User user = User.builder()
                .username(username)
                .email(email)
                .firstname(jwt.getClaimAsString("given_name"))
                .lastname(jwt.getClaimAsString("family_name"))
                .blocked(false)
                .build();
        try {
            return this.create(user);
        } catch (Exception e) {
            throw new EntityCreateException(format("Impossible to save User with username %s", username), e);
        }
    }
}
