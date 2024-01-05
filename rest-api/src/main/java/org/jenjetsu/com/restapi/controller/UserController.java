package org.jenjetsu.com.restapi.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jenjetsu.com.restapi.dto.TaskReturnDTO;
import org.jenjetsu.com.restapi.dto.UserCreateDTO;
import org.jenjetsu.com.restapi.dto.UserReturnDTO;
import org.jenjetsu.com.restapi.model.User;
import org.jenjetsu.com.restapi.service.TaskService;
import org.jenjetsu.com.restapi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final TaskService taskService;

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserReturnDTO getUserById(@PathVariable("userId") UUID userId) {
        User user = this.userService.readByIdFetchTasks(userId);
        return UserReturnDTO.from(user);
    }

    @GetMapping("/{userId}/todo")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, List<TaskReturnDTO>> getUserTasks(@PathVariable("userId") UUID userId) {
        List<TaskReturnDTO> tasks = taskService.readAllByUserId(userId)
                                               .stream()
                                               .map(TaskReturnDTO::from)
                                               .toList();
        return Map.of("tasks", tasks);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    // @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public Map<String, String> createUser(@RequestBody UserCreateDTO createDto) {
        User raw = UserCreateDTO.from(createDto);
        raw = userService.create(raw);
        return Map.of("user_id", raw.getUserId().toString());
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    // @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public void deleteUser(@PathVariable("userId") UUID userId) {
        userService.deleteById(userId);
    }
}
