package de.woody64k.services.word.model.value.request.validate;

import de.woody64k.services.word.model.value.request.DocumentValueRequirement;
import de.woody64k.services.word.model.value.request.SearchRequirement;

public class RequestValidator {
    public static void validate(DocumentValueRequirement requirement) {
        for (SearchRequirement value : requirement.getValues()) {
            checkValue(value);
        }
    }

    private static void checkValue(SearchRequirement value) {
        if (!verify(value)) {
            if (value.getValues() != null) {
                for (SearchRequirement subValue : value.getValues()) {
                    checkValue(subValue);
                }
            }
        } else {
            throw new RuntimeException(String.format("Search for '%s' is invalid. It contains substrauture and value Mapping.", value.getSearchTerm()));
        }
    }

    private static boolean verify(SearchRequirement value) {
        return (value.getTransform() != null && value.getValues() != null);
    }
}
