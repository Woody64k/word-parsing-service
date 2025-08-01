package de.woody64k.services.document.model.value.request;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.woody64k.services.document.model.value.request.transform.ValueTransformRequirement;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper = true)
public class SearchRequirement extends ValueRequirements {
    private String searchTerm;
    private boolean useRegex;
    private String readAllTill;
    private String resultName;
    private String defaultValue;
    private ValueTransformRequirement transform;

    @JsonIgnore
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    private Pattern regexPattern;

    public Pattern getRegexPattern() {
        if (regexPattern == null) {
            regexPattern = buildRegex(searchTerm);
        }
        return regexPattern;
    }

    public Pattern getRegexTill() {
        return buildRegex(readAllTill);
    }

    public Pattern buildRegex(String value) {
        if (useRegex) {
            return Pattern.compile(value.replaceAll("\s+", "\\s+"), Pattern.CASE_INSENSITIVE);
        } else {
            List<String> parts = Arrays.asList(value.split("\s+"));
            String regex = parts.stream()
                    .map(p -> Pattern.quote(p))
                    .collect(Collectors.joining("\\s+"));
            return Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        }
    }
}
