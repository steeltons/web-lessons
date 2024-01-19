package org.jenjetsu.com.finalproject.model;

import org.hibernate.annotations.Check;

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
@Entity(name = "task_dependency")
@Check(constraints = "task_id != required_task_id")
public class TaskDependency {
    
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long taskDependencyId;
    
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "task_id")
    private Task task;
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "required_task_id")
    private Task requiredTask;
}
