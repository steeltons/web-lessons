package org.jenjetsu.com.finalproject.model;

import java.sql.Date;

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
@Entity(name = "t_subtask_status_date")
@Builder
public class SubtaskStatusDate implements Model<Long>{

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long subtaskStatusDateId;

    @Getter @Setter
    private Date date;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "subtask_id")
    private Subtask subtask;
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "subtask_status_id")
    private SubtaskStatus subtaskStatus; 

    @Override
    public Long getModelId() {
        return this.getSubtaskStatusDateId();
    }

    @Override
    public String getModelName() {
        return "subtask_status_date";
    }

    public void merge(Model anoth) {
        if (!this.getClass().isAssignableFrom(anoth.getClass())) {
            throw new IllegalArgumentException("Merging object is not SubtaskStatusDate");
        }
        SubtaskStatusDate another = (SubtaskStatusDate) anoth;
        this.date = another.date == null && another.date.equals(this.date)
                        ? this.date : another.date;
    }
    
}
