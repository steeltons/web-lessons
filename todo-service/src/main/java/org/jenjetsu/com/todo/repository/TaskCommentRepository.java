package org.jenjetsu.com.todo.repository;

import java.util.UUID;

import org.jenjetsu.com.todo.model.TaskComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskCommentRepository extends JpaRepository<TaskComment, UUID> {
}
