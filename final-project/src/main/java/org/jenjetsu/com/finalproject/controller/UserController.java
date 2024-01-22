package org.jenjetsu.com.finalproject.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jenjetsu.com.finalproject.dto.UserCreateDTO;
import org.jenjetsu.com.finalproject.dto.UserReturnDTO;
import org.jenjetsu.com.finalproject.model.User;
import org.jenjetsu.com.finalproject.security.model.BearerTokenAuthentication;
import org.jenjetsu.com.finalproject.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Controller with user endpoints")
@SecurityRequirement(name = "JWT")
public class UserController {
    
    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public UserReturnDTO getUserById(BearerTokenAuthentication token) {
        User user = this.userService.readById(token.getUserId());
        return UserReturnDTO.convert(user);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public Map<String, List<UserReturnDTO>> getAllUsers() {
        List<UserReturnDTO> dtos = this.userService
                                       .readAll()
                                       .stream()
                                       .map(UserReturnDTO::convert)
                                       .toList();
        return Map.of("user_list", dtos);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public Map<String, String> createUser(@RequestBody UserCreateDTO dto) {
        User raw = UserCreateDTO.convert(dto);
        raw.setBlocked(false);
        raw = this.userService.create(raw);
        return Map.of("user_id", raw.getUserId().toString());
    } 

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void putUser(@RequestBody UserReturnDTO dto) {
        User newUser = UserReturnDTO.convertToUser(dto);
        this.userService.update(newUser);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pathcUser(@RequestBody UserReturnDTO dto) {
        User mergingUser = UserReturnDTO.convertToUser(dto);
        User mergableUser = this.userService.readById(mergingUser.getUserId());
        mergableUser.merge(mergingUser);
        this.userService.update(mergableUser);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public void deletUser(@PathVariable("userId") UUID userId) {
        this.userService.deleteById(userId);
    }

}
