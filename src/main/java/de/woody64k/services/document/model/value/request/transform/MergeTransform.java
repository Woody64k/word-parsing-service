package de.woody64k.services.document.model.value.request.transform;

import java.util.List;

import lombok.Data;

@Data
public class MergeTransform {
    // The merge Key
    List<String> by;
    // Single value arrays
    List<String> collect;
    // Complex value objects
    List<MergeObject> objects;

    @Data
    public static class MergeObject {
        String resultName;
        List<String> values;
        String[] orderBy;
    }
}
