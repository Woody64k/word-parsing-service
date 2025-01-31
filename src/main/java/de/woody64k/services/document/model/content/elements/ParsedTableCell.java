package de.woody64k.services.document.model.content.elements;

import de.woody64k.services.document.model.content.DocumentContent;
import lombok.Data;

@Data
public class ParsedTableCell {
    private String text;
    private DocumentContent complex;

    public ParsedTableCell(String text) {
        super();
        this.text = text;
    }

    public ParsedTableCell(DocumentContent complex) {
        super();
        this.complex = complex;
    }
}
