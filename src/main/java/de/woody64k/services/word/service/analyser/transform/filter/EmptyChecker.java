package de.woody64k.services.word.service.analyser.transform.filter;

import java.util.function.Predicate;

import de.woody64k.services.word.model.value.request.transform.FilterTransform;
import de.woody64k.services.word.model.value.response.GenericObject;
import de.woody64k.services.word.util.Checker;
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
