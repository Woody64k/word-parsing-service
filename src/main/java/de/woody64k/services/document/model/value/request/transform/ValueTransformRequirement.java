package de.woody64k.services.document.model.value.request.transform;

import lombok.Data;

@Data
public class ValueTransformRequirement {
    SplitTransform split;

    // Only when multiple values are found.
    ConcatListTransform concatFindings;
    SubStringTransform subString;
}
