package org.jenjetsu.com.finalproject.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Entity(name = "t_subtask_status")
public class SubtaskStatus implements Model<Integer>{
    
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer subtaskStatusId;
    @Column(name = "name", length = 63, nullable = false, unique = true)
    private String name;

    @Override
    public Integer getModelId() {
        return this.getSubtaskStatusId();
    }

    @Override
    public String getModelName() {
        return "subtask_status";
    }

    public void merge(Model anoth) {
        if (!this.getClass().isAssignableFrom(anoth.getClass())) {
            throw new IllegalArgumentException("Merging object is not SubtaskStatus");
        }
        SubtaskStatus another = (SubtaskStatus) anoth;
        this.name = another.name == null || another.name.equals(this.name) 
                        ? this.name : another.name;
    }
}
