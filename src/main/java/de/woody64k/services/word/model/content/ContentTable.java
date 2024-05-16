package de.woody64k.services.word.model.content;

import java.util.ArrayList;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ContentTable extends ArrayList<ContentTableRow> {
    private TABLE_TYPE type;
    boolean filled = false;

    public ContentTableRow newRow() {
        ContentTableRow row = new ContentTableRow();
        add(row);
        return row;
    }

    public void addRow(ContentTableRow row) {
        add(row);
    }

    public enum TABLE_TYPE {
        FLAT, OLE
    }
}
