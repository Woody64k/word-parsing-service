package de.woody64k.services.document.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import de.woody64k.services.document.model.value.request.transform.FilterTransform;
import de.woody64k.services.document.model.value.response.GenericObject;
import de.woody64k.services.document.transform.filter.EmptyChecker;
import de.woody64k.services.document.transform.filter.EqualChecker;

public class FilterPredicateHelper {

    public static Predicate<GenericObject> getLambda(FilterTransform rule) {
        if (rule.getValue() != null) {
            return createValuePredicate(rule);
        } else {
            List<Predicate<GenericObject>> lambas = new ArrayList<>();
            for (FilterTransform subRule : rule.getAllSubRules()) {
                lambas.add(getLambda(subRule));
            }
            return combineLambda(lambas, rule.getAnd() != null);
        }
    }

    private static Predicate<GenericObject> combineLambda(List<Predicate<GenericObject>> lambas, boolean and) {
        Predicate<GenericObject> resultLambda = neutralPredicate();
        boolean first = true;
        for (Predicate<GenericObject> lambda : lambas) {
            if (first) {
                // overwrite neutral predicate to avoid issues with or
                resultLambda = lambda;
                first = false;
            } else {
                if (and) {
                    resultLambda = resultLambda.and(lambda);
                } else {
                    resultLambda = resultLambda.or(lambda);
                }
            }
        }
        return resultLambda;
    }

    private static Predicate<GenericObject> createValuePredicate(FilterTransform rule) {
        if (rule.getIsEmpty() != null) {
            return new EmptyChecker(rule);
        } else if (rule.getEquals() != null) {
            return new EqualChecker(rule);
        } else {
            // TODO: add implementation for like
            return neutralPredicate();
        }
    }

    private static Predicate<GenericObject> neutralPredicate() {
        return t -> true;
    }
}
