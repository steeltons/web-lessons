package org.jenjetsu.com.finalproject.dto;

import java.text.SimpleDateFormat;
import java.time.Instant;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;

import lombok.SneakyThrows;

public class DTOInstantDeserializer implements Converter<String, Instant> {
    
    private final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

    @Override
    @SneakyThrows
    public Instant convert(String text) {
        return format.parse(text).toInstant();
    }

    @Override
    public JavaType getInputType(TypeFactory typeFactory) {
        return typeFactory.constructType(String.class);
    }

    @Override
    public JavaType getOutputType(TypeFactory typeFactory) {
        return typeFactory.constructType(Instant.class);
    }

    
}
