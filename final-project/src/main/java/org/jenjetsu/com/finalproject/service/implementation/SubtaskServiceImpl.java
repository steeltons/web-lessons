package org.jenjetsu.com.finalproject.service.implementation;

import java.util.UUID;

import org.jenjetsu.com.finalproject.model.Subtask;
import org.jenjetsu.com.finalproject.repository.SubtaskRepository;
import org.jenjetsu.com.finalproject.service.SubtaskService;
import org.springframework.stereotype.Service;

@Service
public class SubtaskServiceImpl extends SimpleJpaService<Subtask, UUID>
                                implements SubtaskService {
    
    private final SubtaskRepository subtaskRep;

    public SubtaskServiceImpl(SubtaskRepository subtaskRep) {
        super(Subtask.class, subtaskRep);
        this.subtaskRep = subtaskRep;
    }

    @Override
    protected Subtask createEntity(Subtask raw) {
        raw.setSubtaskId(null);
        raw.setDeleted(false);
        return raw;
    }
}
