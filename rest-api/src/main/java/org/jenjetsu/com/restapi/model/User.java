package org.jenjetsu.com.restapi.model;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
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
public class User {
    
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;
    @Column(name = "username", length = 36, unique = true, nullable = false)
    private String username;
    @Column(name = "email", length = 64, unique = true, nullable = false)
    @Email
    private String email;
    @OneToMany(mappedBy= "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Task> taskList;
}
