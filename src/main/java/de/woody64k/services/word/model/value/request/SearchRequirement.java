package de.woody64k.services.word.model.value.request;

import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.woody64k.services.word.model.value.request.transform.ValueTransformRequirement;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper = true)
public class SearchRequirement extends ValueRequirements {
    String searchTerm;
    boolean useRegex;
    String resultName;
    String defaultValue;
    ValueTransformRequirement transform;

    @JsonIgnore
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    Pattern regexPattern;

    public Pattern getRegexPattern() {
        if (regexPattern == null) {
            if (useRegex) {
                regexPattern = Pattern.compile(getSearchTerm());
            } else {
                String term = Pattern.quote(getSearchTerm());
                term = term.replaceAll("\\w*", "\\w*");
                regexPattern = Pattern.compile(term);
            }
        }
        return regexPattern;
    }
}
