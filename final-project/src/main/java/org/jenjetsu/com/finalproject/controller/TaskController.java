package org.jenjetsu.com.finalproject.controller;

import java.util.UUID;

import org.jenjetsu.com.finalproject.deserializer.TaskDeserializer;
import org.jenjetsu.com.finalproject.model.Task;
import org.jenjetsu.com.finalproject.serializer.TaskSerializer;
import org.jenjetsu.com.finalproject.service.TaskService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController extends CRUDController<Task, UUID> {
    
    private final TaskService taskService;

    public TaskController(TaskService taskService,
                          TaskSerializer serializer,
                          TaskDeserializer deserializer) {
        super(taskService);
        this.taskService = taskService;
        super.setModelDeserializer(deserializer);
        super.setModelSerializer(serializer);
    }
}
