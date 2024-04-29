package de.woody64k.services.word.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ContentTableRow {
    List<String> cells = new ArrayList<>();

    public void add(String value) {
        cells.add(value);
    }

    public String get(int index) {
        return cells.get(index);
    }
}
