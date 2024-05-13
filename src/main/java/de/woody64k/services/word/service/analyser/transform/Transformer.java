package de.woody64k.services.word.service.analyser.transform;

import java.util.Map.Entry;

import de.woody64k.services.word.model.value.request.transform.SplitTransform.SplitAs;
import de.woody64k.services.word.model.value.request.transform.ValueTransformRequirement;
import de.woody64k.services.word.model.value.response.GenericObject;
import de.woody64k.services.word.util.Trimmer;

public class Transformer {
    public static Object transform(String value, ValueTransformRequirement requ) {
        if (requ != null) {
            return doSplit(value, requ);
        } else {
            return value;
        }
    }

    /**
     * @implements FR-08, FR-09
     * @param value
     * @param requ
     * @return
     */
    public static Object doSplit(String value, ValueTransformRequirement requ) {
        if (value != null && requ.getSplit() != null) {
            String[] splittedValue = value.split(requ.getSplit().getBy());
            if (requ.getSplit().getAs() == SplitAs.list) {
                // @implements: FR-08
                return Trimmer.trimmAll(splittedValue);
            } else {
                // @implements: FR-09
                GenericObject splitResult = new GenericObject();
                for (Entry<Integer, String> values : requ.getSplit().getValues().entrySet()) {
                    if (values.getKey() <= splittedValue.length && values.getKey() >= 1) {
                        splitResult.put(values.getValue(), splittedValue[values.getKey() - 1].trim());
                    }
                }
                return splitResult;
            }
        }
        return value;
    }
}
