package de.woody64k.services.word.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ContentTable extends AContent {
    private List<ContentTableRow> objects = new ArrayList<>();

    public ContentTableRow newRow() {
        ContentTableRow row = new ContentTableRow();
        objects.add(row);
        return row;
    }

    public void addRow(ContentTableRow row) {
        objects.add(row);
    }
}
