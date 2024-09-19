package de.woody64k.services.word.service.analyser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import de.woody64k.services.word.model.content.ContentTable;
import de.woody64k.services.word.model.content.WordContent;
import de.woody64k.services.word.model.content.elements.ParsedTableRow;
import de.woody64k.services.word.model.value.request.SearchRequirement;
import de.woody64k.services.word.model.value.response.GenericObject;
import de.woody64k.services.word.service.analyser.transform.ValueTransformer;
import de.woody64k.services.word.service.analyser.util.MatchHelper;

public class DouplepointValueAnalyser {

    public static GenericObject analyse(WordContent parsedData, SearchRequirement searchRequirement) {
        GenericObject result = new GenericObject();
        for (ContentTable table : parsedData.getTables()) {
            for (ParsedTableRow row : table.getTable()) {
                for (Object cell : row) {
                    GenericObject foundValues = null;
                    if (cell instanceof String) {
                        foundValues = scanCell((String) cell, searchRequirement);
                    } else if (cell instanceof WordContent) {
                        // FR-10: handle embedded Documents
                        foundValues = analyse((WordContent) cell, searchRequirement);
                    }
                    if (foundValues != null && foundValues.size() > 0) {
                        result.putAllAndFlatten(foundValues, true);
                    }
                }
            }
        }
        return result;
    }

    public static GenericObject scanCell(String cell, SearchRequirement searchRequirement) {
        List<Object> result = new ArrayList<>();
        for (Matcher match = MatchHelper.findWithRegex(cell, searchRequirement); match.find();) {
            Object foundValues = ValueTransformer.transform(getFromText(cell, match), searchRequirement.getTransform());
            if (foundValues != null) {
                result.add(foundValues);
            }
        }
        if (result.isEmpty()) {
            return null;
        } else {
            return GenericObject.create(searchRequirement.getResultName(), result);
        }
    }

    public static String getFromText(String text, Matcher match) {
        String other = text.substring(match.end())
                .trim();
        String foundString = match.group();
        if (foundString.endsWith(":") || other.startsWith(":")) {
            String result = other.contains("\n") ? other.substring(0, other.indexOf("\n")) : other;
            if (!result.isBlank()) {
                return result;
            }
        }
        return null;
    }
}
