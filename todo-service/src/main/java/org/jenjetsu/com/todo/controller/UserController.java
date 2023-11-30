package org.jenjetsu.com.todo.controller;

import java.util.List;
import java.util.Map;

import org.jenjetsu.com.todo.dto.TaskReturnDTO;
import org.jenjetsu.com.todo.dto.UserCreateDTO;
import org.jenjetsu.com.todo.dto.UserReturnDTO;
import org.jenjetsu.com.todo.model.Task;
import org.jenjetsu.com.todo.model.User;
import org.jenjetsu.com.todo.security.JwtUserIdAuthenticationToken;
import org.jenjetsu.com.todo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public UserReturnDTO getUserById(JwtUserIdAuthenticationToken token) {
        User user = this.userService.readById(token.getUserId());
        return UserReturnDTO.from(user);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('MODERATOR')")
    public Map<String, ?> createUser(@RequestBody UserCreateDTO dto) {
        User user = User.builder()
                        .username(dto.username())
                        .email(dto.email())
                        .firstname(dto.firstname())
                        .lastname(dto.lastname())
                        .phoneNumber(dto.phoneNumber())
                        .blocked(false)
                        .build();
        user = this.userService.create(user);
        Map<String, ?> responseBody = Map.of("user_id", user.getUserId());
        return responseBody;
    }

    @GetMapping("/todo")
    @SneakyThrows
    @ResponseStatus(HttpStatus.OK)
    public Map<String, ?> getUserTasks(JwtUserIdAuthenticationToken token) {
        List<Task> taskList = this.userService.readAllUserTasksWithUserActivities(token.getUserId());
        List<TaskReturnDTO> dtoList = taskList.stream()
                .map(TaskReturnDTO::from)
                .toList();
        Map<String, ?> responseBody = Map.of("tasks", dtoList);
        return responseBody;
    }

}
