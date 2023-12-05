package org.jenjetsu.com.todo.configuration;

import org.jenjetsu.com.todo.model.ActivityStatus;
import org.jenjetsu.com.todo.model.TaskComment;
import org.jenjetsu.com.todo.model.TaskStatus;
import org.jenjetsu.com.todo.service.CRUDService;
import org.jenjetsu.com.todo.service.implementation.SimpleJpaService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;

@Configuration
public class SimpleJpaServiceBeanConfiguration {
    
    @Bean
    public CRUDService<TaskComment, Long> taskCommentServiceImpl(JpaRepository<TaskComment, Long> rep) {
        return new SimpleJpaService<>(TaskComment.class, rep);
    }

    @Bean
    public CRUDService<TaskStatus, Integer> taskStatusServiceImpl(JpaRepository<TaskStatus, Integer> rep) {
        return new SimpleJpaService<>(TaskStatus.class, rep);
    }

    @Bean
    public CRUDService<ActivityStatus, Integer> activityStatusServiceImpl(JpaRepository<ActivityStatus, Integer> rep) {
        return new SimpleJpaService<>(ActivityStatus.class, rep);
    }
}
