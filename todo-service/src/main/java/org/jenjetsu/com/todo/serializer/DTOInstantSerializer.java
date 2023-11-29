package org.jenjetsu.com.todo.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;

public class DTOInstantSerializer extends JsonSerializer<Instant> {

    private final DateFormat format = new SimpleDateFormat("yyyy MMM dd HH:mm:ss");
    @Override
    public void serialize(Instant value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if(value == null) {
            gen.writeNull();
        } else {
            gen.writeString(format.format(value.getEpochSecond()));
        }
    }
}
