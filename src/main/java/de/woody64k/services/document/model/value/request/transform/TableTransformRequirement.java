package de.woody64k.services.document.model.value.request.transform;

import lombok.Data;

@Data
public class TableTransformRequirement {
    MergeTableTransform merge;
    FilterTransform filter;
    String[] orderBy;
}
