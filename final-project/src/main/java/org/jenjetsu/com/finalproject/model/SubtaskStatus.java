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

    @Override
    public SubtaskStatus patchModel(Model anoth) {
        if (!this.getClass().isAssignableFrom(anoth.getClass())) {
            throw new IllegalArgumentException("Another is not same to SubtaskStatus");
        }
        SubtaskStatus another = (SubtaskStatus) anoth;
        return SubtaskStatus.builder()
                            .subtaskStatusId(this.subtaskStatusId)
                            .name(checkSimilarity(this.name, another.name) 
                                ? this.name : another.name)
                            .build();
    }

    private boolean checkSimilarity(Object myVal, Object anotherVal) {
        return anotherVal != null && anotherVal.equals(myVal);
    }
}
