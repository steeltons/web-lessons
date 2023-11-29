package org.jenjetsu.com.todo.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;

import static jakarta.persistence.CascadeType.*;

@Getter @Setter @ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "t_task_activity")
public class TaskActivity {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID DEFAULT gen_random_uuid()")
    private UUID taskActivityId;
    @Column(name = "title", length = 512, nullable = false)
    private String title;
    @Column(name = "description", length = 2048)
    private String description;
    @ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = {MERGE, DETACH})
    @JoinColumn(name = "user_id", columnDefinition = "UUID REFERENCES t_user_task(user_id)")
    private User user;
    @ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = {MERGE, DETACH})
    @JoinColumn(name = "task_id", columnDefinition = "UUID REFERENCES t_user_task(task_id)")
    private Task task;
    @ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = {MERGE, DETACH})
    @JoinColumn(name = "activity_status_id", nullable = false)
    private ActivityStatus activityStatus;
    @Column(name = "deleted", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean deleted;

    @ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = {MERGE, DETACH})
    @JoinColumn(name = "creator_id", nullable = false)
    private User createdBy;
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;
//    private User updatedBy;

}
