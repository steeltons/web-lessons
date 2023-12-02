package org.jenjetsu.com.todo.controller;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;

import org.jenjetsu.com.todo.dto.CommentCreateDTO;
import org.jenjetsu.com.todo.dto.TaskCommentaryReturnDTO;
import org.jenjetsu.com.todo.model.Task;
import org.jenjetsu.com.todo.model.TaskComment;
import org.jenjetsu.com.todo.model.User;
import org.jenjetsu.com.todo.security.JwtUserIdAuthenticationToken;
import org.jenjetsu.com.todo.service.CRUDService;
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
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class TaskCommentController {
    
    private final CRUDService<TaskComment, Long> commentService;

    @GetMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public TaskCommentaryReturnDTO getCommentById(@PathVariable("commentId") Long commentId) {
        return TaskCommentaryReturnDTO.from(this.commentService.readById(commentId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Long> createComment(@RequestBody CommentCreateDTO createDto,
                                           JwtUserIdAuthenticationToken token) {
        TaskComment rawComment = TaskComment.builder()
            .content(createDto.content())
            .task(Task.builder().taskId(createDto.taskId()).build())
            .uploadedBy(User.builder().userId(token.getUserId()).build())
            .uploadedAt(Timestamp.from(Instant.now()))
            .build();
        rawComment = this.commentService.create(rawComment);
        return Map.of("comment_id", rawComment.getTaskCommentId());
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> deleteComment(@PathVariable("commentId") Long commentId) {
        this.commentService.deleteById(commentId);
        return Map.of("message", String.format("Comment %d was deleted", commentId));
    }

}
