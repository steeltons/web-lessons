package org.jenjetsu.com.finalproject.model;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
@Entity(name = "t_user")
public class User implements Model<UUID> {
    
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID DEFAULT gen_random_uuid()")
    private UUID userId;
    @Column(name = "username", unique = true, nullable = false)
    private String username;
    @Column(name = "email", unique = true, nullable = false)
    private String email;
    @Column(name = "firstname", nullable = true, length = 50)
    private String firstname;
    @Column(name = "lastname", nullable = true, length = 50)
    private String lastname;
    @Column(name = "blocked", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean blocked;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "t_user_project",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "project_id")
    )
    private List<Project> projectList;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Subtask> subtaskList;

    @Override
    public String getModelName() {
        return "user";
    }

    @Override
    public UUID getModelId() {
        return this.getUserId();
    }

    @Override
    public User patchModel(Model another) {
        if (!User.class.isAssignableFrom(another.getClass())) {
            throw new RuntimeException("");
        }
        User anotherUser = (User) another;
        return null;
    }


}
