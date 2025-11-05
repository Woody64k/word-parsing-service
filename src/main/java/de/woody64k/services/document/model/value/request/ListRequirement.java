package de.woody64k.services.document.model.value.request;

import de.woody64k.services.document.model.value.request.transform.TableTransformRequirement;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ListRequirement extends ValueRequirements {
    String resultName;
    TableTransformRequirement transform;
}
