package de.woody64k.services.word.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ContentTableRow {
    List<String> values = new ArrayList<>();

    public void add(String value) {
        values.add(value);
    }

    public String get(int index) {
        return values.get(index);
    }
}
