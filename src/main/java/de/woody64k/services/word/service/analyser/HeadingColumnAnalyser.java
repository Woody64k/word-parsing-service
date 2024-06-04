package de.woody64k.services.word.service.analyser;

import java.util.ArrayList;
import java.util.List;

import de.woody64k.services.word.model.content.ContentTable;
import de.woody64k.services.word.model.content.WordContent;
import de.woody64k.services.word.model.content.elements.ParsedTableRow;
import de.woody64k.services.word.model.value.request.SearchRequirement;
import de.woody64k.services.word.model.value.response.GenericObject;
import de.woody64k.services.word.service.analyser.transform.ValueTransformer;

/**
 * Parses Tables with a heading column.
 * 
 * @implements FR-01
 */
public class HeadingColumnAnalyser {

    public static GenericObject analyse(WordContent parsedData, SearchRequirement searchRequirement) {
        GenericObject result = new GenericObject();
        for (ContentTable table : parsedData.getTables()) {
            boolean lastLineMatch = false;
            for (ParsedTableRow row : table.getTable()) {
                if (lastLineMatch && row.get(0).isBlank()) {
                    // Handle merged cells
                    // @implements FR-03
                    row.set(0, searchRequirement.getSearchTerm());
                } else {
                    lastLineMatch = false;
                }
                GenericObject foundValues = scanRow(searchRequirement, row);
                if (foundValues != null && foundValues.size() > 0) {
                    result.putAll(foundValues);
                    lastLineMatch = true;
                }
            }
        }
        return result;
    }

    private static GenericObject scanRow(SearchRequirement searchRequirement, ParsedTableRow row) {
        boolean firstColumn = true;
        List<Object> results = new ArrayList<>();
        for (String cell : row) {
            // first column matches
            if (firstColumn) {
                if (cell.equalsIgnoreCase(searchRequirement.getSearchTerm())) {
                    // match
                    firstColumn = false;
                } else {
                    // no match (abort)
                    return null;
                }
            } else {
                // if matches
                if (!cell.isBlank()) {
                    // collect values
                    // @implements: FR-02
                    Object foundInformation = ValueTransformer.transform(cell, searchRequirement.getTransform());
                    Object result = SubvalueScanner.scannForSubvalues(foundInformation, searchRequirement.getValues());
                    results.add(result);
                }
            }
        }
        return GenericObject.create(searchRequirement.getResultName(), results);
    }

}
