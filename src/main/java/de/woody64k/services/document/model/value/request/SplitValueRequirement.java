package de.woody64k.services.document.model.value.request;

import de.woody64k.services.document.model.value.request.transform.ValueTransformRequirement;
import lombok.Data;

@Data
public class SplitValueRequirement {
    Integer position;
    Integer fromPosition;
    String resultName;
    ValueTransformRequirement transform;
}
