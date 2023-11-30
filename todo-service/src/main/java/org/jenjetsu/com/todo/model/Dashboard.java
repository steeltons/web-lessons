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
@Entity(name = "t_dashboard")
@Builder
public class Dashboard {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "dashboard_id", columnDefinition = "UUID DEFAULT gen_random_uuid()")
    private UUID dashboardId;
    @Column(name = "name", length = 64, columnDefinition = "VARCHAR(64) DEFAULT ''")
    private String name;
    @Column(name = "is_hidden", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isHidden;
    @Column(name = "is_deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isDeleted;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "creator_id")
    private User createdBy;
    // TODO единое время
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @ManyToMany(fetch = FetchType.LAZY, cascade = ALL)
    @JoinTable(
        name = "t_user_dashboard",
        joinColumns = @JoinColumn(name = "dashboard_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> userList;
    @OneToMany(fetch = FetchType.LAZY, cascade = {MERGE, DETACH})
    @JoinColumn(name = "dashboard_id")
    private List<Task> taskList;
}
