package de.woody64k.services.word.model.content;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public List<IContent> getAllByCathegory(ContentCategory category) {
        List<IContent> collection = new ArrayList<>();
        for (ParsedTableRow row : table) {
            for (Object cell : row) {
                if (cell instanceof WordContent) {
                    WordContent embeddedDoc = (WordContent) cell;
                    collection.addAll(embeddedDoc.getAllByCathegory(category));
                }
            }
        }
        return collection;
    }

    public enum TABLE_TYPE {
        FLAT, OLE
    }

    @Override
    public String flattenToString() {
        List<String> collection = new ArrayList<>();
        for (ParsedTableRow row : table) {
            for (Object cell : row) {
                if (cell instanceof IContent) {
                    collection.add(((IContent) cell).flattenToString());
                } else {
                    collection.add((String) cell);
                }
            }
        }
        return String.join("\n", collection);
    }
}
