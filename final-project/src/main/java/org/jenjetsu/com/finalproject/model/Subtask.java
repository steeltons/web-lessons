package org.jenjetsu.com.finalproject.model;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.Check;
import org.hibernate.annotations.Checks;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Builder
@Entity(name = "t_subtask")
@Checks(
    value = @Check(constraints = "start_date < end_date")
)
public class Subtask implements Model<UUID>{
    
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID DEFAULT gen_random_uuid()")
    private UUID subtaskId;
    @Column(name = "title", nullable = false, length = 512)
    private String title;
    @Column(name = "description", length = 2048)
    private String description;
    @Column(name = "start_date", nullable = false)
    private Date startDate;
    @Column(name = "end_date", nullable = false)
    private Date endDate;
    @Column(name = "deleted", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean deleted;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User creator;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;
    @OneToMany(mappedBy = "subtask", fetch = FetchType.LAZY)
    private List<SubtaskStatusDate> subtaskStatusList;

    @Override
    public String getModelName() {
        return "subtask";
    }

    @Override
    public UUID getModelId() {
        return this.getSubtaskId();
    }

    @Override
    public Subtask patchModel(Model anoth) {
        if (!this.getClass().isAssignableFrom(anoth.getClass())) {
            throw new IllegalArgumentException("Another is not same to Subtask");
        }
        Subtask another = (Subtask) anoth;
        return Subtask.builder()
                        .subtaskId(this.subtaskId)
                        .title(checkSimilarity(this.title, another.title)
                            ? this.title : another.title)
                        .description(checkSimilarity(this.description, another.description)
                            ? this.description : another.description)
                        .startDate(checkSimilarity(this.startDate, another.startDate)
                            ? this.startDate : another.startDate)
                        .endDate(checkSimilarity(this.endDate, another.endDate)
                            ? this.endDate : another.endDate)
                        .deleted(checkSimilarity(this.deleted, another.deleted)
                            ? this.deleted : another.deleted)
                        .creator(checkSimilarity(this.creator.getUserId(), another.getCreator().getUserId())
                            ? this.getCreator() : another.getCreator())
                        .user(checkSimilarity(this.getUser().getUserId(), another.getUser().getUserId())
                            ? this.getUser() : another.getUser())
                        .task(checkSimilarity(this.getTask().getTaskId(), another.getTask().getTaskId())
                            ? this.getTask() : another.getTask())
                      .build();
    }

    private boolean checkSimilarity(Object myVal, Object anotherVal) {
        return anotherVal != null && anotherVal.equals(myVal);
    }
}
