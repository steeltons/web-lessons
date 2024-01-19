package org.jenjetsu.com.finalproject.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.UUID;

import org.jenjetsu.com.finalproject.model.Subtask;
import org.jenjetsu.com.finalproject.model.SubtaskStatus;
import org.jenjetsu.com.finalproject.model.SubtaskStatusDate;
import org.jenjetsu.com.finalproject.service.CRUDService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping("/api/v1/subtask-status-dates")
public class SubtaskStatusDateController extends CRUDController<SubtaskStatusDate, Long> {
    
    private final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public SubtaskStatusDateController(CRUDService<SubtaskStatusDate, Long> service) {
        super(service);
        super.setModelSerializer((statusDate) -> {
            ObjectNode node = new ObjectMapper().createObjectNode();
            node.put("substatus_id", statusDate.getSubtask().getSubtaskId().toString());
            node.put("subtask_status", statusDate.getSubtaskStatus().getSubtaskStatusId());
            node.put("date", dateFormat.format(statusDate.getDate()));
            return node;
        });
        super.setModelDeserializer((node) -> {
            UUID subtaskId = UUID.fromString(node.get("subtask_id").asText());
            Integer subtaskStatusId = node.get("subtask_status_id").asInt();
            return SubtaskStatusDate.builder()
                                    .subtask(Subtask.builder().subtaskId(subtaskId).build())
                                    .subtaskStatus(SubtaskStatus.builder().subtaskStatusId(subtaskStatusId).build())
                                    .build();
        });     
    }
}
