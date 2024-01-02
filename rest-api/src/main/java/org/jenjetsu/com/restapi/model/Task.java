package org.jenjetsu.com.restapi.model;

import java.util.UUID;

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
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "t_task")
public class Task {

    public static final String STANDARD_STATUS = "CREATED";
    public static final String STANDARD_DESCRIPTION = "";
    
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID taskId;
    @Column(name = "title", nullable = false, length = 512)
    private String title;
    @Column(name = "description", nullable = false, length = 2048)
    @Builder.Default
    private String description = "";
    @Builder.Default
    @Column(name = "status", nullable = true, length = 64)
    private String status = "CREATED";
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void putTask(Task newTask) {
        this.title = newTask.title;
        this.description = newTask.description;
        this.status = newTask.status;
    }
}
