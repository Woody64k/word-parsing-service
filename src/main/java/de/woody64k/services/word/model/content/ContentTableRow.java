package de.woody64k.services.word.model.content;

import java.util.ArrayList;

import lombok.Data;

@Data
public class ContentTableRow extends ArrayList<String> {
    boolean filled = false;
}
