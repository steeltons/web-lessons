package org.jenjetsu.com.todo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserCreateDTO(
        @JsonProperty(required = true) String username,
        @JsonProperty(required = true) String email,
        String firstname,
        String lastname,
        @JsonProperty(value = "phone_number") Long phoneNumber) {
}
