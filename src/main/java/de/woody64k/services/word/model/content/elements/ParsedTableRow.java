package de.woody64k.services.word.model.content.elements;

import java.util.ArrayList;

import de.woody64k.services.word.model.content.WordContent;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ParsedTableRow extends ArrayList<Object> {
    boolean filled = false;

    public void add(String text) {
        super.add(text);
    }

    public void add(WordContent content) {
        // FR-10: unpack documents to simple string if possible.
        String flatContent = content.flattenToString();
        if (flatContent != null) {
            super.add(flatContent);
        } else {
            super.add(content);
        }
    }
}