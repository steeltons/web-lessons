package org.jenjetsu.com.todo.model;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "t_activity_status")
public class ActivityStatus {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer activityStatusId;
    @Column(name = "status", nullable = false, unique = true, length = 64)
    private String status;
}
