package org.jenjetsu.com.finalproject.service.implementation;

import java.util.UUID;

import org.jenjetsu.com.finalproject.model.User;
import org.jenjetsu.com.finalproject.repository.UserRepository;
import org.jenjetsu.com.finalproject.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends SimpleJpaService<User, UUID>
                             implements UserService {
    
    private final UserRepository userRep;

    public UserServiceImpl(UserRepository userRep) {
        super(User.class, userRep);
        this.userRep = userRep;
    }
}
