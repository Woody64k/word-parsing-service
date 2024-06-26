package de.woody64k.services.word.model.value.request;

import de.woody64k.services.word.model.value.request.transform.ListTransformRequirement;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ListRequirement extends ValueRequirements {
    String resultName;
    ListTransformRequirement transform;
}
