package de.woody64k.services.document.service.analyser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.woody64k.services.document.model.content.ContentTable;
import de.woody64k.services.document.model.content.DocumentContent;
import de.woody64k.services.document.model.content.elements.ParsedTableRow;
import de.woody64k.services.document.model.value.request.ListRequirement;
import de.woody64k.services.document.model.value.request.SearchRequirement;
import de.woody64k.services.document.model.value.response.GenericObject;
import de.woody64k.services.document.service.analyser.util.MatchHelper;
import de.woody64k.services.document.transform.ListTransformer;
import de.woody64k.services.document.transform.ValueTransformer;

public class HeadingRowAnalyser {

    public static List<GenericObject> analyse(DocumentContent parsedData, ListRequirement listRequirement) {
        List<GenericObject> result = new ArrayList<>();
        for (ContentTable table : parsedData.getTables()) {
            List<GenericObject> foundValues = scannTable(listRequirement, table);
            if (foundValues != null && foundValues.size() > 0) {
                result.addAll(ListTransformer.transform(foundValues, listRequirement.getTransform()));
            }
        }
        return result;
    }

    public static List<GenericObject> scannTable(ListRequirement listRequirement, ContentTable table) {
        boolean firstRow = true;
        int sizeFirstRow = 0;
        List<GenericObject> result = new ArrayList<>();
        Map<String, Integer> matches = new HashMap<>();
        for (ParsedTableRow row : table.getTable()) {
            if (firstRow) {
                matches = scannHeader(row, listRequirement);
                if (matches.size() != listRequirement.getValues()
                        .size()) {
                    if (listRequirement.getIgnoreUnknownHeader() == null) {
                        // FR-06
                        // Abort if no match
                        return null;
                    } else {
                        if ((matches.size() + listRequirement.getIgnoreUnknownHeader()) < listRequirement.getValues()
                                .size()) {
                            // FR-06
                            // Abort if no match
                            return null;
                        }
                    }
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

    public static GenericObject readLine(Map<String, Integer> matches, ParsedTableRow row, ListRequirement listRequirement) {
        GenericObject oneRow = new GenericObject();
        // read Values
        for (SearchRequirement valueRequ : listRequirement.getValues()) {
            if (matches.containsKey(valueRequ.getResultName())) {
                Integer matchColumn = matches.get(valueRequ.getResultName());
                String text = (String) row.get(matchColumn);
                oneRow.putAndFlatten(valueRequ.getResultName(), ValueTransformer.transform(text, valueRequ.getTransform()), false);
            }
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
    private static Map<String, Integer> scannHeader(ParsedTableRow row, ListRequirement listRequirement) {
        Map<String, Integer> tableMap = new HashMap<>();
        for (SearchRequirement requirement : listRequirement.getValues()) {
            int pos = findInRow(row, tableMap, requirement);
            if (pos >= 0) {
                tableMap.put(requirement.getResultName(), pos);
            }
        }
        return tableMap;
    }

    public static int findInRow(ParsedTableRow row, Map<String, Integer> tableMap, SearchRequirement requirement) {
        for (int i = 0; i < row.size(); i++) {
            if (row.get(i) instanceof String && MatchHelper.matches((String) row.get(i), requirement))
                return i;
        }
        // not found
        return -1;
    }
}
