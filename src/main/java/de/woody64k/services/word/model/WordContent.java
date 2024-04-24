package de.woody64k.services.word.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class WordContent {
    private List<Table> tables = new ArrayList<>();

    public void addTable(Table table) {
        tables.add(table);
    }
}
