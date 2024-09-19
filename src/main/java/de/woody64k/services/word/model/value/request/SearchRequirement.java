package de.woody64k.services.word.model.value.request;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
                regexPattern = Pattern.compile(getSearchTerm(), Pattern.CASE_INSENSITIVE);
            } else {
                List<String> parts = Arrays.asList(getSearchTerm().split("\\s+"));
                String regex = parts.stream()
                        .map(p -> Pattern.quote(p))
                        .collect(Collectors.joining("\\s+"));
                regexPattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            }
        }
        return regexPattern;
    }
}
