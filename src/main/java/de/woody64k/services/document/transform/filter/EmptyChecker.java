package de.woody64k.services.document.transform.filter;

import java.util.function.Predicate;

import de.woody64k.services.document.model.value.request.transform.FilterTransform;
import de.woody64k.services.document.model.value.response.GenericObject;
import de.woody64k.services.document.util.Checker;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EmptyChecker implements Predicate<GenericObject> {
    private FilterTransform rule;

    @Override
    public boolean test(GenericObject t) {
        Object value = t.get(rule.getValue());
        if (Checker.isEmpty(value)) {
            return rule.getIsEmpty();
        } else {
            return !rule.getIsEmpty();
        }
    }
}
