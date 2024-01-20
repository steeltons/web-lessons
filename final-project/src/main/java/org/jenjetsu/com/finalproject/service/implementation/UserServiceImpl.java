package org.jenjetsu.com.finalproject.service.implementation;

import static java.lang.String.format;
import java.util.List;
import java.util.UUID;

import org.jenjetsu.com.finalproject.exception.EntityNotFoundException;
import org.jenjetsu.com.finalproject.model.Subtask;
import org.jenjetsu.com.finalproject.model.User;
import org.jenjetsu.com.finalproject.repository.SubtaskRepository;
import org.jenjetsu.com.finalproject.repository.UserRepository;
import org.jenjetsu.com.finalproject.service.UserService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends SimpleJpaService<User, UUID>
                             implements UserService {
    
    private final UserRepository userRep;
    private final SubtaskRepository subtaskRep;

    public UserServiceImpl(UserRepository userRep,
                           SubtaskRepository subtaskRep) {
        super(User.class, userRep);
        this.userRep = userRep;
        this.subtaskRep = subtaskRep;
    }

    @Override
    @Cacheable(value = "user-id")
    public UUID readUserIdByUsername(String username) {
        return this.userRep.findUserIdByUsername(username)
                   .orElseThrow(() -> new EntityNotFoundException(format("User with username %s not found",
                                                                          username)));
    }

    @Override
    @Cacheable(value = "user-id")
    public UUID readUserIdByEmail(String email) {
        return this.userRep.findUserIdByEmail(email)
                   .orElseThrow(() -> new EntityNotFoundException(format("User with email %s not found",
                                                                          email)));
    }

    @Override
    public List<Subtask> readUserSubtasks(UUID userId, UUID projectId, boolean skipCompleted) {

        return null;
    }

    public boolean existsByUsername(String username) {
        return (username != null) && this.userRep.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return (email != null) && this.userRep.existsByEmail(email);
    }
}
