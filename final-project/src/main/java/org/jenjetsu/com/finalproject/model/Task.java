package org.jenjetsu.com.finalproject.model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.Check;
import org.hibernate.annotations.Checks;

import jakarta.persistence.CascadeType;
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
@Entity(name = "t_task")
@Checks(
    value = @Check(constraints = "start_date < end_date")
)
public class Task implements Model<UUID>{
    
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID DEFAULT gen_random_uuid()")
    private UUID taskId;
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
    @JoinColumn(name = "creator_id")
    private User creator;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;
    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<Subtask> subtaskList = new ArrayList<>();
    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<TaskDependency> taskDependencyList = new ArrayList<>();

    public Task(UUID taskId, Date startDate, Date endDate) {
          this.taskId = taskId;
          this.startDate = startDate;
          this.endDate = endDate;
    }
    @Override
    public String getModelName() {
        return "task";
    }

    @Override
    public UUID getModelId() {
        return this.taskId;
    }

    @Override
    public Task patchModel(Model anoth) {
        if (!this.getClass().isAssignableFrom(anoth.getClass())) {
            throw new IllegalArgumentException("Another clas is not Task");
        }
        Task another = (Task) anoth;
        return Task.builder()
                   .taskId(this.taskId)
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
                   .project(checkSimilarity(this.getProject().getProjectId(), another.getProject().getProjectId())
                        ? this.getProject() : another.getProject())
                   .build();
    }

    private boolean checkSimilarity(Object myVal, Object anotherVal) {
        return anotherVal != null && anotherVal.equals(myVal);
    }
}
