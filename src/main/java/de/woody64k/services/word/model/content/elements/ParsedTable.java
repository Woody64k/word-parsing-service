package de.woody64k.services.word.model.content.elements;

import java.util.ArrayList;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ParsedTable extends ArrayList<ParsedTableRow> {
    public ParsedTableRow newRow() {
        ParsedTableRow row = new ParsedTableRow();
        add(row);
        return row;
    }
}
