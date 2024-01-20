package org.jenjetsu.com.finalproject.service;

import java.util.List;
import java.util.UUID;

import org.jenjetsu.com.finalproject.model.Subtask;

public interface SubtaskService extends CRUDService<Subtask, UUID>  {
    
    public List<Subtask> readAllUserSubtasksByParams(UUID userId, UUID projectId, String statusName);
}
