package org.jenjetsu.com.finalproject.model;

import java.sql.Date;
import java.util.ArrayList;
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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.UniqueConstraint;
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
@Entity(name = "t_project")
@Checks(
    value = @Check(constraints = "start_date < end_date")
)
public class Project implements Model<UUID>{
    
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID DEFAULT gen_random_uuid()")
    private UUID projectId;
    @Column(name = "name", length = 64, nullable = true)
    private String name;
    @Column(name = "deleted", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean deleted;
    @Column(name = "start_date", columnDefinition = "DATE DEFAULT now()")
    private Date startDate;
    @Column(name = "end_date", nullable = false)
    private Date endDate;   
    
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User createdBy;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "t_user_project",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "project_id"}),
        joinColumns = @JoinColumn(name = "project_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private List<User> userList = new ArrayList<>();
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Task> taskList = new ArrayList<>();

    @Override
    public String getModelName() {
        return "project";
    }

    @Override
    public UUID getModelId() {
        return this.getProjectId();
    }

    public void merge(Model anoth) {
        if (!this.getClass().isAssignableFrom(anoth.getClass())) {
            throw new IllegalArgumentException("Merging object is not Project");
        }
        Project another = (Project) anoth;
        this.name = checkSimilarity(this.name, another.name) ? this.name : another.name;
        this.startDate = checkSimilarity(this.startDate, another.startDate) ? this.startDate : another.startDate;
        this.endDate = checkSimilarity(this.endDate, another.endDate) ? this.endDate : another.endDate;
    }

    private boolean checkSimilarity(Object myVal, Object anotherVal) {
        return anotherVal != null && anotherVal.equals(myVal);
    }
}
