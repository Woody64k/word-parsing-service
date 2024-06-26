package de.woody64k.services.word.service.analyser.transform.filter;

import java.util.function.Predicate;

import de.woody64k.services.word.model.value.request.transform.FilterTransform;
import de.woody64k.services.word.model.value.response.GenericObject;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EqualChecker implements Predicate<GenericObject> {
    private FilterTransform rule;

    @Override
    public boolean test(GenericObject t) {
        Object value = t.get(rule.getValue());
        if (value == null) {
            return false;
        } else {
            if (value instanceof String) {
                return ((String) value).contentEquals(rule.getEquals());
            }
        }
        return false;
    }
}
