package org.jenjetsu.com.finalproject.serializer;

import org.jenjetsu.com.finalproject.model.User;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public class UserSerializer implements Converter<User, ObjectNode> {
    
    @Override
    public ObjectNode convert(User user) {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("username", user.getUsername());
        objectNode.put("email", user.getEmail());
        objectNode.put("firstname", user.getFirstname());
        objectNode.put("lastName", user.getLastname());
        return objectNode;
    }
}
