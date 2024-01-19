package org.jenjetsu.com.finalproject.controller;

import java.util.UUID;

import org.jenjetsu.com.finalproject.model.Subtask;
import org.jenjetsu.com.finalproject.service.SubtaskService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/subtasks")
public class SubtaskController extends CRUDController<Subtask, UUID>{
    
    private final SubtaskService subtaskService;

    public SubtaskController(SubtaskService subtaskService) {
        super(subtaskService);
        this.subtaskService = subtaskService;
    }
}
