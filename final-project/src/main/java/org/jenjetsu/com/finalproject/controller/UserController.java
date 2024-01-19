package org.jenjetsu.com.finalproject.controller;

import java.util.UUID;

import org.jenjetsu.com.finalproject.deserializer.UserDeserializer;
import org.jenjetsu.com.finalproject.model.User;
import org.jenjetsu.com.finalproject.serializer.UserSerializer;
import org.jenjetsu.com.finalproject.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping("/api/v1/users")
public class UserController extends CRUDController<User, UUID> {
    
    private final UserService userService;

    public UserController(UserService userService,
                          UserSerializer serializer,
                          UserDeserializer deserializer) {
        super(userService);
        super.setModelDeserializer(deserializer);
        super.setModelSerializer(serializer);
        this.userService = userService;
    }

}
