package org.jenjetsu.com.finalproject.deserializer;

import org.jenjetsu.com.finalproject.model.User;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public class UserDeserializer implements Converter<ObjectNode, User> {
    
    @Override
    public User convert(ObjectNode node) {
        return User.builder()
                   .username(node.get("username").asText())
                   .email(node.get("email").asText())
                   .firstname(node.has("firstname") ? node.get("firstname").asText() : null)
                   .lastname(node.has("lastname") ? node.get("lastname").asText() : null)
                   .build();
    }
}
