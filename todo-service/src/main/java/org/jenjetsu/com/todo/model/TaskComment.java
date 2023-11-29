package org.jenjetsu.com.todo.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

import static jakarta.persistence.CascadeType.*;

@Getter @Setter @ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "t_task_comment")
public class TaskComment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskCommentId;
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = {MERGE, DETACH})
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;
    @ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = {MERGE, DETACH})
    @JoinColumn(name = "uploader_id", nullable = false)
    private User uploadedBy;
    @Column(name = "uploaded_at", nullable = false)
    private Timestamp uploadedAt;
}
