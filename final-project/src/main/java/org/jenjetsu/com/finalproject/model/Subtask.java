package org.jenjetsu.com.finalproject.model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.Check;
import org.hibernate.annotations.Checks;

import static jakarta.persistence.CascadeType.DETACH;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
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

    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = {})
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = {})
    @JoinColumn(name = "creator_id")
    private User creator;
    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = {})
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;
    @OneToMany(mappedBy = "subtask", fetch = FetchType.EAGER, cascade = {PERSIST, DETACH, REMOVE})
    @Builder.Default
    private List<SubtaskStatusDate> subtaskStatusList = new ArrayList<>();

    public Subtask(UUID subtaskId, Date startDate, Date endDate, UUID userId) {
        this.subtaskId = subtaskId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.user = User.builder().userId(userId).build();
    }

    @Override
    public String getModelName() {
        return "subtask";
    }

    @Override
    public UUID getModelId() {
        return this.getSubtaskId();
    }

    public void merge(Model anoth) {
        if (!this.getClass().isAssignableFrom(anoth.getClass())) {
            throw new IllegalArgumentException("Merging object is not Subtask");
        }
        Subtask another = (Subtask) anoth;
        this.title = checkSimilarity(this.title, another.title) ? this.title : another.title;
        this.description = checkSimilarity(this.description, another.description) ? this.description : another.description;
        this.startDate = checkSimilarity(this.startDate, another.startDate) ? this.startDate : another.startDate;
        this.endDate = checkSimilarity(this.endDate, another.endDate) ? this.endDate : another.endDate;
    }

    private boolean checkSimilarity(Object myVal, Object anotherVal) {
        return anotherVal == null && anotherVal.equals(myVal);
    }
}
