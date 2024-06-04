package de.woody64k.services.word.model.content.elements;

import java.util.ArrayList;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ParsedTableRow extends ArrayList<String> {
    boolean filled = false;
}
