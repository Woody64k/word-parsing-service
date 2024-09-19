package de.woody64k.services.word.service.analyser;

import de.woody64k.services.word.model.content.ContentTable;
import de.woody64k.services.word.model.content.WordContent;
import de.woody64k.services.word.model.content.elements.ParsedTableRow;
import de.woody64k.services.word.model.value.request.SearchRequirement;
import de.woody64k.services.word.model.value.response.GenericObject;
import de.woody64k.services.word.service.analyser.transform.ValueTransformer;
import de.woody64k.services.word.service.analyser.util.MatchHelper;

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
                if (row.get(0) instanceof String && lastLineMatch && ((String) row.get(0)).isBlank()) {
                    // Handle merged cells
                    // @implements FR-03
                    row.set(0, searchRequirement.getSearchTerm());
                } else {
                    lastLineMatch = false;
                }
                GenericObject foundValues = scanRow(searchRequirement, row);
                if (foundValues != null && foundValues.size() > 0) {
                    result.putAllAndFlatten(foundValues, true);
                    lastLineMatch = true;
                }
            }
        }
        return result;
    }

    private static GenericObject scanRow(SearchRequirement searchRequirement, ParsedTableRow row) {
        boolean firstColumn = true;
        GenericObject results = new GenericObject();
        for (Object cell : row) {
            String value;
            if (cell instanceof String) {
                value = (String) cell;
                // first column matches
                if (firstColumn) {
                    if (MatchHelper.matches(value, searchRequirement)) {
                        // match
                        firstColumn = false;
                    } else {
                        // no match (abort)
                        return null;
                    }
                } else {
                    // if matches
                    if (!value.isBlank()) {
                        // collect values
                        Object foundInformation = ValueTransformer.transform(value, searchRequirement.getTransform());
                        Object result = SubvalueScanner.scannForSubvalues(foundInformation, searchRequirement.getValues());
                        results.putAndFlatten(searchRequirement.getResultName(), result, true);
                    }
                }
            } else if (cell instanceof WordContent) {
                // FR-10: handle embedded Documents
                results.putAndFlatten(null, analyse((WordContent) cell, searchRequirement), false);
            }
        }
        return results;
    }
}
