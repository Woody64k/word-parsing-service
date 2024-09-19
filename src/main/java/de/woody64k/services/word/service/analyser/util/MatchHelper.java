package de.woody64k.services.word.service.analyser.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.woody64k.services.word.model.value.request.SearchRequirement;

public class MatchHelper {
    public static boolean matches(String text, SearchRequirement requirement) {
        if (text == null) {
            return false;
        } else {
            return text.equalsIgnoreCase(requirement.getSearchTerm());
        }
    }

    public static boolean matchWitRegex(String text, SearchRequirement requirement) {
        if (text == null) {
            return false;
        } else {
            return text.matches(requirement.getSearchTerm());
        }
    }

    public static int findWithRegex(String text, SearchRequirement requirement) {
        Pattern pattern = requirement.getRegexPattern();
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.start();
        } else {
            return -1;
        }
    }
}
