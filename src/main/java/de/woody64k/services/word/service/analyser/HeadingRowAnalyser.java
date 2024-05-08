package de.woody64k.services.word.service.analyser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.woody64k.services.word.model.content.ContentTable;
import de.woody64k.services.word.model.content.ContentTableRow;
import de.woody64k.services.word.model.content.WordContent;
import de.woody64k.services.word.model.value.request.ListRequirement;
import de.woody64k.services.word.model.value.request.SearchRequirement;
import de.woody64k.services.word.model.value.response.GenericObject;
import de.woody64k.services.word.service.analyser.transform.Transformer;

public class HeadingRowAnalyser {

    public static List<GenericObject> analyse(WordContent parsedData, ListRequirement listRequirement) {
        List<GenericObject> result = new ArrayList<>();
        for (ContentTable table : parsedData.getTables()) {
            List<GenericObject> foundValues = scannTable(listRequirement, table);
            if (foundValues != null && foundValues.size() > 0) {
                result.addAll(foundValues);
            }
        }
        return result;
    }

    public static List<GenericObject> scannTable(ListRequirement listRequirement, ContentTable table) {
        boolean firstRow = true;
        int sizeFirstRow = 0;
        List<GenericObject> result = new ArrayList<>();
        Map<String, Integer> matches = new HashMap<>();
        for (ContentTableRow row : table) {
            if (firstRow) {
                matches = scannHeader(row, listRequirement);
                if (matches.size() != listRequirement.getValues().size()) {
                    // FR-06
                    // Abort if no match
                    return null;
                }
                firstRow = false;
                sizeFirstRow = row.size();
            } else {
                if (row.size() >= sizeFirstRow) {
                    result.add(readLine(matches, row, listRequirement));
                }
            }
        }
        return result;
    }

    public static GenericObject readLine(Map<String, Integer> matches, ContentTableRow row, ListRequirement listRequirement) {
        GenericObject oneRow = new GenericObject();
        // read Values
        for (SearchRequirement valueRequ : listRequirement.getValues()) {
            Integer matchColumn = matches.get(valueRequ.getResultName());
            oneRow.put(valueRequ.getResultName(), Transformer.transform(row.get(matchColumn), valueRequ.getTransform()));
        }
        return oneRow;
    }

    /**
     * Matches Header and notes the index of the column.
     * 
     * @implements FR-06
     * @param row
     * @param listRequirement
     * @return Map matching the resultName from SearchRequirement with the index of
     *         the column.
     */
    private static Map<String, Integer> scannHeader(ContentTableRow row, ListRequirement listRequirement) {
        Map<String, Integer> tableMap = new HashMap<>();
        for (SearchRequirement requirement : listRequirement.getValues()) {
            for (int i = 0; i < row.size(); i++) {
                if (row.get(i).equalsIgnoreCase(requirement.getSearchTerm())) {
                    tableMap.put(requirement.getResultName(), i);
                }
            }
        }
        return tableMap;
    }
}
