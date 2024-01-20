package org.jenjetsu.com.finalproject.dto;

import java.util.UUID;

import org.jenjetsu.com.finalproject.model.User;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(
    description = "DTO for getting information about user",
    type = "object",
    requiredProperties = {"user_id", "username", "email"}
)
public record UserReturnDTO(
    @JsonProperty(value = "user_id", required = true) 
    @Schema(type = "string", format = "uuid", example = "ef69b987-7669-4edf-a582-ca38b7f71e05")
    UUID userId, 
    @Schema(type = "string", example="stey")
    String username, 
    @Schema(type = "string", example="some@mail.sru")
    String email, 
    @Schema(type = "string", example="Stanislav")
    String firstname, 
    @Schema(type = "string", example="Tsvetkov")
    String lastname,
    @Schema(type = "boolean", example="fasle") 
    boolean blocked) {
    
    public static UserReturnDTO convert(User user) {
        return UserReturnDTO.builder()
                            .userId(user.getUserId())
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .firstname(user.getFirstname())
                            .lastname(user.getLastname())
                            .blocked(user.getBlocked())
                            .build();
    }

    public static User convertToUser(UserReturnDTO dto) {
        return User.builder()
                   .userId(dto.userId)
                   .username(dto.username)
                   .email(dto.email)
                   .firstname(dto.firstname)
                   .lastname(dto.lastname)
                   .blocked(dto.blocked)
                   .build();
    }
}
