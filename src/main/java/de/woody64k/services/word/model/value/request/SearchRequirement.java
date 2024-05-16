package de.woody64k.services.word.model.value.request;

import de.woody64k.services.word.model.value.request.transform.ValueTransformRequirement;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SearchRequirement extends ValueRequirements {
    String searchTerm;
    String resultName;
    ValueTransformRequirement transform;
}
