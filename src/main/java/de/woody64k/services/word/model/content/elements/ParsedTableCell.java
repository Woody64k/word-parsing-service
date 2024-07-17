package de.woody64k.services.word.model.content.elements;

import de.woody64k.services.word.model.content.WordContent;
import lombok.Data;

@Data
public class ParsedTableCell {
    private String text;
    private WordContent complex;

    public ParsedTableCell(String text) {
        super();
        this.text = text;
    }

    public ParsedTableCell(WordContent complex) {
        super();
        this.complex = complex;
    }
}
