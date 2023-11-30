package org.jenjetsu.com.todo.model;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.CascadeType.DETACH;
import static jakarta.persistence.CascadeType.MERGE;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "t_task")
@Builder
public class Task {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID DEFAULT gen_random_uuid()")
    private UUID taskId;
    @Column(name = "title", nullable = false, length = 512)
    private String title;
    @Column(name = "description", length = 2048)
    private String description;
    @ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = {MERGE, DETACH})
    @JoinColumn(name = "task_status_id", nullable = false)
    private TaskStatus status;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "t_user_dashboard_task",
            joinColumns = @JoinColumn(name = "task_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "user_id", nullable = false)
    )
    private List<User> userList;
    @Column(name = "deleted", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean deleted;

    @ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = {MERGE, DETACH})
    @JoinColumn(name = "creator_id" ,nullable = false)
    private User createdBy;
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;
    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = {MERGE, DETACH})
    @JoinColumn(name = "dashboard_id", columnDefinition = "UUID REFERENCES t_user_dashboard_task(dashboard_id)")
    private Dashboard dashboard;

    @OneToMany(fetch = FetchType.LAZY, cascade = ALL)
    @JoinColumn(name = "task_id")
    private List<TaskActivity> activityList;
    @OneToMany(fetch = FetchType.LAZY, cascade = ALL)
    @JoinColumn(name = "task_id")
    private List<TaskComment> commentList;
}
