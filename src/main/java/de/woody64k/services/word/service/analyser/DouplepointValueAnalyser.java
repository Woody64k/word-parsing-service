package de.woody64k.services.word.service.analyser;

import java.util.ArrayList;
import java.util.List;

import de.woody64k.services.word.model.content.ContentTable;
import de.woody64k.services.word.model.content.WordContent;
import de.woody64k.services.word.model.content.elements.ParsedTableRow;
import de.woody64k.services.word.model.value.request.SearchRequirement;
import de.woody64k.services.word.model.value.response.GenericObject;
import de.woody64k.services.word.service.analyser.transform.ValueTransformer;

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
        String condition = searchRequirement.getSearchTerm();
        for (String text = cell; text.contains(condition); text = text.substring(text.indexOf(condition) + condition.length())) {
            Object foundValues = ValueTransformer.transform(findInText(text, condition), searchRequirement.getTransform());
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

    public static String findInText(String text, String condition) {
        if (text.contains(condition)) {
            String other = text.substring(text.indexOf(condition) + condition.length());
            String otherTillEol = other.contains("\n") ? other.substring(0, other.indexOf("\n")) : other;
            if (otherTillEol.contains(":")) {
                return otherTillEol.substring(otherTillEol.indexOf(":") + 1)
                        .trim();
            }
        }
        return null;
    }
}
