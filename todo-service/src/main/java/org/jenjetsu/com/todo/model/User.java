package org.jenjetsu.com.todo.model;


import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "t_user")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID DEFAULT gen_random_uuid()")
    private UUID userId;
    @Column(name = "username", unique = true, nullable = false)
    private String username;
    @Column(name = "email", unique = true, nullable = false)
    private String email;
    @Column(name = "firstname", nullable = true, length = 50)
    private String firstname;
    @Column(name = "lastname", nullable = true, length = 50)
    private String lastname;
    @Column(name = "phone_number")
    @Min(70000000000l) @Max(89999999999l)
    private Long phoneNumber;
    @Column(name = "blocked", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean blocked;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "t_user_dashboard_task",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "task_id")
    )
    private List<Task> taskList;
    @ManyToMany
    @JoinTable(
        name = "t_user_dashboard",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "dashboard_id")
    )
    private List<Dashboard> dashboardList;
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private List<TaskActivity> activityList;
}
