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

    public static Object doSplit(String value, ValueTransformRequirement requ) {
        if (value != null && requ.getSplit() != null) {
            String[] splittedValue = value.split(requ.getSplit().getBy());
            if (requ.getSplit().getAs() == SplitAs.list) {
                return Trimmer.trimmAll(splittedValue);
            } else {
                GenericObject splitResult = new GenericObject();
                for (Entry<Integer, String> values : requ.getSplit().getValues().entrySet()) {
                    if (values.getKey() <= splittedValue.length) {
                        splitResult.put(values.getValue(), splittedValue[values.getKey() - 1]);
                    }
                }
                return splitResult;
            }
        }
        return value;
    }
}
