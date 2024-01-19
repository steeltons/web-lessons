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
public class Project {
    
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID DEFAULT gen_random_uuid()")
    private UUID projectId;
    @Column(name = "name", length = 64, nullable = true)
    private String name;
    @Column(name = "deleted", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean deleted;
    @Column(name = "start_date", columnDefinition = "DATE DEFAULT now()")
    private Date startDate;
    @Column(name = "end_date", columnDefinition = "DATE DEFAULT now()")
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
    private List<User> userList;
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<Task> taskList;
}
