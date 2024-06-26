package de.woody64k.services.word.service.analyser.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import de.woody64k.services.word.model.value.request.transform.FilterTransform;
import de.woody64k.services.word.model.value.request.transform.ListTransformRequirement;
import de.woody64k.services.word.model.value.request.transform.MergeTransform;
import de.woody64k.services.word.model.value.request.transform.MergeTransform.MergeObject;
import de.woody64k.services.word.model.value.response.GenericObject;

public class ListTransformer {
    public static List<GenericObject> transform(List<GenericObject> values, ListTransformRequirement requ) {
        if (requ != null) {
            List<GenericObject> result = doFilter(values, requ.getFilter());
            result = doMerge(result, requ.getMerge());
            return result;
        } else {
            return values;
        }
    }

    private static List<GenericObject> doFilter(List<GenericObject> objects, FilterTransform filter) {
        if (filter == null) {
            return objects;
        } else {
            return objects.stream().filter(FilterPredicateHelper.getLambda(filter)).collect(Collectors.toList());
        }
    }

    private static List<GenericObject> doMerge(List<GenericObject> values, MergeTransform mergeTransform) {
        List<GenericObject> mergedData = new ArrayList<>();
        if (values != null && mergeTransform != null) {
            List<String> mergeKey = mergeTransform.getBy();
            List<String> collectKey = mergeTransform.getCollect();
            List<MergeObject> objects = mergeTransform.getObjects();

            for (GenericObject value : values) {
                GenericObject match = findObjectInListByKey(value, mergedData, mergeKey);
                if (match == null) {
                    // no match
                    match = value.sliceOut(mergeKey);
                    mergedData.add(match);
                }
                if (collectKey != null) {
                    match.putAllAndFlatten(tranformToListForm(value.sliceOut(collectKey)), false);
                }
                if (objects != null) {
                    for (MergeObject objectMapping : objects) {
                        match.putAndFlatten(objectMapping.getResultName(), List.of(value.sliceOut(objectMapping.getValues())), false);
                    }
                }
            }
        }
        return mergedData;
    }

    private static GenericObject tranformToListForm(GenericObject sliceOut) {
        for (Entry<String, Object> entry : sliceOut.entrySet()) {
            if (entry.getValue() != null && !(entry.getValue() instanceof Collection)) {
                entry.setValue(List.of(entry.getValue()));
            }
        }
        return sliceOut;
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
