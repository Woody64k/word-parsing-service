package de.woody64k.services.word.service.analyser;

import java.util.ArrayList;
import java.util.List;

import de.woody64k.services.word.model.content.ContentTable;
import de.woody64k.services.word.model.content.ContentTableRow;
import de.woody64k.services.word.model.content.WordContent;
import de.woody64k.services.word.model.value.request.SearchRequirement;
import de.woody64k.services.word.model.value.response.GenericObject;

public class DouplepointValueAnalyser {

    public static GenericObject analyse(WordContent parsedData, SearchRequirement searchRequirement) {
        GenericObject result = new GenericObject();
        for (ContentTable table : parsedData.getTables()) {
            for (ContentTableRow row : table) {
                for (String cell : row) {
                    GenericObject foundValues = scanCell(cell, searchRequirement);
                    if (foundValues != null && foundValues.size() > 0) {
                        result.putAll(foundValues);
                    }
                }
            }
        }
        return result;
    }

    public static GenericObject scanCell(String cell, SearchRequirement searchRequirement) {
        List<String> result = new ArrayList<>();
        String condition = searchRequirement.getSearchTerm();
        for (String text = cell; text.contains(condition); text = text.substring(text.indexOf(condition) + condition.length())) {
            String foundValues = findInText(text, condition);
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
                return otherTillEol.substring(otherTillEol.indexOf(":") + 1).trim();
            }
        }
        return null;
    }
}
