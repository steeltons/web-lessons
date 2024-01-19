package org.jenjetsu.com.finalproject.controller;

import org.jenjetsu.com.finalproject.model.SubtaskStatus;
import org.jenjetsu.com.finalproject.service.CRUDService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/subtask-statuses")
public class SubtaskStatusController extends CRUDController<SubtaskStatus, Integer>{
    
    public SubtaskStatusController(CRUDService<SubtaskStatus, Integer> service) {
        super(service);
    }
}
