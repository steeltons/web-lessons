package org.jenjetsu.com.todo.repository;

import org.jenjetsu.com.todo.model.TaskComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaskCommentRepository extends JpaRepository<TaskComment, UUID> {
}
