package de.woody64k.services.word.service.analyser.transform.filter;

import java.util.Collection;
import java.util.function.Predicate;

import de.woody64k.services.word.model.value.request.transform.FilterTransform;
import de.woody64k.services.word.model.value.response.GenericObject;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EmptyChecker implements Predicate<GenericObject> {
    private FilterTransform rule;

    @Override
    public boolean test(GenericObject t) {
        Object value = t.get(rule.getValue());
        if (value == null) {
            return rule.getIsEmpty();
        } else {
            if (value instanceof String) {
                return ((String) value).isBlank() == rule.getIsEmpty();
            } else if (value instanceof Collection) {
                return ((Collection) value).isEmpty() == rule.getIsEmpty();
            } else if (value instanceof GenericObject) {
                return !rule.getIsEmpty();
            }
        }
        return false;
    }
}
