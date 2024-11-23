package de.woody64k.services.document.model.content.elements;

import java.util.ArrayList;

import de.woody64k.services.document.model.content.DocumentContent;
import de.woody64k.services.document.util.Checker;
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

    public void add(DocumentContent content) {
        // FR-10: unpack documents to simple string if possible.
        if (content.isOnlyText()) {
            super.add(content.flattenToString());
        } else {
            super.add(content);
        }
    }

    public boolean isBlank() {
        // FR-13: Cleanup artifacts in pdf
        for (Object cell : this) {
            if (Checker.isNotEmpty(cell)) {
                // Something in row
                return false;
            }
        }
        // full row only contains blank
        return true;
    }
}