package de.woody64k.services.word.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Table extends AContent {
    private List<TableRow> objects = new ArrayList<>();

    public void addRow(TableRow row) {
        objects.add(row);
    }
}
