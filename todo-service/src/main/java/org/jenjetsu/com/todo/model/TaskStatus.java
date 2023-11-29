package org.jenjetsu.com.todo.model;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "t_task_status")
public class TaskStatus {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer taskStatusId;
    @Column(name = "status", length = 64, nullable = false, unique = true)
    private String status;
}
