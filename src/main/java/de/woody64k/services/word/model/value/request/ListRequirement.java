package de.woody64k.services.word.model.value.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ListRequirement extends ValueRequirements {
    String name;
}
