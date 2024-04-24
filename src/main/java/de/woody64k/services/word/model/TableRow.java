package de.woody64k.services.word.model;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Data;

@Data
public class TableRow {
    Map<String, String> values = new LinkedHashMap<>();

    public void add(String key, String value) {
        values.put(key, value);
    }

    public String get(String key) {
        return values.get(key);
    }

    public String get(int index) {
        return (String) values.values().toArray()[index];
    }
}
