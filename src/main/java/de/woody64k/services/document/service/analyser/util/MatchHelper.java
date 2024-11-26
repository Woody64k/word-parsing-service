package de.woody64k.services.document.service.analyser.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.woody64k.services.document.model.value.request.SearchRequirement;

public class MatchHelper {
    public static boolean matches(String text, SearchRequirement requirement) {
        if (text == null) {
            return false;
        } else {
            return matchWitRegex(text, requirement);
        }
    }

    public static boolean matchWitRegex(String text, SearchRequirement requirement) {
        if (text == null) {
            return false;
        } else {
            return requirement.getRegexPattern()
                    .matcher(text.replaceAll("\s", ""))
                    .matches();
        }
    }

    public static Matcher findWithRegex(String text, SearchRequirement requirement) {
        Pattern pattern = requirement.getRegexPattern();
        Matcher matcher = pattern.matcher(text.replaceAll("\s", ""));
        return matcher;
    }
}
