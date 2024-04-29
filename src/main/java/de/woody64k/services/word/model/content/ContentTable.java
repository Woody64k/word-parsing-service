package de.woody64k.services.word.model.content;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ContentTable extends AContent {
    private TABLE_TYPE type;
    private List<ContentTableRow> rows = new ArrayList<>();

    public ContentTableRow newRow() {
        ContentTableRow row = new ContentTableRow();
        rows.add(row);
        return row;
    }

    public void addRow(ContentTableRow row) {
        rows.add(row);
    }

    public enum TABLE_TYPE {
        FLAT, OLE
    }
}
