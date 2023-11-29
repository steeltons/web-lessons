package org.jenjetsu.com.todo.serializer;

import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.core.convert.converter.Converter;

public class JSONObjectResponseConverter implements Converter<JSONObject, String> {


    @Override
    public String convert(JSONObject source) {
        return source.toString();
    }
}
