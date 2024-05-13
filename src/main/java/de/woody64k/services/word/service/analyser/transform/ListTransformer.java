package de.woody64k.services.word.service.analyser.transform;

import java.util.ArrayList;
import java.util.List;

import de.woody64k.services.word.model.value.request.transform.ListTransformRequirement;
import de.woody64k.services.word.model.value.request.transform.MergeTransform.MergeObject;
import de.woody64k.services.word.model.value.response.GenericObject;

public class ListTransformer {
    public static List<GenericObject> transform(List<GenericObject> values, ListTransformRequirement requ) {
        if (requ != null) {
            return doMerge(values, requ);
        } else {
            return values;
        }
    }

    private static List<GenericObject> doMerge(List<GenericObject> values, ListTransformRequirement requ) {
        List<GenericObject> mergedData = new ArrayList<>();
        if (values != null && requ.getMerge() != null) {
            List<String> mergeKey = requ.getMerge().getBy();
            List<String> collectKey = requ.getMerge().getCollect();
            List<MergeObject> objects = requ.getMerge().getObjects();

            for (GenericObject value : values) {
                GenericObject match = findObjectInListByKey(value, mergedData, mergeKey);
                if (match == null) {
                    // no match
                    match = value.sliceOut(mergeKey);
                    mergedData.add(match);
                }
                if (collectKey != null) {
                    match.putAll(value.sliceOut(collectKey));
                }
                for (MergeObject objectMapping : objects) {
                    match.put(objectMapping.getResultName(), value.sliceOut(objectMapping.getValues()));
                }
            }
        }
        return mergedData;
    }

    public static GenericObject findObjectInListByKey(GenericObject value, List<GenericObject> mergedData, List<String> mergeKey) {
        for (GenericObject mergedResult : mergedData) {
            if (value.equalsByKey(mergedResult, mergeKey)) {
                return mergedResult;
            }
        }
        return null;
    }

}
