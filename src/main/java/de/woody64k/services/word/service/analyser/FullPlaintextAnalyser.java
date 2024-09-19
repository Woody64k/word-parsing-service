package de.woody64k.services.word.service.analyser;

import java.util.regex.Matcher;

import de.woody64k.services.word.model.content.WordContent;
import de.woody64k.services.word.model.value.request.SearchRequirement;
import de.woody64k.services.word.model.value.response.GenericObject;
import de.woody64k.services.word.service.analyser.util.MatchHelper;

/**
 * Parses Tables with a heading column.
 * 
 * @implements FR-01
 */
public class FullPlaintextAnalyser {

    public static GenericObject analyse(WordContent parsedData, SearchRequirement searchRequirement) {
        GenericObject result = new GenericObject();
        if (searchRequirement.getReadAllTill() != null) {
            String contentText = parsedData.flattenToString();
            for (Matcher match = MatchHelper.findWithRegex(contentText, searchRequirement); match.find();) {
                String fromMatch = contentText.substring(match.end());
                Matcher endMatch = searchRequirement.getRegexTill()
                        .matcher(fromMatch);
                if (endMatch.find()) {
                    result.put(searchRequirement.getResultName(), fromMatch.substring(0, endMatch.start())
                            .trim());
                }
            }
        }
        return result;
    }
}
