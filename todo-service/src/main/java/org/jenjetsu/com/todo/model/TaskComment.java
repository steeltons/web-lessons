package org.jenjetsu.com.todo.model;

import java.sql.Timestamp;

import static jakarta.persistence.CascadeType.DETACH;
import static jakarta.persistence.CascadeType.MERGE;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "t_task_comment")
@Builder
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
