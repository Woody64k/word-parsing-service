package de.woody64k.services.word.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.Data;

@Data
public class WordContent {
    private List<ContentTable> tables = new ArrayList<>();

    public void addTable(ContentTable table) {
        tables.add(table);
    }

    public void addTables(Collection<ContentTable> table) {
        tables.addAll(table);
    }
}
