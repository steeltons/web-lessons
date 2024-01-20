package org.jenjetsu.com.finalproject.dto;

import org.jenjetsu.com.finalproject.model.User;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    description = "Dto to create new user", 
    type = "object", 
    requiredProperties = "user_id"   
)
public record UserCreateDTO(
    @JsonProperty(required = true)
    @Schema(type = "string", example = "stey")
    String username, 
    @JsonProperty(required = true)
    @Schema(type = "string", format = "email", example = "some@mail.sru")
    String email, 
    @Schema(type = "string", example = "Stanislav")
    String firstname, 
    @Schema(type = "string", example = "Tsvetkov")
    String lastname) {

    public static User convert(UserCreateDTO dto) {
        return User.builder()
                   .username(dto.username)
                   .email(dto.email)
                   .firstname(dto.firstname)
                   .lastname(dto.lastname)
                   .build();
    }
    
}
