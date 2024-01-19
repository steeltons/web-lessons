package org.jenjetsu.com.finalproject.controller;

import org.jenjetsu.com.finalproject.model.SubtaskStatus;
import org.jenjetsu.com.finalproject.service.CRUDService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping("/api/v1/subtask-statuses")
public class SubtaskStatusController extends CRUDController<SubtaskStatus, Integer>{

    public SubtaskStatusController(CRUDService<SubtaskStatus, Integer> service) {
        super(service);
        super.setModelSerializer((status) -> {
            ObjectNode node = new ObjectMapper().createObjectNode();
            node.put("subtask_status_id", status.getSubtaskStatusId());
            node.put("name", status.getName());
            return node;
        });
        super.setModelDeserializer((node) -> {
                return SubtaskStatus.builder()
                                    .name(node.get("name").asText())
                                    .build();
            }
        );
    }
}
