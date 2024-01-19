package org.jenjetsu.com.finalproject.controller;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.jenjetsu.com.finalproject.model.Model;
import org.jenjetsu.com.finalproject.service.CRUDService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public abstract class CRUDController<T extends Model<ID>, ID extends Serializable> {

    private final CRUDService<T, ID> service;
    @Setter
    private Converter<T, ObjectNode> modelSerializer;
    @Setter
    private Converter<ObjectNode, T> modelDeserializer;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Object getById(@PathVariable("id") ID id) {
        T entity = this.service.readById(id);
        return modelSerializer.convert(entity);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Map<String, List<ObjectNode>> getAll() {
        List<ObjectNode> dtos = this.service.readAll()
                                .stream()
                                .map(modelSerializer::convert)
                                .toList();
        return Map.of("list", dtos);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> createEntity(@RequestBody ObjectNode dto) {
        T raw = this.modelDeserializer.convert(dto);
        raw = this.service.create(raw);
        return Map.of(raw.getModelName()+"_id", raw.getModelId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEntityById(@PathVariable("id") ID id) {
        this.service.deleteById(id);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pathcEntity(@RequestBody ObjectNode dto) {
        T modelToPatch = this.modelDeserializer.convert(dto);
        T existModel = this.service.readById(modelToPatch.getModelId());
        T mergedModel = (T) existModel.patchModel(modelToPatch);
        this.service.update(mergedModel);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void putEntity(@RequestBody ObjectNode dto) {
        T model = this.modelDeserializer.convert(dto);
        this.service.update(model);
    }
}
