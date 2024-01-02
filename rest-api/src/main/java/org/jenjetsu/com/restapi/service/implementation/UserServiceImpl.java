package org.jenjetsu.com.restapi.service.implementation;

import static java.lang.String.format;
import java.util.UUID;

import org.jenjetsu.com.restapi.exception.EntityNotFoundException;
import org.jenjetsu.com.restapi.exception.EntityValidateException;
import org.jenjetsu.com.restapi.model.User;
import org.jenjetsu.com.restapi.repository.UserRepository;
import org.jenjetsu.com.restapi.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends SimpleJpaService<User, UUID>
                             implements UserService {
    

    private final UserRepository userRep;
    
    public UserServiceImpl(UserRepository userRep) {
        super(User.class, userRep);
        this.userRep = userRep;
    }

    @Override
    public User createEntity(User raw) {
        if (raw.getUsername() == null || raw.getEmail() == null) {
            throw new EntityValidateException("Username or email is null");
        }
        if(userRep.existsByUsername(raw.getUsername())) {
            throw new EntityValidateException(format("User with username %s is already exists", 
                                                     raw.getUsername()));
        }
        if(userRep.existsByEmail(raw.getEmail())) {
            throw new EntityValidateException(format("User with email %s is already exists", 
                                                     raw.getEmail()));
        }
        return super.createEntity(raw);
    }

    @Override
    public User readByIdFetchTasks(UUID userId) {
        return userRep.findByIdFetchTasks(userId)
                      .orElseThrow(() -> new EntityNotFoundException(format("User with id %s not found", 
                                                                            userId)));
    }
}
