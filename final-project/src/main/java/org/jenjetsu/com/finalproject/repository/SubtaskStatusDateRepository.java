package org.jenjetsu.com.finalproject.repository;

import org.jenjetsu.com.finalproject.model.SubtaskStatusDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubtaskStatusDateRepository extends JpaRepository<SubtaskStatusDate, Long> {
    
}
