package de.woody64k.services.word.model.content.elements;

import java.util.ArrayList;

import de.woody64k.services.word.model.content.WordContent;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ParsedTableRow extends ArrayList<Object> {
    private static final long serialVersionUID = 1L;
    boolean filled = false;

    public void add(String text) {
        super.add(text);
    }

    public void add(WordContent content) {
        // FR-10: unpack documents to simple string if possible.
        if (content.isOnlyText()) {
            super.add(content.flattenToString());
        } else {
            super.add(content);
        }
    }
}