package de.woody64k.services.document.model.value.request.transform;

import lombok.Data;

@Data
public class ListTransformRequirement {
    MergeTransform merge;
    FilterTransform filter;
    String[] orderBy;
}
