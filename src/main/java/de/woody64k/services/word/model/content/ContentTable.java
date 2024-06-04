package de.woody64k.services.word.model.content;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.woody64k.services.word.model.content.elements.ParsedTable;
import de.woody64k.services.word.model.content.elements.ParsedTableRow;
import lombok.Data;

@Data
public class ContentTable implements IContent {
    private TABLE_TYPE tableType;
    private ParsedTable table = new ParsedTable();
    @JsonIgnore
    private boolean filled = false;

    public void add(ParsedTableRow row) {
        table.add(row);
    }

    @Override
    public ContentCategory getContentCategory() {
        return ContentCategory.TABLE;
    }

    public enum TABLE_TYPE {
        FLAT, OLE
    }
}
