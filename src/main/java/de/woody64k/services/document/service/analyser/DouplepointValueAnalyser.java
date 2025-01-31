package de.woody64k.services.document.service.analyser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

import de.woody64k.services.document.model.content.ContentTable;
import de.woody64k.services.document.model.content.DocumentContent;
import de.woody64k.services.document.model.content.IContent;
import de.woody64k.services.document.model.content.IContent.ContentCategory;
import de.woody64k.services.document.model.content.elements.ParsedTableRow;
import de.woody64k.services.document.model.value.request.SearchRequirement;
import de.woody64k.services.document.model.value.response.GenericObject;
import de.woody64k.services.document.service.analyser.util.MatchHelper;
import de.woody64k.services.document.transform.ValueTransformer;

public class DouplepointValueAnalyser {

    public static GenericObject analyse(DocumentContent parsedData, SearchRequirement searchRequirement) {
        GenericObject result = new GenericObject();
        for (ContentTable table : parsedData.getTables()) {
            for (ParsedTableRow row : table.getTable()) {
                for (Object cell : row) {
                    GenericObject foundValues = null;
                    if (cell instanceof String) {
                        foundValues = scanCell((String) cell, searchRequirement);
                    } else if (cell instanceof DocumentContent) {
                        // FR-10: handle embedded Documents
                        foundValues = analyse((DocumentContent) cell, searchRequirement);
                    }
                    if (foundValues != null && foundValues.size() > 0) {
                        result.putAllAndFlatten(foundValues, true);
                    }
                }
            }
        }
        for (IContent textBlock : parsedData.getAllByCategory(ContentCategory.TEXT)) {
            GenericObject foundValues = scanCell(textBlock.flattenToString(), searchRequirement);
            result.putAllAndFlatten(foundValues, true);
        }
        return result;
    }

    public static GenericObject scanCell(String cell, SearchRequirement searchRequirement) {
        List<Object> result = new ArrayList<>();
        for (Matcher match = MatchHelper.findWithRegex(cell, searchRequirement); match.find();) {
            String[] foundValues = getFromText(cell, match);
            for (String foundValue : foundValues) {
                result.add(ValueTransformer.transform(foundValue.trim(), searchRequirement.getTransform()));
            }
        }
        if (result.isEmpty()) {
            return null;
        } else {
            return GenericObject.create(searchRequirement.getResultName(), result);
        }
    }

    public static String[] getFromText(String text, Matcher match) {
        String other = text.substring(match.end())
                .trim();
        String foundString = match.group();
        if (foundString.endsWith(":") || other.startsWith(":")) {
            if (other.startsWith(":")) {
                // remove :
                other = other.substring(1);
            }

            // collect next lines if they doesn't contain:
            String[] lines = other.split("\n");
            for (int i = 0; i < lines.length; i++) {
                if (lines[i].contains(":") || lines[i].isBlank()) {
                    // minumum the first line
                    return Arrays.copyOfRange(lines, 0, (i < 1) ? 1 : i);
                }
            }

            // return all if no : occurs
            return lines;
        }
        return new String[0];
    }
}
